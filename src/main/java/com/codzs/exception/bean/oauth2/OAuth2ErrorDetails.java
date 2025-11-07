package com.codzs.exception.bean.oauth2;

/**
 * Represents OAuth2-specific error details.
 * This class provides detailed information about OAuth2 errors
 * including error codes, descriptions, and OAuth2-specific metadata.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
public class OAuth2ErrorDetails {
    
    private String error;
    private String errorDescription;
    private String errorUri;

    // Private constructor for builder pattern
    private OAuth2ErrorDetails() {}

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public String getErrorUri() {
        return errorUri;
    }

    /**
     * Builder class for OAuth2ErrorDetails.
     */
    public static class Builder {
        private final OAuth2ErrorDetails oauth2ErrorDetails;

        public Builder() {
            this.oauth2ErrorDetails = new OAuth2ErrorDetails();
        }

        public Builder error(String error) {
            oauth2ErrorDetails.error = error;
            return this;
        }

        public Builder errorDescription(String errorDescription) {
            oauth2ErrorDetails.errorDescription = errorDescription;
            return this;
        }

        public Builder errorUri(String errorUri) {
            oauth2ErrorDetails.errorUri = errorUri;
            return this;
        }

        public OAuth2ErrorDetails build() {
            return oauth2ErrorDetails;
        }
    }
} 