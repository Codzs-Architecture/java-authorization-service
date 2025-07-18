/*
 * Copyright 2020-2025 Nitin Khaitan.
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

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.codzs.config.oauth2.OAuth2ClientRegistrationConfig;
import com.codzs.config.oauth2.OAuth2SecurityFilterChainConfig;
import com.codzs.config.oauth2.OAuth2ServiceConfig;
import com.codzs.config.oauth2.OAuth2TokenConfig;

/**
 * Main configuration class for OAuth2 Authorization Server.
 * This class imports all the separated OAuth2 configuration classes to provide
 * a complete OAuth2 authorization server setup.
 * 
 * The configuration has been decomposed into separate concerns:
 * - OAuth2ClientRegistrationConfig: Client registration and management
 * - OAuth2SecurityFilterChainConfig: Security filter chain for OAuth2 endpoints
 * - OAuth2ServiceConfig: Authorization services and server settings
 * - OAuth2TokenConfig: JWT/token configuration and customization
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
@Configuration(proxyBeanMethods = false)
@Import({
	OAuth2ClientRegistrationConfig.class,
	OAuth2SecurityFilterChainConfig.class,
	OAuth2ServiceConfig.class,
	OAuth2TokenConfig.class
})
public class AuthorizationServerConfig {
}
