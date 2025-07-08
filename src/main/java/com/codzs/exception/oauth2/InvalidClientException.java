package com.codzs.exception.oauth2;

import org.springframework.security.oauth2.core.OAuth2ErrorCodes;

/**
 * Exception thrown when an OAuth2 client is invalid or unauthorized.
 * This corresponds to the OAuth2 error code "invalid_client".
 * 
 * @author OAuth2 Exception Hierarchy
 * @since 1.1
 */
public class InvalidClientException extends OAuth2Exception {

    /**
     * Constructs a new invalid client exception with default message.
     */
    public InvalidClientException() {
        super(OAuth2ErrorCodes.INVALID_CLIENT, "The client authentication failed");
    }

    /**
     * Constructs a new invalid client exception with the specified description.
     * 
     * @param description the error description
     */
    public InvalidClientException(String description) {
        super(OAuth2ErrorCodes.INVALID_CLIENT, description);
    }

    /**
     * Constructs a new invalid client exception with the specified description and cause.
     * 
     * @param description the error description
     * @param cause the cause of this exception
     */
    public InvalidClientException(String description, Throwable cause) {
        super(OAuth2ErrorCodes.INVALID_CLIENT, description, cause);
    }

    /**
     * Constructs a new invalid client exception for a specific client.
     * 
     * @param clientId the client ID that failed authentication
     */
    public static InvalidClientException forClientId(String clientId) {
        return new InvalidClientException("Client authentication failed for client: " + clientId);
    }

    /**
     * Constructs a new invalid client exception for missing client credentials.
     * 
     * @return InvalidClientException for missing credentials
     */
    public static InvalidClientException missingCredentials() {
        return new InvalidClientException("Client credentials are missing");
    }

    /**
     * Constructs a new invalid client exception for unsupported authentication method.
     * 
     * @param method the unsupported authentication method
     * @return InvalidClientException for unsupported method
     */
    public static InvalidClientException unsupportedAuthenticationMethod(String method) {
        return new InvalidClientException("Unsupported client authentication method: " + method);
    }
} 