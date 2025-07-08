package com.codzs.oauth2.authentication.device.error;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;

/**
 * Utility class for handling device authentication errors.
 * This class encapsulates error handling logic for device authentication,
 * following the Single Responsibility Principle.
 * 
 * @author Device Authentication Error Handler
 * @since 1.1
 */
public class DeviceAuthenticationErrorHandler {
    private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-3.2.1";

    /**
     * Throws an OAuth2AuthenticationException with invalid client error.
     * 
     * @param parameterName the parameter name that caused the error
     * @throws OAuth2AuthenticationException always thrown with invalid client error
     */
    public static void throwInvalidClient(String parameterName) {
        OAuth2Error error = new OAuth2Error(
                OAuth2ErrorCodes.INVALID_CLIENT,
                "Device client authentication failed: " + parameterName,
                ERROR_URI
        );
        throw new OAuth2AuthenticationException(error);
    }

    /**
     * Creates an OAuth2Error for invalid client scenarios.
     * 
     * @param parameterName the parameter name that caused the error
     * @return OAuth2Error configured for invalid client
     */
    public static OAuth2Error createInvalidClientError(String parameterName) {
        return new OAuth2Error(
                OAuth2ErrorCodes.INVALID_CLIENT,
                "Device client authentication failed: " + parameterName,
                ERROR_URI
        );
    }
} 