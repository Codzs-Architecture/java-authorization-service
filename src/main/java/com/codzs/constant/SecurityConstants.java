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
package com.codzs.constant;

import java.util.List;

/**
 * Security-related constants for the application.
 * 
 * <p>This class provides centralized definitions for security configuration
 * constants used across the application, particularly for CORS and CSP settings.</p>
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
public final class SecurityConstants {

    private SecurityConstants() {
        // Prevent instantiation
    }

    /**
     * Standard CORS headers commonly used in OAuth2 applications.
     */
    public static final class CorsHeaders {
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String AUTHORIZATION = "Authorization";
        public static final String X_REQUESTED_WITH = "X-Requested-With";
        public static final String X_XSRF_TOKEN = "X-XSRF-TOKEN";
        public static final String CACHE_CONTROL = "Cache-Control";
        public static final String PRAGMA = "Pragma";

        /**
         * Standard CORS headers for OAuth2 device authorization flows.
         */
        public static final List<String> STANDARD_OAUTH2_HEADERS = List.of(
            CONTENT_TYPE,
            AUTHORIZATION, 
            X_REQUESTED_WITH
        );

        /**
         * Extended CORS headers for development environments.
         */
        public static final List<String> EXTENDED_OAUTH2_HEADERS = List.of(
            CONTENT_TYPE,
            AUTHORIZATION,
            X_REQUESTED_WITH,
            X_XSRF_TOKEN,
            CACHE_CONTROL,
            PRAGMA
        );

        private CorsHeaders() {
            // Prevent instantiation
        }
    }

    /**
     * Standard HTTP methods used in CORS configurations.
     */
    public static final class CorsMethods {
        public static final String GET = "GET";
        public static final String POST = "POST";
        public static final String PUT = "PUT";
        public static final String DELETE = "DELETE";
        public static final String OPTIONS = "OPTIONS";
        public static final String HEAD = "HEAD";

        /**
         * Standard HTTP methods for OAuth2 device authorization flows.
         */
        public static final List<String> STANDARD_OAUTH2_METHODS = List.of(
            GET,
            POST,
            OPTIONS
        );

        /**
         * Extended HTTP methods for comprehensive API access.
         */
        public static final List<String> EXTENDED_OAUTH2_METHODS = List.of(
            GET,
            POST,
            PUT,
            DELETE,
            OPTIONS,
            HEAD
        );

        private CorsMethods() {
            // Prevent instantiation
        }
    }

    /**
     * Content Security Policy directive constants.
     */
    public static final class CspDirectives {
        public static final String SELF = "'self'";
        public static final String NONE = "'none'";
        public static final String UNSAFE_INLINE = "'unsafe-inline'";
        public static final String UNSAFE_EVAL = "'unsafe-eval'";
        public static final String DATA = "data:";
        public static final String HTTPS = "https:";
        
        private CspDirectives() {
            // Prevent instantiation
        }
    }
}