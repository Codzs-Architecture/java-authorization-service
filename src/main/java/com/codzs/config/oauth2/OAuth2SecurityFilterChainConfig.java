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
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import static org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer.authorizationServer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import com.codzs.oauth2.authentication.device.DeviceClientAuthenticationProvider;
import com.codzs.web.authentication.DeviceClientAuthenticationConverter;

/**
 * Configuration class for OAuth2 authorization server security filter chain.
 * This class handles the configuration of the security filter chain for OAuth2 authorization endpoints.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
@Configuration(proxyBeanMethods = false)
public class OAuth2SecurityFilterChainConfig {

	private static final String CUSTOM_CONSENT_PAGE_URI = "/oauth2/consent";

	/**
	 * Configure the security filter chain for OAuth2 authorization server.
	 * This filter chain handles OAuth2 authorization endpoints including device authorization.
	 * 
	 * CAUTION: The device authorization endpoints configured here do not require authentication
	 * and can be accessed by any client that has a valid clientId. It is recommended to
	 * carefully monitor the use of these endpoints and employ additional protections as needed.
	 * 
	 * @param http the HttpSecurity to configure
	 * @param registeredClientRepository the repository for registered clients
	 * @param authorizationServerSettings the authorization server settings
	 * @return SecurityFilterChain configured for OAuth2 authorization server
	 * @throws Exception if configuration fails
	 */
	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public SecurityFilterChain authorizationServerSecurityFilterChain(
			HttpSecurity http, 
			RegisteredClientRepository registeredClientRepository,
			AuthorizationServerSettings authorizationServerSettings) throws Exception {

		// Create device client authentication components
		DeviceClientAuthenticationConverter deviceClientAuthenticationConverter =
				new DeviceClientAuthenticationConverter(
						authorizationServerSettings.getDeviceAuthorizationEndpoint());
		DeviceClientAuthenticationProvider deviceClientAuthenticationProvider =
				new DeviceClientAuthenticationProvider(registeredClientRepository);

		OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = authorizationServer();

		// Configure the OAuth2 authorization server
		http
			.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
			.with(authorizationServerConfigurer, (authorizationServer) ->
				authorizationServer
					.deviceAuthorizationEndpoint(deviceAuthorizationEndpoint ->
						deviceAuthorizationEndpoint.verificationUri("/activate")
					)
					.deviceVerificationEndpoint(deviceVerificationEndpoint ->
						deviceVerificationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI)
					)
					.clientAuthentication(clientAuthentication ->
						clientAuthentication
							.authenticationConverter(deviceClientAuthenticationConverter)
							.authenticationProvider(deviceClientAuthenticationProvider)
					)
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
					new LoginUrlAuthenticationEntryPoint("/login"),
					new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
				)
			)
			.cors(Customizer.withDefaults());

		return http.build();
	}
} 