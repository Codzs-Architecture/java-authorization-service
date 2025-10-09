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
package com.codzs.constant;

/**
 * Constants for OAuth2 and authorization server configuration.
 * This class centralizes all OAuth2-related constants to improve maintainability
 * and prevent magic strings throughout the codebase.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
public final class OAuth2Constant {

    private OAuth2Constant() {
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

    /**
     * OAuth2 parameter names.
     */
    public static final class Parameters {
        public static final String CLIENT_ID = "client_id";
        public static final String SCOPE = "scope";
        public static final String REDIRECT_URI = "redirect_uri";
        public static final String RESPONSE_TYPE = "response_type";
        public static final String STATE = "state";
        public static final String CODE = "code";
        public static final String GRANT_TYPE = "grant_type";
        
        private Parameters() {}
    }

    /**
     * OAuth2 grant types.
     */
    public static final class GrantTypes {
        public static final String AUTHORIZATION_CODE = "authorization_code";
        public static final String REFRESH_TOKEN = "refresh_token";
        public static final String CLIENT_CREDENTIALS = "client_credentials";
        public static final String DEVICE_CODE = "urn:ietf:params:oauth:grant-type:device_code";
        
        private GrantTypes() {}
    }

    /**
     * OAuth2 client authentication methods.
     */
    public static final class AuthMethods {
        public static final String CLIENT_SECRET_BASIC = "client_secret_basic";
        public static final String CLIENT_SECRET_POST = "client_secret_post";
        public static final String NONE = "none";
        public static final String PRIVATE_KEY_JWT = "private_key_jwt";
        public static final String CLIENT_SECRET_JWT = "client_secret_jwt";
        
        private AuthMethods() {}
    }

    /**
     * OAuth2 scopes.
     */
    public static final class Scopes {
        public static final String MESSAGE_READ = "message.read";
        public static final String MESSAGE_WRITE = "message.write";
        public static final String USER_READ = "user.read";
        public static final String OTHER_SCOPE = "other.scope";
        
        private Scopes() {}
    }

    /**
     * Security roles.
     */
    public static final class Roles {
        public static final String ADMIN = "ADMIN";
        public static final String USER = "USER";
        
        private Roles() {}
    }

    /**
     * Database collection names.
     */
    public static final class Collections {
        public static final String USER = "user";
        public static final String AUTHORITY = "authority";
        public static final String OAUTH2_REGISTERED_CLIENT = "oauth2_registered_client";
        public static final String OAUTH2_AUTHORIZATION = "oauth2_authorization";
        public static final String OAUTH2_AUTHORIZATION_CONSENT = "oauth2_authorization_consent";
        public static final String IP_BLACKLIST = "ip_blacklist";
        public static final String API_WHITELIST = "api_whitelist";
        
        private Collections() {}
    }

    /**
     * Database field names.
     */
    public static final class Fields {
        public static final String USERNAME = "username";
        public static final String AUTHORITY = "authority";
        public static final String IS_ACTIVE = "is_active";
        public static final String PRIORITY = "priority";
        public static final String BLOCKED_BY = "blockedBy";
        public static final String REASON = "reason";
        
        private Fields() {}
    }

    /**
     * Protocol schemes.
     */
    public static final class Schemes {
        public static final String HTTP = "http";
        public static final String HTTPS = "https";
        
        private Schemes() {}
    }

    /**
     * Validation error messages.
     */
    public static final class ValidationMessages {
        public static final String SCOPE_VALIDATION_FAILED = "Scope validation failed";
        public static final String REDIRECT_URI_VALIDATION_FAILED = "Redirect URI validation failed";
        public static final String INVALID_IP_RANGE_RESERVED = "Invalid IP range - reserved";
        public static final String EXAMPLE_LOCALHOST_BLOCK = "Example localhost block (remove in production)";
        public static final String EXAMPLE_PRIVATE_NETWORK_BLOCK = "Example private network block (configure as needed)";
        
        private ValidationMessages() {}
    }
}