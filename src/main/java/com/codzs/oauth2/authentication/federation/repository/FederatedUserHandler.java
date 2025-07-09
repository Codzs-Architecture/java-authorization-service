package com.codzs.oauth2.authentication.federation.repository;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Strategy interface for handling federated users.
 * This interface provides a pluggable way to handle federated user operations
 * such as account linking, JIT provisioning, or user synchronization.
 * 
 * Implementations of this interface can provide different strategies for
 * handling federated users (e.g., in-memory, database, external service).
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
public interface FederatedUserHandler {

    /**
     * Handles an OAuth2User after successful authentication.
     * This method can be used for account linking, JIT provisioning, or
     * user synchronization.
     * 
     * @param oauth2User the OAuth2User to handle
     */
    void handleOAuth2User(OAuth2User oauth2User);

    /**
     * Handles an OidcUser after successful authentication.
     * This method can be used for account linking, JIT provisioning, or
     * user synchronization.
     * 
     * @param oidcUser the OidcUser to handle
     */
    void handleOidcUser(OidcUser oidcUser);

    /**
     * Determines if this handler supports the given user type.
     * 
     * @param userType the user type to check
     * @return true if this handler supports the user type
     */
    default boolean supports(Class<?> userType) {
        return OAuth2User.class.isAssignableFrom(userType) || 
               OidcUser.class.isAssignableFrom(userType);
    }
} 