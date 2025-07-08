package com.codzs.service.authentication;

import java.security.Principal;

/**
 * Service interface for handling authentication operations.
 * This interface defines the contract for authentication-related business logic,
 * including user validation, authentication state management, and security operations.
 * 
 * @author Authentication Service Interface
 * @since 1.1
 */
public interface AuthenticationService {

    /**
     * Validate user authentication status.
     * Checks if the provided principal represents a valid authenticated user.
     * 
     * @param principal the user principal to validate
     * @return true if the user is properly authenticated, false otherwise
     */
    boolean isUserAuthenticated(Principal principal);

    /**
     * Get user display name from principal.
     * Extracts the appropriate display name for the user from the principal.
     * 
     * @param principal the user principal
     * @return the user's display name
     */
    String getUserDisplayName(Principal principal);

    /**
     * Check if user has specific role or authority.
     * Determines if the authenticated user has the specified role or authority.
     * 
     * @param principal the user principal
     * @param role the role or authority to check
     * @return true if the user has the specified role, false otherwise
     */
    boolean hasRole(Principal principal, String role);

    /**
     * Get authentication context information.
     * Provides contextual information about the current authentication state.
     * 
     * @param principal the user principal
     * @return AuthenticationContext containing authentication details
     */
    AuthenticationContext getAuthenticationContext(Principal principal);

    /**
     * Authentication context information.
     * Contains details about the current authentication state including
     * user information, authorities, and authentication metadata.
     */
    class AuthenticationContext {
        private final String username;
        private final String displayName;
        private final boolean authenticated;
        private final String authenticationType;

        public AuthenticationContext(String username, String displayName, boolean authenticated, String authenticationType) {
            this.username = username;
            this.displayName = displayName;
            this.authenticated = authenticated;
            this.authenticationType = authenticationType;
        }

        public String getUsername() {
            return username;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isAuthenticated() {
            return authenticated;
        }

        public String getAuthenticationType() {
            return authenticationType;
        }

        /**
         * Create an unauthenticated context.
         * 
         * @return AuthenticationContext for unauthenticated state
         */
        public static AuthenticationContext unauthenticated() {
            return new AuthenticationContext(null, null, false, null);
        }

        /**
         * Create an authenticated context.
         * 
         * @param username the username
         * @param displayName the display name
         * @param authenticationType the authentication type
         * @return AuthenticationContext for authenticated state
         */
        public static AuthenticationContext authenticated(String username, String displayName, String authenticationType) {
            return new AuthenticationContext(username, displayName, true, authenticationType);
        }
    }
} 