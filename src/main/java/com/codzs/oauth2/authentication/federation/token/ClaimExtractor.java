package com.codzs.oauth2.authentication.federation.token;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Utility class for extracting claims from federated identities.
 * This class encapsulates claim extraction logic for better separation
 * of concerns and improved testability.
 * 
 * @author Claim Extraction Utility
 * @since 1.1
 */
public class ClaimExtractor {

    /**
     * Standard ID token claims that should be filtered out to avoid conflicts.
     */
    public static final Set<String> STANDARD_ID_TOKEN_CLAIMS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            IdTokenClaimNames.ISS,
            IdTokenClaimNames.SUB,
            IdTokenClaimNames.AUD,
            IdTokenClaimNames.EXP,
            IdTokenClaimNames.IAT,
            IdTokenClaimNames.AUTH_TIME,
            IdTokenClaimNames.NONCE,
            IdTokenClaimNames.ACR,
            IdTokenClaimNames.AMR,
            IdTokenClaimNames.AZP,
            IdTokenClaimNames.AT_HASH,
            IdTokenClaimNames.C_HASH
    )));

    /**
     * Extracts claims from a federated identity authentication principal.
     * 
     * @param principal the authentication principal (can be OidcUser, OAuth2User, or other)
     * @return Map of claims extracted from the principal, empty map if unsupported type
     */
    public static Map<String, Object> extractClaims(Authentication principal) {
        Map<String, Object> claims;
        if (principal.getPrincipal() instanceof OidcUser oidcUser) {
            OidcIdToken idToken = oidcUser.getIdToken();
            claims = idToken.getClaims();
        } else if (principal.getPrincipal() instanceof OAuth2User oauth2User) {
            claims = oauth2User.getAttributes();
        } else {
            claims = Collections.emptyMap();
        }

        return new HashMap<>(claims);
    }

    /**
     * Filters out standard ID token claims from the provided claims map.
     * This prevents conflicts with claims set by the authorization server.
     * 
     * @param claims the claims map to filter
     * @return filtered claims map with standard ID token claims removed
     */
    public static Map<String, Object> filterStandardIdTokenClaims(Map<String, Object> claims) {
        Map<String, Object> filteredClaims = new HashMap<>(claims);
        STANDARD_ID_TOKEN_CLAIMS.forEach(filteredClaims::remove);
        return filteredClaims;
    }

    /**
     * Extracts and filters claims from a federated identity in one operation.
     * This is a convenience method that combines extraction and filtering.
     * 
     * @param principal the authentication principal
     * @return filtered claims map ready for use in ID tokens
     */
    public static Map<String, Object> extractAndFilterClaims(Authentication principal) {
        Map<String, Object> claims = extractClaims(principal);
        return filterStandardIdTokenClaims(claims);
    }
} 