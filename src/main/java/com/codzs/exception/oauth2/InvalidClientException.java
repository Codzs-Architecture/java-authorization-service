package com.codzs.exception.oauth2;

import org.springframework.security.oauth2.core.OAuth2ErrorCodes;

/**
 * Exception thrown when OAuth2 client validation fails.
 * This exception is specific to the authorization service OAuth2 client operations
 * and provides additional context for client validation failures.
 * 
 * @author Nitin Khaitan
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