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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

/**
 * Configuration class for OAuth2 authorization services and server settings.
 * This class handles the configuration of OAuth2 authorization services and server settings.
 * 
 * @author Refactored from AuthorizationServerConfig
 * @since 1.1
 */
@Configuration(proxyBeanMethods = false)
public class OAuth2ServiceConfig {

	/**
	 * Configure the OAuth2 authorization service for managing OAuth2 authorizations.
	 * This service stores and retrieves OAuth2 authorization information.
	 * 
	 * @param jdbcTemplate the JDBC template for database operations
	 * @param registeredClientRepository the repository for registered clients
	 * @return OAuth2AuthorizationService for managing authorizations
	 */
	@Bean
	public OAuth2AuthorizationService authorizationService(
			JdbcTemplate jdbcTemplate,
			RegisteredClientRepository registeredClientRepository) {
		return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
	}

	/**
	 * Configure the OAuth2 authorization consent service for managing user consent.
	 * This service stores and retrieves user consent information for OAuth2 authorizations.
	 * Used by the ConsentController for consent management.
	 * 
	 * @param jdbcTemplate the JDBC template for database operations
	 * @param registeredClientRepository the repository for registered clients
	 * @return OAuth2AuthorizationConsentService for managing consent
	 */
	@Bean
	public OAuth2AuthorizationConsentService authorizationConsentService(
			JdbcTemplate jdbcTemplate,
			RegisteredClientRepository registeredClientRepository) {
		return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
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