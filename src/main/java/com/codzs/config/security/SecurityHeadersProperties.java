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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Configuration properties for security headers and CORS settings.
 * 
 * <p>This class binds configuration from the config server using the prefix {@code app.security}.
 * All values are loaded from external configuration with safe fallback defaults to ensure
 * the application remains functional even if configuration is missing.</p>
 * 
 * <p>Configuration structure:</p>
 * <ul>
 *   <li>{@code app.security.hsts.*} - HTTP Strict Transport Security settings</li>
 *   <li>{@code app.security.csp.*} - Content Security Policy directives</li>
 *   <li>{@code app.security.device-cors.*} - OAuth2 device-specific CORS settings</li>
 * </ul>
 * 
 * <p>All properties are environment-specific and configured via Spring Cloud Config Server.</p>
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
@Component
@ConfigurationProperties(prefix = "app.security")
public class SecurityHeadersProperties {

    private final Hsts hsts = new Hsts();
    private final Csp csp = new Csp();
    private final DeviceCors deviceCors = new DeviceCors();

    public Hsts getHsts() {
        return hsts;
    }

    public Csp getCsp() {
        return csp;
    }

    public DeviceCors getDeviceCors() {
        return deviceCors;
    }

    /**
     * HTTP Strict Transport Security configuration.
     */
    public static class Hsts {
        private long maxAgeSeconds;
        private boolean includeSubDomains;

        public long getMaxAgeSeconds() {
            return maxAgeSeconds;
        }

        public void setMaxAgeSeconds(long maxAgeSeconds) {
            this.maxAgeSeconds = maxAgeSeconds;
        }

        public boolean isIncludeSubDomains() {
            return includeSubDomains;
        }

        public void setIncludeSubDomains(boolean includeSubDomains) {
            this.includeSubDomains = includeSubDomains;
        }
    }

    /**
     * Content Security Policy configuration.
     */
    public static class Csp {
        private String defaultSrc;
        private String scriptSrc;
        private String styleSrc;
        private String imgSrc;
        private String fontSrc;
        private String formAction;
        private String baseUri;

        public String getDefaultSrc() {
            return defaultSrc;
        }

        public void setDefaultSrc(String defaultSrc) {
            this.defaultSrc = defaultSrc;
        }

        public String getScriptSrc() {
            return scriptSrc;
        }

        public void setScriptSrc(String scriptSrc) {
            this.scriptSrc = scriptSrc;
        }

        public String getStyleSrc() {
            return styleSrc;
        }

        public void setStyleSrc(String styleSrc) {
            this.styleSrc = styleSrc;
        }

        public String getImgSrc() {
            return imgSrc;
        }

        public void setImgSrc(String imgSrc) {
            this.imgSrc = imgSrc;
        }

        public String getFontSrc() {
            return fontSrc;
        }

        public void setFontSrc(String fontSrc) {
            this.fontSrc = fontSrc;
        }

        public String getFormAction() {
            return formAction;
        }

        public void setFormAction(String formAction) {
            this.formAction = formAction;
        }

        public String getBaseUri() {
            return baseUri;
        }

        public void setBaseUri(String baseUri) {
            this.baseUri = baseUri;
        }

        /**
         * Builds the complete CSP policy string from configured values.
         * All values must be provided via configuration.
         */
        public String buildPolicy() {
            if (!StringUtils.hasText(defaultSrc) || !StringUtils.hasText(scriptSrc) ||
                !StringUtils.hasText(styleSrc) || !StringUtils.hasText(imgSrc) ||
                !StringUtils.hasText(fontSrc) || !StringUtils.hasText(formAction) ||
                !StringUtils.hasText(baseUri)) {
                throw new IllegalStateException("All CSP directives must be configured via app.security.csp.*");
            }
            return String.format(
                "default-src %s; script-src %s; style-src %s; img-src %s; font-src %s; form-action %s; base-uri %s",
                defaultSrc, scriptSrc, styleSrc, imgSrc, fontSrc, formAction, baseUri
            );
        }
    }

    /**
     * Device-specific CORS configuration for OAuth2 endpoints.
     */
    public static class DeviceCors {
        private List<String> allowedOrigins;
        private List<String> allowedHeaders;
        private List<String> allowedMethods;
        private boolean allowCredentials = true; // Default to true for OAuth2
        private long maxAgeSeconds;

        public List<String> getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(List<String> allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }

        public List<String> getAllowedHeaders() {
            return allowedHeaders != null && !allowedHeaders.isEmpty()
                ? allowedHeaders
                : SecurityConstants.CorsHeaders.STANDARD_OAUTH2_HEADERS;
        }

        public void setAllowedHeaders(List<String> allowedHeaders) {
            this.allowedHeaders = allowedHeaders;
        }

        public List<String> getAllowedMethods() {
            return allowedMethods != null && !allowedMethods.isEmpty()
                ? allowedMethods
                : SecurityConstants.CorsMethods.STANDARD_OAUTH2_METHODS;
        }

        public void setAllowedMethods(List<String> allowedMethods) {
            this.allowedMethods = allowedMethods;
        }

        public boolean isAllowCredentials() {
            return allowCredentials;
        }

        public void setAllowCredentials(boolean allowCredentials) {
            this.allowCredentials = allowCredentials;
        }

        public long getMaxAgeSeconds() {
            return maxAgeSeconds;
        }

        public void setMaxAgeSeconds(long maxAgeSeconds) {
            this.maxAgeSeconds = maxAgeSeconds;
        }
    }
}