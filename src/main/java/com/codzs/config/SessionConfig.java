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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.DefaultCookieSerializer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Session configuration for the authorization server.
 * 
 * This configuration ensures proper session handling for OAuth2 authorization flows
 * by using an in-memory session repository that doesn't suffer from serialization
 * issues with complex OAuth2 objects.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Configuration
@EnableSpringHttpSession
public class SessionConfig {

    /**
     * Configure in-memory session repository to avoid serialization issues
     * with OAuth2AuthorizationRequest objects.
     * 
     * @return MapSessionRepository for in-memory session storage
     */
    @Bean
    public SessionRepository<?> sessionRepository() {
        return new MapSessionRepository(new ConcurrentHashMap<>());
    }

    /**
     * Configure cookie serializer for session management.
     * 
     * @return DefaultCookieSerializer with secure settings
     */
    @Bean
    public DefaultCookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("JSESSIONID");
        serializer.setCookiePath("/");
        serializer.setUseHttpOnlyCookie(true);
        serializer.setSameSite("Lax");
        // Set to true in production with HTTPS
        serializer.setUseSecureCookie(false);
        return serializer;
    }
}