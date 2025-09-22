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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.codzs.constants.OAuth2Constants;
import com.codzs.oauth2.authentication.federation.FederatedIdentityAuthenticationSuccessHandler;

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
			.securityMatcher(OAuth2Constants.Resources.ASSETS, OAuth2Constants.Endpoints.LOGIN)
			.authorizeHttpRequests(authorize ->
				authorize
					.requestMatchers(OAuth2Constants.Resources.ASSETS, OAuth2Constants.Endpoints.LOGIN).permitAll()
					.anyRequest().authenticated()
			)
			.formLogin(formLogin ->
				formLogin
					.loginPage(OAuth2Constants.Endpoints.LOGIN)
			)
			.oauth2Login(oauth2Login ->
				oauth2Login
					.loginPage(OAuth2Constants.Endpoints.LOGIN)
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
	 * The JdbcUserDetailsService is automatically configured as a @Service component.
	 * 
	 * Note: This bean method is no longer needed as JdbcUserDetailsService is now
	 * a standalone @Service component that will be auto-detected by Spring.
	 */

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

	/**
	 * Configure the DaoAuthenticationProvider for username/password authentication.
	 * This provider uses the UserDetailsService to load user details and validates
	 * passwords using the configured PasswordEncoder.
	 * 
	 * @param userDetailsService the service to load user details from database
	 * @param passwordEncoder the encoder to validate passwords
	 * @return DaoAuthenticationProvider configured with database-backed user details
	 */
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService, 
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder);
		return authProvider;
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
			.securityMatcher(OAuth2Constants.Management.MANAGEMENT, OAuth2Constants.Management.ACTUATOR)
			.authorizeHttpRequests(authz -> authz
				.requestMatchers(OAuth2Constants.Management.HEALTH, OAuth2Constants.Management.INFO).permitAll()  // Only health and info are public
				.requestMatchers(OAuth2Constants.Management.MANAGEMENT, OAuth2Constants.Management.ACTUATOR).authenticated()  // All other endpoints require authentication
				.anyRequest().authenticated()
			)
			.httpBasic(Customizer.withDefaults())  // Enable HTTP Basic Auth for management endpoints
			.csrf(csrf -> csrf.disable());

		return http.build();
	}

}
