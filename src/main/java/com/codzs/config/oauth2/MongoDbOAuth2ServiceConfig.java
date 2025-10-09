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

import com.codzs.service.oauth2.MongoOAuth2AuthorizationService;
import com.codzs.service.oauth2.MongoOAuth2AuthorizationConsentService;
import com.codzs.service.oauth2.MongoRegisteredClientRepository;
import com.codzs.repository.oauth2.OAuth2AuthorizationRepository;
import com.codzs.repository.oauth2.OAuth2AuthorizationConsentRepository;
import com.codzs.repository.oauth2.OAuth2RegisteredClientRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

/**
 * Configuration class for OAuth2 authorization services and server settings.
 * This class handles the configuration of OAuth2 authorization services and server settings.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
@Configuration(proxyBeanMethods = false)
public class MongoDbOAuth2ServiceConfig {

	/**
	 * Configure the OAuth2 authorization service for managing OAuth2 authorizations.
	 * This service stores and retrieves OAuth2 authorization information using MongoDB.
	 * 
	 * @param authorizationRepository the MongoDB repository for authorizations
	 * @param registeredClientRepository the repository for registered clients
	 * @return OAuth2AuthorizationService for managing authorizations
	 */
	@Bean
	public OAuth2AuthorizationService authorizationService(
			OAuth2AuthorizationRepository authorizationRepository,
			RegisteredClientRepository registeredClientRepository) {
		return new MongoOAuth2AuthorizationService(authorizationRepository, registeredClientRepository);
	}

	/**
	 * Configure the OAuth2 authorization consent service for managing user consent.
	 * This service stores and retrieves user consent information for OAuth2 authorizations using MongoDB.
	 * Used by the ConsentController for consent management.
	 * 
	 * @param authorizationConsentRepository the MongoDB repository for consent
	 * @param registeredClientRepository the repository for registered clients
	 * @return OAuth2AuthorizationConsentService for managing consent
	 */
	@Bean
	@Primary
	public OAuth2AuthorizationConsentService authorizationConsentService(
			OAuth2AuthorizationConsentRepository authorizationConsentRepository,
			RegisteredClientRepository registeredClientRepository) {
		return new MongoOAuth2AuthorizationConsentService(authorizationConsentRepository, registeredClientRepository);
	}

	/**
	 * Configure the registered client repository for managing OAuth2 clients.
	 * This service stores and retrieves OAuth2 client registration information using MongoDB.
	 * Uses its own OAuth2-specific ObjectMapper configuration instead of global one.
	 * 
	 * @param mongoRepository the MongoDB repository for registered clients
	 * @return RegisteredClientRepository for managing client registrations
	 */
	@Bean
	@Primary
	public RegisteredClientRepository registeredClientRepository(
			OAuth2RegisteredClientRepository mongoRepository) {
		return new MongoRegisteredClientRepository(mongoRepository, null);
	}

	/**
	 * Configure the authorization server settings.
	 * This provides the basic settings for the OAuth2 authorization server endpoints.
	 * 
	 * @return AuthorizationServerSettings with default configuration
	 */
	@Bean
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder().build();
	}
} 