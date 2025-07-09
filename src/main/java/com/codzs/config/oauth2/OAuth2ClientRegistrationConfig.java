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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

/**
 * Configuration class for OAuth2 client registration.
 * This class provides the JdbcRegisteredClientRepository bean that loads
 * registered clients from the database via Flyway migrations.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
@Configuration(proxyBeanMethods = false)
public class OAuth2ClientRegistrationConfig {

	/**
	 * Configure the registered client repository to load clients from database.
	 * Clients are now registered via Flyway migrations in V0_0_7__oauth2_clients.sql
	 * instead of being created programmatically.
	 * 
	 * @param jdbcTemplate the JDBC template for database operations
	 * @return RegisteredClientRepository that loads clients from database
	 */
	@Bean
	public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
		return new JdbcRegisteredClientRepository(jdbcTemplate);
	}
} 