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
package com.codzs.config.security.database;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * Configuration class for OAuth2 database setup.
 * This class handles the configuration of the embedded database used for OAuth2 authorization server.
 * 
 * @author Refactored from AuthorizationServerConfig
 * @since 1.1
 */
@Configuration(proxyBeanMethods = false)
public class OAuth2DatabaseConfig {

	/**
	 * Configure an embedded H2 database for OAuth2 authorization server.
	 * This database is used to store OAuth2 authorization data, consent information, and registered clients.
	 * 
	 * The database is initialized with the following schemas:
	 * - oauth2-authorization-schema.sql: For storing OAuth2 authorization data
	 * - oauth2-authorization-consent-schema.sql: For storing user consent information
	 * - oauth2-registered-client-schema.sql: For storing registered client information
	 * 
	 * @return EmbeddedDatabase configured with OAuth2 schemas
	 */
	@Bean
	public EmbeddedDatabase embeddedDatabase() {
		return new EmbeddedDatabaseBuilder()
				.generateUniqueName(true)
				.setType(EmbeddedDatabaseType.H2)
				.setScriptEncoding("UTF-8")
				.addScript("org/springframework/security/oauth2/server/authorization/oauth2-authorization-schema.sql")
				.addScript("org/springframework/security/oauth2/server/authorization/oauth2-authorization-consent-schema.sql")
				.addScript("org/springframework/security/oauth2/server/authorization/client/oauth2-registered-client-schema.sql")
				.build();
	}
} 