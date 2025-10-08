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

/**
 * Configuration class for OAuth2 client registration.
 * This class is now deprecated as OAuth2 client registration is handled
 * by MongoDB services in OAuth2ServiceConfig.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 * @deprecated Use MongoDB-based services in OAuth2ServiceConfig instead
 */
@Deprecated
public class OAuth2ClientRegistrationConfig {
    // This class is no longer needed as RegisteredClientRepository
    // is configured in OAuth2ServiceConfig using MongoDB
} 