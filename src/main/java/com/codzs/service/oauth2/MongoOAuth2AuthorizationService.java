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

import com.codzs.entity.oauth2.OAuth2Authorization;
import com.codzs.repository.oauth2.OAuth2AuthorizationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

import java.util.List;
import com.fasterxml.jackson.databind.Module;

/**
 * MongoDB implementation of OAuth2AuthorizationService.
 * Follows the same pattern as Spring's JdbcOAuth2AuthorizationService but uses MongoDB as the data store.
 * Simplified implementation without complex object reconstruction logic.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
public class MongoOAuth2AuthorizationService implements OAuth2AuthorizationService {

    
    private final OAuth2AuthorizationRepository authorizationRepository;
    private final RegisteredClientRepository registeredClientRepository;
    private final ObjectMapper objectMapper;

    public MongoOAuth2AuthorizationService(
            OAuth2AuthorizationRepository authorizationRepository,
            RegisteredClientRepository registeredClientRepository) {
        Assert.notNull(authorizationRepository, "authorizationRepository cannot be null");
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        this.authorizationRepository = authorizationRepository;
        this.registeredClientRepository = registeredClientRepository;
        this.objectMapper = createObjectMapper();
    }
    
    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();
        
        // Register all Spring Security Jackson modules (same as JDBC implementation)
        List<Module> modules = SecurityJackson2Modules.getModules(classLoader);
        mapper.registerModules(modules);
        
        // Register OAuth2 Authorization Server specific module (this handles complex OAuth2 objects)
        mapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        
        // Register time module
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return mapper;
    }

    @Override
    public void save(org.springframework.security.oauth2.server.authorization.OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        
        OAuth2Authorization entity = toEntity(authorization);
        authorizationRepository.save(entity);
    }

    @Override
    public void remove(org.springframework.security.oauth2.server.authorization.OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        
        authorizationRepository.deleteById(authorization.getId());
    }

    @Override
    public org.springframework.security.oauth2.server.authorization.OAuth2Authorization findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        
        Optional<OAuth2Authorization> authorization = authorizationRepository.findById(id);
        return authorization.map(this::toObject).orElse(null);
    }

    @Override
    public org.springframework.security.oauth2.server.authorization.OAuth2Authorization findByToken(
            String token, OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");
        
        Optional<OAuth2Authorization> result = Optional.empty();
        
        if (tokenType == null) {
            // Search all token types
            result = authorizationRepository.findByState(token);
            if (result.isEmpty()) {
                result = authorizationRepository.findByAuthorizationCodeValue(token);
            }
            if (result.isEmpty()) {
                result = authorizationRepository.findByAccessTokenValue(token);
            }
            if (result.isEmpty()) {
                result = authorizationRepository.findByRefreshTokenValue(token);
            }
            if (result.isEmpty()) {
                result = authorizationRepository.findByOidcIdTokenValue(token);
            }
            if (result.isEmpty()) {
                result = authorizationRepository.findByUserCodeValue(token);
            }
            if (result.isEmpty()) {
                result = authorizationRepository.findByDeviceCodeValue(token);
            }
        } else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            result = authorizationRepository.findByState(token);
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            result = authorizationRepository.findByAuthorizationCodeValue(token);
        } else if (OAuth2TokenType.ACCESS_TOKEN.equals(tokenType)) {
            result = authorizationRepository.findByAccessTokenValue(token);
        } else if (OAuth2TokenType.REFRESH_TOKEN.equals(tokenType)) {
            result = authorizationRepository.findByRefreshTokenValue(token);
        } else if (OidcParameterNames.ID_TOKEN.equals(tokenType.getValue())) {
            result = authorizationRepository.findByOidcIdTokenValue(token);
        }
        
        return result.map(this::toObject).orElse(null);
    }

    private OAuth2Authorization toEntity(org.springframework.security.oauth2.server.authorization.OAuth2Authorization authorization) {
        OAuth2Authorization entity = new OAuth2Authorization();
        entity.setId(authorization.getId());
        entity.setRegisteredClientId(authorization.getRegisteredClientId());
        entity.setPrincipalName(authorization.getPrincipalName());
        entity.setAuthorizationGrantType(authorization.getAuthorizationGrantType().getValue());
        entity.setAuthorizedScopes(String.join(",", authorization.getAuthorizedScopes()));
        entity.setAttributes(writeMap(authorization.getAttributes()));
        entity.setState(authorization.getAttribute(OAuth2ParameterNames.STATE));

        // Authorization Code
        org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode =
                authorization.getToken(OAuth2AuthorizationCode.class);
        setTokenDataOnEntity(entity, authorizationCode, entity::setAuthorizationCodeValue,
                entity::setAuthorizationCodeIssuedAt, entity::setAuthorizationCodeExpiresAt,
                entity::setAuthorizationCodeMetadata);

        // Access Token
        org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token<OAuth2AccessToken> accessToken =
                authorization.getToken(OAuth2AccessToken.class);
        setTokenDataOnEntity(entity, accessToken, entity::setAccessTokenValue,
                entity::setAccessTokenIssuedAt, entity::setAccessTokenExpiresAt,
                entity::setAccessTokenMetadata);
        if (accessToken != null && accessToken.getToken().getScopes() != null) {
            entity.setAccessTokenScopes(String.join(",", accessToken.getToken().getScopes()));
            entity.setAccessTokenType(accessToken.getToken().getTokenType().getValue());
        }

        // Refresh Token
        org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken =
                authorization.getToken(OAuth2RefreshToken.class);
        setTokenDataOnEntity(entity, refreshToken, entity::setRefreshTokenValue,
                entity::setRefreshTokenIssuedAt, entity::setRefreshTokenExpiresAt,
                entity::setRefreshTokenMetadata);

        // OIDC ID Token
        org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token<OidcIdToken> oidcIdToken =
                authorization.getToken(OidcIdToken.class);
        setTokenDataOnEntity(entity, oidcIdToken, entity::setOidcIdTokenValue,
                entity::setOidcIdTokenIssuedAt, entity::setOidcIdTokenExpiresAt,
                entity::setOidcIdTokenMetadata);
        if (oidcIdToken != null) {
            entity.setOidcIdTokenClaims(writeMap(oidcIdToken.getToken().getClaims()));
        }

        return entity;
    }

    private org.springframework.security.oauth2.server.authorization.OAuth2Authorization toObject(OAuth2Authorization entity) {
        RegisteredClient registeredClient = this.registeredClientRepository.findById(entity.getRegisteredClientId());
        if (registeredClient == null) {
            throw new IllegalArgumentException("Registered client not found with id: " + entity.getRegisteredClientId());
        }

        org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Builder builder =
                org.springframework.security.oauth2.server.authorization.OAuth2Authorization.withRegisteredClient(registeredClient)
                        .id(entity.getId())
                        .principalName(entity.getPrincipalName())
                        .authorizationGrantType(resolveAuthorizationGrantType(entity.getAuthorizationGrantType()))
                        .authorizedScopes(resolveScopes(entity.getAuthorizedScopes()))
                        .attributes(attributes -> attributes.putAll(readMap(entity.getAttributes())));

        if (entity.getState() != null) {
            builder.attribute(OAuth2ParameterNames.STATE, entity.getState());
        }

        if (entity.getAuthorizationCodeValue() != null) {
            OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(
                    entity.getAuthorizationCodeValue(),
                    toInstant(entity.getAuthorizationCodeIssuedAt()),
                    toInstant(entity.getAuthorizationCodeExpiresAt()));
            builder.token(authorizationCode, metadata -> metadata.putAll(readMap(entity.getAuthorizationCodeMetadata())));
        }

        if (entity.getAccessTokenValue() != null) {
            OAuth2AccessToken accessToken = new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    entity.getAccessTokenValue(),
                    toInstant(entity.getAccessTokenIssuedAt()),
                    toInstant(entity.getAccessTokenExpiresAt()),
                    resolveScopes(entity.getAccessTokenScopes()));
            builder.token(accessToken, metadata -> metadata.putAll(readMap(entity.getAccessTokenMetadata())));
        }

        if (entity.getRefreshTokenValue() != null) {
            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(
                    entity.getRefreshTokenValue(),
                    toInstant(entity.getRefreshTokenIssuedAt()),
                    toInstant(entity.getRefreshTokenExpiresAt()));
            builder.token(refreshToken, metadata -> metadata.putAll(readMap(entity.getRefreshTokenMetadata())));
        }

        if (entity.getOidcIdTokenValue() != null) {
            Map<String, Object> idTokenClaims = readMap(entity.getOidcIdTokenClaims());
            OidcIdToken idToken = new OidcIdToken(
                    entity.getOidcIdTokenValue(),
                    toInstant(entity.getOidcIdTokenIssuedAt()),
                    toInstant(entity.getOidcIdTokenExpiresAt()),
                    idTokenClaims);
            builder.token(idToken, metadata -> metadata.putAll(readMap(entity.getOidcIdTokenMetadata())));
        }

        return builder.build();
    }

    private void setTokenDataOnEntity(OAuth2Authorization entity,
                                    org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token<?> token,
                                    Consumer<String> tokenValueConsumer,
                                    Consumer<LocalDateTime> issuedAtConsumer,
                                    Consumer<LocalDateTime> expiresAtConsumer,
                                    Consumer<String> metadataConsumer) {
        if (token != null) {
            OAuth2Token oAuth2Token = token.getToken();
            tokenValueConsumer.accept(oAuth2Token.getTokenValue());
            issuedAtConsumer.accept(toLocalDateTime(oAuth2Token.getIssuedAt()));
            expiresAtConsumer.accept(toLocalDateTime(oAuth2Token.getExpiresAt()));
            metadataConsumer.accept(writeMap(token.getMetadata()));
        }
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }

    private Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toInstant(ZoneOffset.UTC) : null;
    }

    private Map<String, Object> readMap(String data) {
        try {
            if (data == null || data.isEmpty()) {
                return Map.of();
            }
            // Let Spring Security Jackson modules handle all complex object reconstruction
            return this.objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error reading JSON data: " + ex.getMessage(), ex);
        }
    }


    private String writeMap(Map<String, Object> data) {
        try {
            return this.objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error writing JSON data: " + ex.getMessage(), ex);
        }
    }

    private static org.springframework.security.oauth2.core.AuthorizationGrantType resolveAuthorizationGrantType(String authorizationGrantType) {
        if (org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(authorizationGrantType)) {
            return org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE;
        } else if (org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(authorizationGrantType)) {
            return org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS;
        } else if (org.springframework.security.oauth2.core.AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(authorizationGrantType)) {
            return org.springframework.security.oauth2.core.AuthorizationGrantType.REFRESH_TOKEN;
        }
        return new org.springframework.security.oauth2.core.AuthorizationGrantType(authorizationGrantType);
    }

    private static Set<String> resolveScopes(String scope) {
        return (scope != null) ? Set.of(org.springframework.util.StringUtils.delimitedListToStringArray(scope, ",")) : Set.of();
    }
}