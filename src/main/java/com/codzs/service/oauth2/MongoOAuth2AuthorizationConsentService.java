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

import com.codzs.entity.oauth2.OAuth2AuthorizationConsent;
import com.codzs.repository.oauth2.OAuth2AuthorizationConsentRepository;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.Set;

/**
 * MongoDB implementation of OAuth2AuthorizationConsentService.
 * Follows the same pattern as Spring's JdbcOAuth2AuthorizationConsentService but uses MongoDB as the data store.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
public class MongoOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {

    private final OAuth2AuthorizationConsentRepository authorizationConsentRepository;
    private final RegisteredClientRepository registeredClientRepository;

    public MongoOAuth2AuthorizationConsentService(
            OAuth2AuthorizationConsentRepository authorizationConsentRepository,
            RegisteredClientRepository registeredClientRepository) {
        Assert.notNull(authorizationConsentRepository, "authorizationConsentRepository cannot be null");
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        this.authorizationConsentRepository = authorizationConsentRepository;
        this.registeredClientRepository = registeredClientRepository;
    }

    @Override
    public void save(org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        
        OAuth2AuthorizationConsent entity = toEntity(authorizationConsent);
        authorizationConsentRepository.save(entity);
    }

    @Override
    public void remove(org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        
        authorizationConsentRepository.deleteByRegisteredClientIdAndPrincipalName(
                authorizationConsent.getRegisteredClientId(),
                authorizationConsent.getPrincipalName()
        );
    }

    @Override
    public org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent findById(
            String registeredClientId, String principalName) {
        Assert.hasText(registeredClientId, "registeredClientId cannot be empty");
        Assert.hasText(principalName, "principalName cannot be empty");
        
        Optional<OAuth2AuthorizationConsent> consent = authorizationConsentRepository
                .findByRegisteredClientIdAndPrincipalName(registeredClientId, principalName);
        
        return consent.map(this::toObject).orElse(null);
    }

    private OAuth2AuthorizationConsent toEntity(
            org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent authorizationConsent) {
        OAuth2AuthorizationConsent entity = new OAuth2AuthorizationConsent();
        entity.setRegisteredClientId(authorizationConsent.getRegisteredClientId());
        entity.setPrincipalName(authorizationConsent.getPrincipalName());
        entity.setAuthorities(
                StringUtils.collectionToCommaDelimitedString(
                        authorizationConsent.getAuthorities().stream()
                                .map(authority -> authority.getAuthority())
                                .toList()
                )
        );
        return entity;
    }

    private org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent toObject(
            OAuth2AuthorizationConsent entity) {
        RegisteredClient registeredClient = this.registeredClientRepository.findById(entity.getRegisteredClientId());
        if (registeredClient == null) {
            throw new IllegalArgumentException("Registered client not found with id: " + entity.getRegisteredClientId());
        }

        org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent.Builder builder =
                org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent
                        .withId(entity.getRegisteredClientId(), entity.getPrincipalName());

        Set<String> authorities = resolveAuthorities(entity.getAuthorities());
        for (String authority : authorities) {
            builder.authority(new org.springframework.security.core.authority.SimpleGrantedAuthority(authority));
        }

        return builder.build();
    }

    private static Set<String> resolveAuthorities(String authorities) {
        if (StringUtils.hasText(authorities)) {
            return Set.of(StringUtils.commaDelimitedListToStringArray(authorities));
        }
        return Set.of();
    }
}