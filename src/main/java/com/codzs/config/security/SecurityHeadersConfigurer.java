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
package com.codzs.config.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configurer for security headers and CORS settings.
 * Provides reusable configuration methods for OAuth2 security.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
@Component
public class SecurityHeadersConfigurer {

    private final SecurityHeadersProperties properties;

    public SecurityHeadersConfigurer(SecurityHeadersProperties properties) {
        this.properties = properties;
    }

    /**
     * Configures security headers for OAuth2 endpoints.
     * 
     * @param headers the HeadersConfigurer to configure
     * @return configured HeadersConfigurer
     * @throws IllegalStateException if required security configuration is missing
     */
    public HeadersConfigurer<HttpSecurity> configureSecurityHeaders(HeadersConfigurer<HttpSecurity> headers) {
        validateSecurityConfiguration();
        
        return headers
            .frameOptions(frameOptions -> frameOptions.deny())
            .contentTypeOptions(contentTypeOptions -> {
                // Default configuration - enables X-Content-Type-Options: nosniff
            })
            .httpStrictTransportSecurity(hsts -> hsts
                .maxAgeInSeconds(properties.getHsts().getMaxAgeSeconds())
                .includeSubDomains(properties.getHsts().isIncludeSubDomains())
            )
            .contentSecurityPolicy(csp -> csp
                .policyDirectives(properties.getCsp().buildPolicy())
            );
    }

    /**
     * Creates device-specific CORS configuration for OAuth2 authorization endpoints.
     * More restrictive than the general application CORS settings.
     * 
     * @return CorsConfigurationSource with device-specific restrictions
     * @throws IllegalStateException if required CORS configuration is missing
     */
    public CorsConfigurationSource createDeviceCorsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        SecurityHeadersProperties.DeviceCors deviceCors = properties.getDeviceCors();
        
        validateCorsConfiguration(deviceCors);
        
        config.setAllowedOrigins(deviceCors.getAllowedOrigins());
        config.setAllowedHeaders(deviceCors.getAllowedHeaders());
        config.setAllowedMethods(deviceCors.getAllowedMethods());
        config.setAllowCredentials(deviceCors.isAllowCredentials());
        config.setMaxAge(deviceCors.getMaxAgeSeconds());
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Validates that all required security configuration properties are present.
     * 
     * @throws IllegalStateException if required configuration is missing
     */
    private void validateSecurityConfiguration() {
        if (properties.getHsts().getMaxAgeSeconds() <= 0) {
            throw new IllegalStateException("HSTS max age must be configured via app.security.hsts.max-age-seconds");
        }
        // CSP validation is handled by buildPolicy() method
    }

    /**
     * Validates that all required CORS configuration properties are present.
     * 
     * @param deviceCors the device CORS configuration to validate
     * @throws IllegalStateException if required configuration is missing
     */
    private void validateCorsConfiguration(SecurityHeadersProperties.DeviceCors deviceCors) {
        if (deviceCors.getAllowedOrigins() == null || deviceCors.getAllowedOrigins().isEmpty()) {
            throw new IllegalStateException("Device CORS allowed origins must be configured via app.security.device-cors.allowed-origins");
        }
        
        if (deviceCors.getMaxAgeSeconds() <= 0) {
            throw new IllegalStateException("Device CORS max age must be configured via app.security.device-cors.max-age-seconds");
        }
    }
}