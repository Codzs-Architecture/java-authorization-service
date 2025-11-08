package com.codzs.exception.type.oauth2;

import com.codzs.framework.exception.type.ServiceException;

/**
 * Exception thrown when authentication fails or is invalid.
 * This exception is specific to the authorization service authentication operations
 * and provides additional context for authentication failures.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
public class AuthenticationException extends ServiceException {

    private final String username;
    private final String authenticationType;
    private final String realm;

    /**
     * Constructs a new authentication exception with the specified message.
     * 
     * @param message the detail message
     */
    public AuthenticationException(String message) {
        super(message);
        this.username = null;
        this.authenticationType = null;
        this.realm = null;
    }

    /**
     * Constructs a new authentication exception with the specified message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
        this.username = null;
        this.authenticationType = null;
        this.realm = null;
    }

    /**
     * Constructs a new authentication exception with authentication context.
     * 
     * @param message the detail message
     * @param username the username that failed authentication
     * @param authenticationType the type of authentication that failed
     */
    public AuthenticationException(String message, String username, String authenticationType) {
        super(message);
        this.username = username;
        this.authenticationType = authenticationType;
        this.realm = null;
    }

    /**
     * Constructs a new authentication exception with full context.
     * 
     * @param message the detail message
     * @param username the username that failed authentication
     * @param authenticationType the type of authentication that failed
     * @param realm the authentication realm
     */
    public AuthenticationException(String message, String username, String authenticationType, String realm) {
        super(message);
        this.username = username;
        this.authenticationType = authenticationType;
        this.realm = realm;
    }

    /**
     * Constructs a new authentication exception with context and cause.
     * 
     * @param message the detail message
     * @param username the username that failed authentication
     * @param authenticationType the type of authentication that failed
     * @param cause the cause of this exception
     */
    public AuthenticationException(String message, String username, String authenticationType, Throwable cause) {
        super(message, cause);
        this.username = username;
        this.authenticationType = authenticationType;
        this.realm = null;
    }

    /**
     * Gets the username associated with this authentication failure.
     * 
     * @return the username, or null if not provided
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the authentication type that failed.
     * 
     * @return the authentication type, or null if not provided
     */
    public String getAuthenticationType() {
        return authenticationType;
    }

    /**
     * Gets the authentication realm.
     * 
     * @return the realm, or null if not provided
     */
    public String getRealm() {
        return realm;
    }

    /**
     * Checks if this exception has authentication context.
     * 
     * @return true if username or authentication type is present, false otherwise
     */
    public boolean hasAuthenticationContext() {
        return (username != null && !username.trim().isEmpty()) ||
               (authenticationType != null && !authenticationType.trim().isEmpty());
    }

    /**
     * Creates an authentication exception for invalid credentials.
     * 
     * @param username the username with invalid credentials
     * @return AuthenticationException for invalid credentials
     */
    public static AuthenticationException invalidCredentials(String username) {
        return new AuthenticationException("Invalid credentials provided", username, "credentials");
    }

    /**
     * Creates an authentication exception for missing authentication.
     * 
     * @return AuthenticationException for missing authentication
     */
    public static AuthenticationException missingAuthentication() {
        return new AuthenticationException("Authentication is required but not provided");
    }

    /**
     * Creates an authentication exception for expired authentication.
     * 
     * @param username the username with expired authentication
     * @return AuthenticationException for expired authentication
     */
    public static AuthenticationException expiredAuthentication(String username) {
        return new AuthenticationException("Authentication has expired", username, "expired");
    }
} 