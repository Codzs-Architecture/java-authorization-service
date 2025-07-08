package com.codzs.exception.handler;

/**
 * Represents authentication context details.
 * This class provides specific information about authentication errors
 * including username, authentication type, and realm.
 * 
 * @author Authentication Context Details Model
 * @since 1.1
 */
public class AuthenticationContextDetails {
    
    private String username;
    private String authenticationType;
    private String realm;

    // Private constructor for builder pattern
    private AuthenticationContextDetails() {}

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public String getRealm() {
        return realm;
    }

    /**
     * Builder class for AuthenticationContextDetails.
     */
    public static class Builder {
        private final AuthenticationContextDetails authenticationContextDetails;

        public Builder() {
            this.authenticationContextDetails = new AuthenticationContextDetails();
        }

        public Builder username(String username) {
            authenticationContextDetails.username = username;
            return this;
        }

        public Builder authenticationType(String authenticationType) {
            authenticationContextDetails.authenticationType = authenticationType;
            return this;
        }

        public Builder realm(String realm) {
            authenticationContextDetails.realm = realm;
            return this;
        }

        public AuthenticationContextDetails build() {
            return authenticationContextDetails;
        }
    }
} 