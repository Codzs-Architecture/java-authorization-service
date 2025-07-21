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
package com.codzs.constants;

/**
 * Constants for OAuth2 and authorization server configuration.
 * This class centralizes all OAuth2-related constants to improve maintainability
 * and prevent magic strings throughout the codebase.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
public final class OAuth2Constants {

    private OAuth2Constants() {
        // Utility class - prevent instantiation
    }

    /**
     * OAuth2 endpoint paths.
     */
    public static final class Endpoints {
        public static final String LOGIN = "/login";
        public static final String CONSENT = "/oauth2/consent";
        public static final String DEVICE_ACTIVATION = "/activate";
        public static final String DEVICE_ACTIVATED = "/activated";
        public static final String DEVICE_AUTHORIZATION = "/oauth2/device_authorization";
        public static final String DEVICE_VERIFICATION = "/oauth2/device_verification";
        public static final String TOKEN = "/oauth2/token";
        public static final String AUTHORIZE = "/oauth2/authorize";
        
        private Endpoints() {}
    }

    /**
     * Static resource paths.
     */
    public static final class Resources {
        public static final String ASSETS = "/assets/**";
        public static final String CSS = "/css/**";
        public static final String JS = "/js/**";
        public static final String IMAGES = "/images/**";
        
        private Resources() {}
    }

    /**
     * Management and actuator endpoints.
     */
    public static final class Management {
        public static final String MANAGEMENT = "/management/**";
        public static final String ACTUATOR = "/actuator/**";
        public static final String HEALTH = "/actuator/health";
        public static final String INFO = "/actuator/info";
        
        private Management() {}
    }

    /**
     * OAuth2 token settings.
     */
    public static final class TokenSettings {
        public static final String X509_CERTIFICATE_BOUND_ACCESS_TOKENS = 
            "settings.token.x509-certificate-bound-access-tokens";
        
        private TokenSettings() {}
    }

    /**
     * JWT claim names.
     */
    public static final class Claims {
        public static final String CNF = "cnf";
        public static final String X5T_S256 = "x5t#S256";
        
        private Claims() {}
    }
}