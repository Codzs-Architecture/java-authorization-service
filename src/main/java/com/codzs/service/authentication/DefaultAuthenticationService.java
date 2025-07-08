package com.codzs.service.authentication;

import java.security.Principal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link AuthenticationService}.
 * This service handles authentication operations including user validation,
 * authentication state management, and security operations.
 * 
 * @author Default Authentication Service Implementation
 * @since 1.1
 */
@Service
public class DefaultAuthenticationService implements AuthenticationService {

    private final Log logger = LogFactory.getLog(getClass());

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUserAuthenticated(Principal principal) {
        boolean authenticated = principal != null && 
                               principal instanceof Authentication &&
                               ((Authentication) principal).isAuthenticated();
        
        if (logger.isDebugEnabled()) {
            logger.debug("User authentication check: " + authenticated + 
                        " for principal: " + (principal != null ? principal.getName() : "null"));
        }
        
        return authenticated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserDisplayName(Principal principal) {
        if (principal == null) {
            return "Anonymous";
        }
        
        String displayName = principal.getName();
        
        if (logger.isDebugEnabled()) {
            logger.debug("User display name: " + displayName);
        }
        
        return displayName != null ? displayName : "Unknown User";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasRole(Principal principal, String role) {
        if (principal == null || !(principal instanceof Authentication)) {
            return false;
        }
        
        Authentication authentication = (Authentication) principal;
        boolean hasRole = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(authority -> authority.equals(role) || authority.equals("ROLE_" + role));
        
        if (logger.isDebugEnabled()) {
            logger.debug("Role check for '" + role + "': " + hasRole + 
                        " for user: " + authentication.getName());
        }
        
        return hasRole;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthenticationContext getAuthenticationContext(Principal principal) {
        if (principal == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Creating unauthenticated context");
            }
            return AuthenticationContext.unauthenticated();
        }
        
        String username = principal.getName();
        String displayName = getUserDisplayName(principal);
        String authenticationType = getAuthenticationType(principal);
        
        if (logger.isDebugEnabled()) {
            logger.debug("Creating authenticated context for user: " + username + 
                        ", type: " + authenticationType);
        }
        
        return AuthenticationContext.authenticated(username, displayName, authenticationType);
    }

    /**
     * Extract authentication type from principal.
     * 
     * @param principal the principal to analyze
     * @return the authentication type or "unknown"
     */
    private String getAuthenticationType(Principal principal) {
        if (principal instanceof Authentication) {
            Authentication auth = (Authentication) principal;
            return auth.getClass().getSimpleName();
        }
        return "unknown";
    }
} 