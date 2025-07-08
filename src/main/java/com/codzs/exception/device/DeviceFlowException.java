package com.codzs.exception.device;

import com.codzs.exception.oauth2.OAuth2Exception;

/**
 * Base exception class for OAuth2 device flow related errors.
 * This exception extends OAuth2Exception and provides device flow-specific
 * error handling capabilities.
 * 
 * @author Device Flow Exception Hierarchy
 * @since 1.1
 */
public class DeviceFlowException extends OAuth2Exception {

    private final String deviceCode;
    private final String userCode;

    /**
     * Constructs a new device flow exception with the specified message.
     * 
     * @param message the detail message
     */
    public DeviceFlowException(String message) {
        super(message);
        this.deviceCode = null;
        this.userCode = null;
    }

    /**
     * Constructs a new device flow exception with the specified message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public DeviceFlowException(String message, Throwable cause) {
        super(message, cause);
        this.deviceCode = null;
        this.userCode = null;
    }

    /**
     * Constructs a new device flow exception with OAuth2 error parameters.
     * 
     * @param oauth2ErrorCode the OAuth2 error code
     * @param oauth2ErrorDescription the human-readable error description
     */
    public DeviceFlowException(String oauth2ErrorCode, String oauth2ErrorDescription) {
        super(oauth2ErrorCode, oauth2ErrorDescription);
        this.deviceCode = null;
        this.userCode = null;
    }

    /**
     * Constructs a new device flow exception with device context.
     * 
     * @param oauth2ErrorCode the OAuth2 error code
     * @param oauth2ErrorDescription the human-readable error description
     * @param deviceCode the device code associated with this error
     * @param userCode the user code associated with this error
     */
    public DeviceFlowException(String oauth2ErrorCode, String oauth2ErrorDescription, 
                               String deviceCode, String userCode) {
        super(oauth2ErrorCode, oauth2ErrorDescription);
        this.deviceCode = deviceCode;
        this.userCode = userCode;
    }

    /**
     * Constructs a new device flow exception with OAuth2 error parameters and cause.
     * 
     * @param oauth2ErrorCode the OAuth2 error code
     * @param oauth2ErrorDescription the human-readable error description
     * @param cause the cause of this exception
     */
    public DeviceFlowException(String oauth2ErrorCode, String oauth2ErrorDescription, Throwable cause) {
        super(oauth2ErrorCode, oauth2ErrorDescription, cause);
        this.deviceCode = null;
        this.userCode = null;
    }

    /**
     * Gets the device code associated with this exception.
     * 
     * @return the device code, or null if not provided
     */
    public String getDeviceCode() {
        return deviceCode;
    }

    /**
     * Gets the user code associated with this exception.
     * 
     * @return the user code, or null if not provided
     */
    public String getUserCode() {
        return userCode;
    }

    /**
     * Checks if this exception has device flow context.
     * 
     * @return true if device code or user code is present, false otherwise
     */
    public boolean hasDeviceContext() {
        return (deviceCode != null && !deviceCode.trim().isEmpty()) ||
               (userCode != null && !userCode.trim().isEmpty());
    }
} 