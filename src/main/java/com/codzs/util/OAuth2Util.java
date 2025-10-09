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
package com.codzs.util;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * Utility class for OAuth2 operations.
 * Centralizes common OAuth2 resolver methods to avoid code duplication.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
public final class OAuth2Util {

    private OAuth2Util() {
        // Utility class - prevent instantiation
    }

    /**
     * Resolves authorization grant type from string value.
     */
    public static AuthorizationGrantType resolveAuthorizationGrantType(String grantType) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(grantType)) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(grantType)) {
            return AuthorizationGrantType.CLIENT_CREDENTIALS;
        } else if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(grantType)) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        }
        return new AuthorizationGrantType(grantType);
    }

    /**
     * Resolves client authentication method from string value.
     */
    public static ClientAuthenticationMethod resolveClientAuthenticationMethod(String method) {
        if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue().equals(method)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        } else if (ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue().equals(method)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_POST;
        } else if (ClientAuthenticationMethod.NONE.getValue().equals(method)) {
            return ClientAuthenticationMethod.NONE;
        }
        return new ClientAuthenticationMethod(method);
    }

    /**
     * Resolves scopes from comma-delimited string.
     */
    public static Set<String> resolveScopes(String scope) {
        return StringUtils.hasText(scope) ? 
            Set.of(StringUtils.commaDelimitedListToStringArray(scope)) : 
            Set.of();
    }
}