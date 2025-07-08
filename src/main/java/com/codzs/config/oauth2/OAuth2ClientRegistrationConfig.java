/*
 * Copyright 2020-2024 the original author or authors.
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

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

/**
 * Configuration class for OAuth2 client registration.
 * This class handles the registration of OAuth2 clients including messaging, device, token exchange, and mTLS clients.
 * 
 * @author Refactored from AuthorizationServerConfig
 * @since 1.1
 */
@Configuration(proxyBeanMethods = false)
public class OAuth2ClientRegistrationConfig {

	/**
	 * Configure the registered client repository with predefined OAuth2 clients.
	 * This method registers four different types of clients:
	 * - Messaging client for standard OAuth2 flows
	 * - Device client for device authorization flow
	 * - Token exchange client for token exchange flow
	 * - mTLS demo client for mutual TLS authentication
	 * 
	 * @param jdbcTemplate the JDBC template for database operations
	 * @return RegisteredClientRepository containing all registered clients
	 */
	@Bean
	public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
		JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
		
		// Register all predefined clients
		registeredClientRepository.save(createMessagingClient());
		registeredClientRepository.save(createDeviceClient());
		registeredClientRepository.save(createTokenExchangeClient());
		registeredClientRepository.save(createMtlsDemoClient());
		
		return registeredClientRepository;
	}

	/**
	 * Create the messaging client for standard OAuth2 authorization code flow.
	 * This client supports authorization code, refresh token, and client credentials grants.
	 * 
	 * @return RegisteredClient for messaging operations
	 */
	private RegisteredClient createMessagingClient() {
		return RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("messaging-client")
				.clientSecret("{noop}secret")
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.redirectUri("http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc")
				.redirectUri("http://127.0.0.1:8080/authorized")
				.postLogoutRedirectUri("http://127.0.0.1:8080/logged-out")
				.scope(OidcScopes.OPENID)
				.scope(OidcScopes.PROFILE)
				.scope("message.read")
				.scope("message.write")
				.scope("user.read")
				.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
				.build();
	}

	/**
	 * Create the device client for OAuth2 device authorization flow.
	 * This client uses no authentication method and supports device code and refresh token grants.
	 * 
	 * @return RegisteredClient for device operations
	 */
	private RegisteredClient createDeviceClient() {
		return RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("device-messaging-client")
				.clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
				.authorizationGrantType(AuthorizationGrantType.DEVICE_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.scope("message.read")
				.scope("message.write")
				.build();
	}

	/**
	 * Create the token exchange client for OAuth2 token exchange flow.
	 * This client supports the token exchange grant type for token delegation scenarios.
	 * 
	 * @return RegisteredClient for token exchange operations
	 */
	private RegisteredClient createTokenExchangeClient() {
		return RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("token-client")
				.clientSecret("{noop}token")
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(new AuthorizationGrantType("urn:ietf:params:oauth:grant-type:token-exchange"))
				.scope("message.read")
				.scope("message.write")
				.build();
	}

	/**
	 * Create the mTLS demo client for mutual TLS authentication.
	 * This client supports both TLS client auth and self-signed TLS client auth methods.
	 * 
	 * @return RegisteredClient for mTLS operations
	 */
	private RegisteredClient createMtlsDemoClient() {
		return RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("mtls-demo-client")
				.clientAuthenticationMethod(ClientAuthenticationMethod.TLS_CLIENT_AUTH)
				.clientAuthenticationMethod(ClientAuthenticationMethod.SELF_SIGNED_TLS_CLIENT_AUTH)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.scope("message.read")
				.scope("message.write")
				.clientSettings(
						ClientSettings.builder()
								.x509CertificateSubjectDN("CN=demo-client-sample,OU=Spring Samples,O=Spring,C=US")
								.jwkSetUrl("http://127.0.0.1:8080/jwks")
								.build()
				)
				.tokenSettings(
						TokenSettings.builder()
								.x509CertificateBoundAccessTokens(true)
								.build()
				)
				.build();
	}
} 