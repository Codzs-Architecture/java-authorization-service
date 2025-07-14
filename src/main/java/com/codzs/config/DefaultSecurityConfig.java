/*
 * Copyright 2020-2023 Nitin Khaitan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codzs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.codzs.oauth2.authentication.federation.FederatedIdentityAuthenticationSuccessHandler;

import java.util.Map;

/**
 * Configuration class for default security settings.
 * This class handles the configuration of security filter chains,
 * user details service, and authentication providers.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class DefaultSecurityConfig {

	// @formatter:off
	@Bean
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http
			.securityMatcher("/assets/**", "/login")
			.authorizeHttpRequests(authorize ->
				authorize
					.requestMatchers("/assets/**", "/login").permitAll()
					.anyRequest().authenticated()
			)
			.formLogin(formLogin ->
				formLogin
					.loginPage("/login")
			)
			.oauth2Login(oauth2Login ->
				oauth2Login
					.loginPage("/login")
					.successHandler(authenticationSuccessHandler())
			)
			.cors(Customizer.withDefaults());

		return http.build();
	}
	// @formatter:on

	private AuthenticationSuccessHandler authenticationSuccessHandler() {
		return new FederatedIdentityAuthenticationSuccessHandler();
	}

	/**
	 * Configure the UserDetailsService to fetch users from database.
	 * This service loads user details from the 'users' and 'authorities' tables.
	 * 
	 * @param jdbcTemplate the JDBC template for database operations
	 * @param passwordEncoder the password encoder for password validation
	 * @return UserDetailsService that loads users from database
	 */
	@Bean
	public UserDetailsService userDetailsService(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
		return new JdbcUserDetailsService(jdbcTemplate, passwordEncoder);
	}

	/**
	 * Configure the password encoder for password hashing and validation.
	 * Uses BCrypt for secure password storage and validation.
	 * 
	 * @return PasswordEncoder using BCrypt
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		Map<String, PasswordEncoder> encoders = new HashMap<>();
		encoders.put("bcrypt", new BCryptPasswordEncoder());
		// NoOpPasswordEncoder removed for security - all passwords must use bcrypt
		return new org.springframework.security.crypto.password.DelegatingPasswordEncoder("bcrypt", encoders);
	}

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

	@Bean
	@Order(0)  // Highest priority for management endpoints
	public SecurityFilterChain managementSecurityFilterChain(HttpSecurity http) throws Exception {
		http
			.securityMatcher("/management/**", "/actuator/**")
			.authorizeHttpRequests(authz -> authz
				.requestMatchers("/actuator/health", "/actuator/info").permitAll()  // Only health and info are public
				.requestMatchers("/management/**", "/actuator/**").authenticated()  // All other endpoints require authentication
				.anyRequest().authenticated()
			)
			.httpBasic(Customizer.withDefaults())  // Enable HTTP Basic Auth for management endpoints
			.csrf(csrf -> csrf.disable());

		return http.build();
	}

	/**
	 * JDBC-based UserDetailsService implementation.
	 * Fetches user details from the database using the 'users' and 'authorities' tables.
	 */
	private static class JdbcUserDetailsService implements UserDetailsService {
		
		private final JdbcTemplate jdbcTemplate;
		private final PasswordEncoder passwordEncoder;

		public JdbcUserDetailsService(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
			this.jdbcTemplate = jdbcTemplate;
			this.passwordEncoder = passwordEncoder;
		}

		@Override
		public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
			// Query user from database
			String sql = "SELECT username, password, enabled FROM users WHERE username = ?";
			
			try {
				UserDetails user = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
					String dbUsername = rs.getString("username");
					String rawPassword = rs.getString("password");
					boolean enabled = rs.getBoolean("enabled");
					
					// Query user authorities
					String authoritiesSql = "SELECT authority FROM authorities WHERE username = ?";
					var authorities = jdbcTemplate.queryForList(authoritiesSql, String.class, dbUsername);
					
					// Convert authorities to Spring Security format
					var grantedAuthorities = authorities.stream()
						.map(authority -> {
							// Ensure authority starts with ROLE_ if it doesn't already
							if (!authority.startsWith("ROLE_")) {
								return "ROLE_" + authority;
							}
							return authority;
						})
						.map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
						.toList();
					
					// Create UserDetails with proper password handling
					return User.builder()
						.username(dbUsername)
						.password(rawPassword) // Keep the original password format from database
						.disabled(!enabled)
						.authorities(grantedAuthorities)
						.build();
				}, username);
				
				if (user == null) {
					throw new UsernameNotFoundException("User not found: " + username);
				}
				
				return user;
				
			} catch (Exception e) {
				throw new UsernameNotFoundException("Error loading user: " + username, e);
			}
		}
	}
}
