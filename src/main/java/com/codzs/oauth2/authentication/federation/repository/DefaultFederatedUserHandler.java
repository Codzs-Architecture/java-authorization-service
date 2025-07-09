package com.codzs.oauth2.authentication.federation.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Default implementation of {@link FederatedUserHandler}.
 * This handler provides basic federated user management including
 * user creation, linking, and synchronization.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
public class DefaultFederatedUserHandler implements FederatedUserHandler {

    private final Log logger = LogFactory.getLog(getClass());

    @Override
    public void handleOAuth2User(OAuth2User oauth2User) {
        if (logger.isDebugEnabled()) {
            logger.debug("Handling OAuth2User: " + oauth2User.getName());
        }
        
        // Default implementation - can be extended for:
        // - Account linking
        // - JIT provisioning
        // - User synchronization
        // - Audit logging
        
        // For now, this is a no-op to maintain backward compatibility
    }

    @Override
    public void handleOidcUser(OidcUser oidcUser) {
        if (logger.isDebugEnabled()) {
            logger.debug("Handling OidcUser: " + oidcUser.getName());
        }
        
        // Default implementation - can be extended for:
        // - Account linking
        // - JIT provisioning  
        // - User synchronization
        // - Audit logging
        
        // For now, this is a no-op to maintain backward compatibility
    }

    @Override
    public boolean supports(Class<?> userType) {
        return FederatedUserHandler.super.supports(userType);
    }
} 