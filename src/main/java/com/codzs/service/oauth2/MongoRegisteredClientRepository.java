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
package com.codzs.service.oauth2;

import com.codzs.entity.oauth2.OAuth2RegisteredClient;
import com.codzs.repository.oauth2.OAuth2RegisteredClientRepository;
import com.codzs.util.oauth2.OAuth2Util;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

import java.util.List;
import com.fasterxml.jackson.databind.Module;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;

/**
 * MongoDB implementation of RegisteredClientRepository.
 * Follows the same pattern as Spring's JdbcRegisteredClientRepository but uses MongoDB as the data store.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
public class MongoRegisteredClientRepository implements RegisteredClientRepository {


    private final OAuth2RegisteredClientRepository mongoRepository;
    private final ObjectMapper objectMapper;

    public MongoRegisteredClientRepository(OAuth2RegisteredClientRepository mongoRepository, ObjectMapper objectMapper) {
        Assert.notNull(mongoRepository, "mongoRepository cannot be null");
        this.mongoRepository = mongoRepository;
        // Use same ObjectMapper configuration as JDBC implementation
        this.objectMapper = createObjectMapper();
    }
    
    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();
        
        // Register all Spring Security Jackson modules (same as JDBC implementation)
        List<Module> modules = SecurityJackson2Modules.getModules(classLoader);
        mapper.registerModules(modules);
        
        // Register OAuth2 Authorization Server specific module
        mapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        
        // Register time module
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return mapper;
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        Assert.notNull(registeredClient, "registeredClient cannot be null");
        
        OAuth2RegisteredClient entity = toEntity(registeredClient);
        mongoRepository.save(entity);
    }

    @Override
    public RegisteredClient findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        
        Optional<OAuth2RegisteredClient> client = mongoRepository.findById(id);
        return client.map(this::toObject).orElse(null);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        Assert.hasText(clientId, "clientId cannot be empty");
        
        Optional<OAuth2RegisteredClient> client = mongoRepository.findByClientId(clientId);
        return client.map(this::toObject).orElse(null);
    }

    private OAuth2RegisteredClient toEntity(RegisteredClient registeredClient) {
        OAuth2RegisteredClient entity = new OAuth2RegisteredClient();
        entity.setId(registeredClient.getId());
        entity.setClientId(registeredClient.getClientId());
        entity.setClientIdIssuedAt(toLocalDateTime(registeredClient.getClientIdIssuedAt()));
        entity.setClientSecret(registeredClient.getClientSecret());
        entity.setClientSecretExpiresAt(toLocalDateTime(registeredClient.getClientSecretExpiresAt()));
        entity.setClientName(registeredClient.getClientName());
        
        // Convert collections to comma-separated strings (following JDBC pattern)
        entity.setClientAuthenticationMethods(
                StringUtils.collectionToCommaDelimitedString(
                        registeredClient.getClientAuthenticationMethods().stream()
                                .map(ClientAuthenticationMethod::getValue)
                                .toList()
                )
        );
        
        entity.setAuthorizationGrantTypes(
                StringUtils.collectionToCommaDelimitedString(
                        registeredClient.getAuthorizationGrantTypes().stream()
                                .map(AuthorizationGrantType::getValue)
                                .toList()
                )
        );
        
        entity.setRedirectUris(
                StringUtils.collectionToCommaDelimitedString(registeredClient.getRedirectUris())
        );
        
        entity.setPostLogoutRedirectUris(
                StringUtils.collectionToCommaDelimitedString(registeredClient.getPostLogoutRedirectUris())
        );
        
        entity.setScopes(
                StringUtils.collectionToCommaDelimitedString(registeredClient.getScopes())
        );
        
        // Serialize settings to JSON (same as JDBC approach)
        entity.setClientSetting(writeMap(registeredClient.getClientSettings().getSettings()));
        entity.setTokenSetting(writeMap(registeredClient.getTokenSettings().getSettings()));
        
        return entity;
    }

    private RegisteredClient toObject(OAuth2RegisteredClient entity) {
        RegisteredClient.Builder builder = RegisteredClient.withId(entity.getId())
                .clientId(entity.getClientId())
                .clientIdIssuedAt(toInstant(entity.getClientIdIssuedAt()))
                .clientSecret(entity.getClientSecret())
                .clientSecretExpiresAt(toInstant(entity.getClientSecretExpiresAt()))
                .clientName(entity.getClientName());
        
        // Parse authentication methods
        if (StringUtils.hasText(entity.getClientAuthenticationMethods())) {
            for (String method : StringUtils.commaDelimitedListToStringArray(entity.getClientAuthenticationMethods())) {
                builder.clientAuthenticationMethod(OAuth2Util.resolveClientAuthenticationMethod(method.trim()));
            }
        }
        
        // Parse grant types
        if (StringUtils.hasText(entity.getAuthorizationGrantTypes())) {
            for (String grantType : StringUtils.commaDelimitedListToStringArray(entity.getAuthorizationGrantTypes())) {
                builder.authorizationGrantType(OAuth2Util.resolveAuthorizationGrantType(grantType.trim()));
            }
        }
        
        // Parse redirect URIs
        if (StringUtils.hasText(entity.getRedirectUris())) {
            builder.redirectUris(uris -> {
                for (String uri : StringUtils.commaDelimitedListToStringArray(entity.getRedirectUris())) {
                    uris.add(uri.trim());
                }
            });
        }
        
        // Parse post logout redirect URIs
        if (StringUtils.hasText(entity.getPostLogoutRedirectUris())) {
            builder.postLogoutRedirectUris(uris -> {
                for (String uri : StringUtils.commaDelimitedListToStringArray(entity.getPostLogoutRedirectUris())) {
                    uris.add(uri.trim());
                }
            });
        }
        
        // Parse scopes
        if (StringUtils.hasText(entity.getScopes())) {
            builder.scopes(scopes -> {
                for (String scope : StringUtils.commaDelimitedListToStringArray(entity.getScopes())) {
                    scopes.add(scope.trim());
                }
            });
        }
        
        // Deserialize client settings
        Map<String, Object> clientSettingsMap = readMap(entity.getClientSetting());
        builder.clientSettings(ClientSettings.withSettings(clientSettingsMap).build());
        
        // Deserialize token settings
        Map<String, Object> tokenSettingsMap = readMap(entity.getTokenSetting());
        builder.tokenSettings(TokenSettings.withSettings(tokenSettingsMap).build());
        
        return builder.build();
    }

    private LocalDateTime toLocalDateTime(java.time.Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }

    private java.time.Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toInstant(ZoneOffset.UTC) : null;
    }


    private Map<String, Object> readMap(String data) {
        try {
            return StringUtils.hasText(data) ? 
                this.objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {}) : 
                Map.of();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error reading JSON: " + ex.getMessage(), ex);
        }
    }

    private String writeMap(Map<String, Object> data) {
        try {
            return this.objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error writing JSON: " + ex.getMessage(), ex);
        }
    }

}