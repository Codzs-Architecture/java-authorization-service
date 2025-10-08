/*
 * Copyright 2020-2024 Nitin Khaitan.
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
package com.codzs.config.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import static org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer.authorizationServer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import com.codzs.config.security.SecurityHeadersConfigurer;
import com.codzs.constants.OAuth2Constants;
import com.codzs.security.DeviceAuthorizationRateLimitingFilter;
import com.codzs.security.blacklist.GlobalIpBlacklistFilter;
import com.codzs.security.whitelist.GlobalApiWhitelistFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for OAuth2 authorization server security filter chain.
 * 
 * <p>This class configures the security filter chain for OAuth2 authorization endpoints including:
 * <ul>
 *   <li>Device authorization endpoints with rate limiting</li>
 *   <li>IP blacklist and whitelist filters for enhanced security</li>
 *   <li>Security headers (HSTS, CSP, X-Frame-Options, etc.)</li>
 *   <li>CORS restrictions specific to OAuth2 flows</li>
 *   <li>Client authentication using Spring's built-in providers</li>
 * </ul>
 * 
 * <p>Security features include:
 * <ul>
 *   <li><strong>IP Filtering:</strong> Blacklist filter runs first to block banned IPs, 
 *       followed by whitelist filter to enforce allowed IPs</li>
 *   <li><strong>Rate Limiting:</strong> Device authorization endpoints have specific rate limits</li>
 *   <li><strong>Security Headers:</strong> Comprehensive headers via {@link SecurityHeadersConfigurer}</li>
 *   <li><strong>CORS:</strong> Restrictive CORS policy for OAuth2 endpoints only</li>
 * </ul>
 * 
 * <p>This configuration uses Spring Authorization Server's default client authentication 
 * which provides out-of-the-box support for:
 * <ul>
 *   <li>client_secret_basic</li>
 *   <li>client_secret_post</li> 
 *   <li>private_key_jwt</li>
 *   <li>client_secret_jwt</li>
 *   <li>tls_client_auth</li>
 *   <li>self_signed_tls_client_auth</li>
 * </ul>
 * 
 * @author Nitin Khaitan
 * @since 1.1
 * @see SecurityHeadersConfigurer
 * @see com.codzs.config.security.SecurityHeadersProperties
 */
@Configuration(proxyBeanMethods = false)
public class OAuth2SecurityFilterChainConfig {

	private static final String CUSTOM_CONSENT_PAGE_URI = OAuth2Constants.Endpoints.CONSENT;
	
	private final SecurityHeadersConfigurer securityHeadersConfigurer;

	public OAuth2SecurityFilterChainConfig(SecurityHeadersConfigurer securityHeadersConfigurer) {
		this.securityHeadersConfigurer = securityHeadersConfigurer;
	}

	/**
	 * Configure the security filter chain for OAuth2 authorization server.
	 * 
	 * <p>This method sets up a comprehensive security configuration with:
	 * <ul>
	 *   <li><strong>Filter Chain Order:</strong> IP Blacklist → IP Whitelist → Rate Limiting</li>
	 *   <li><strong>Device Authorization:</strong> Custom verification URI and consent page</li>
	 *   <li><strong>Client Authentication:</strong> Spring's default providers (all standard methods)</li>
	 *   <li><strong>OIDC Support:</strong> Full OpenID Connect 1.0 compliance</li>
	 *   <li><strong>Security Headers:</strong> Configurable HSTS, CSP, frame options via properties</li>
	 *   <li><strong>CORS Policy:</strong> Device-specific restrictive CORS settings</li>
	 * </ul>
	 * 
	 * <p>The filter chain processes requests in this order:
	 * <ol>
	 *   <li>IP Blacklist Filter (blocks banned IPs immediately)</li>
	 *   <li>IP Whitelist Filter (enforces allowed IPs if enabled)</li>
	 *   <li>Rate Limiting Filter (prevents device authorization abuse)</li>
	 *   <li>OAuth2 Authorization Server filters (Spring Security)</li>
	 * </ol>
	 * 
	 * @param http the HttpSecurity to configure
	 * @param authorizationServerSettings the authorization server settings bean
	 * @param rateLimitingFilter device authorization rate limiting filter
	 * @param ipBlacklistFilter global IP blacklist filter for security  
	 * @param ipWhitelistFilter global IP whitelist filter for access control
	 * @return SecurityFilterChain configured for OAuth2 authorization server
	 * @throws Exception if security configuration fails
	 */
	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public SecurityFilterChain authorizationServerSecurityFilterChain(
			HttpSecurity http, 
			AuthorizationServerSettings authorizationServerSettings,
			DeviceAuthorizationRateLimitingFilter rateLimitingFilter,
			GlobalIpBlacklistFilter ipBlacklistFilter,
			GlobalApiWhitelistFilter ipWhitelistFilter) throws Exception {

		OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = authorizationServer();

		// Configure the OAuth2 authorization server
		http
			.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
			// Add IP blacklist filter first to block banned IPs immediately
			.addFilterBefore(ipBlacklistFilter, UsernamePasswordAuthenticationFilter.class)
			// Add IP whitelist filter after blacklist to enforce allowed IPs
			.addFilterAfter(ipWhitelistFilter, GlobalIpBlacklistFilter.class)
			// Add rate limiting filter for device authorization endpoints
			.addFilterAfter(rateLimitingFilter, GlobalApiWhitelistFilter.class)
			.with(authorizationServerConfigurer, (authorizationServer) ->
				authorizationServer
					// .tokenEndpoint(Customizer.withDefaults()) // Add this line
					.deviceAuthorizationEndpoint(deviceAuthorizationEndpoint ->
						deviceAuthorizationEndpoint.verificationUri(OAuth2Constants.Endpoints.DEVICE_ACTIVATION)
					)
					.deviceVerificationEndpoint(deviceVerificationEndpoint ->
						deviceVerificationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI)
					)
					// Use Spring's default client authentication - supports all standard methods
					.clientAuthentication(Customizer.withDefaults())
					.authorizationEndpoint(authorizationEndpoint ->
						authorizationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI))
					.oidc(Customizer.withDefaults())	// Enable OpenID Connect 1.0
			)
			.authorizeHttpRequests((authorize) ->
				authorize.anyRequest().authenticated()
			)
			// Redirect to the /login page when not authenticated from the authorization endpoint
			// NOTE: DefaultSecurityConfig is configured with formLogin.loginPage("/login")
			.exceptionHandling((exceptions) -> exceptions
				.defaultAuthenticationEntryPointFor(
					new LoginUrlAuthenticationEntryPoint(OAuth2Constants.Endpoints.LOGIN),
					new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
				)
			)
			.cors(cors -> cors.configurationSource(securityHeadersConfigurer.createDeviceCorsConfigurationSource()))
			// Configure security headers using the dedicated configurer
			.headers(securityHeadersConfigurer::configureSecurityHeaders);

		return http.build();
	}

} 