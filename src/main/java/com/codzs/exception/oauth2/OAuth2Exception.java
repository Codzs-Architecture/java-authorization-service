package com.codzs.exception.oauth2;

import com.codzs.exception.AuthorizationServiceException;

/**
 * Base exception class for OAuth2-related errors.
 * This exception extends the authorization service base exception
 * and provides OAuth2-specific error handling capabilities.
 * 
 * @author OAuth2 Exception Hierarchy
 * @since 1.1
 */
public class OAuth2Exception extends AuthorizationServiceException {

    private final String oauth2ErrorCode;
    private final String oauth2ErrorDescription;
    private final String oauth2ErrorUri;

    /**
     * Constructs a new OAuth2 exception with the specified message.
     * 
     * @param message the detail message
     */
    public OAuth2Exception(String message) {
        super(message);
        this.oauth2ErrorCode = null;
        this.oauth2ErrorDescription = null;
        this.oauth2ErrorUri = null;
    }

    /**
     * Constructs a new OAuth2 exception with the specified message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public OAuth2Exception(String message, Throwable cause) {
        super(message, cause);
        this.oauth2ErrorCode = null;
        this.oauth2ErrorDescription = null;
        this.oauth2ErrorUri = null;
    }

    /**
     * Constructs a new OAuth2 exception with OAuth2 error parameters.
     * 
     * @param oauth2ErrorCode the OAuth2 error code (e.g., "invalid_request")
     * @param oauth2ErrorDescription the human-readable error description
     */
    public OAuth2Exception(String oauth2ErrorCode, String oauth2ErrorDescription) {
        super(oauth2ErrorDescription);
        this.oauth2ErrorCode = oauth2ErrorCode;
        this.oauth2ErrorDescription = oauth2ErrorDescription;
        this.oauth2ErrorUri = null;
    }

    /**
     * Constructs a new OAuth2 exception with full OAuth2 error parameters.
     * 
     * @param oauth2ErrorCode the OAuth2 error code (e.g., "invalid_request")
     * @param oauth2ErrorDescription the human-readable error description
     * @param oauth2ErrorUri the URI of a web page with error information
     */
    public OAuth2Exception(String oauth2ErrorCode, String oauth2ErrorDescription, String oauth2ErrorUri) {
        super(oauth2ErrorDescription);
        this.oauth2ErrorCode = oauth2ErrorCode;
        this.oauth2ErrorDescription = oauth2ErrorDescription;
        this.oauth2ErrorUri = oauth2ErrorUri;
    }

    /**
     * Constructs a new OAuth2 exception with OAuth2 error parameters and cause.
     * 
     * @param oauth2ErrorCode the OAuth2 error code (e.g., "invalid_request")
     * @param oauth2ErrorDescription the human-readable error description
     * @param cause the cause of this exception
     */
    public OAuth2Exception(String oauth2ErrorCode, String oauth2ErrorDescription, Throwable cause) {
        super(oauth2ErrorDescription, cause);
        this.oauth2ErrorCode = oauth2ErrorCode;
        this.oauth2ErrorDescription = oauth2ErrorDescription;
        this.oauth2ErrorUri = null;
    }

    /**
     * Gets the OAuth2 error code.
     * 
     * @return the OAuth2 error code (e.g., "invalid_request", "unauthorized_client")
     */
    public String getOAuth2ErrorCode() {
        return oauth2ErrorCode;
    }

    /**
     * Gets the OAuth2 error description.
     * 
     * @return the human-readable error description
     */
    public String getOAuth2ErrorDescription() {
        return oauth2ErrorDescription;
    }

    /**
     * Gets the OAuth2 error URI.
     * 
     * @return the URI of a web page with error information, or null if not provided
     */
    public String getOAuth2ErrorUri() {
        return oauth2ErrorUri;
    }

    /**
     * Checks if this exception has OAuth2 error details.
     * 
     * @return true if OAuth2 error code is present, false otherwise
     */
    public boolean hasOAuth2ErrorDetails() {
        return oauth2ErrorCode != null && !oauth2ErrorCode.trim().isEmpty();
    }
} 