package com.codzs.framework.exception.type;

/**
 * Base exception class for the OAuth2 Authorization Service.
 * This exception serves as the root of the custom exception hierarchy
 * for all authorization service related errors.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
public class ServiceException extends RuntimeException {

    private final String errorCode;
    private final Object[] arguments;

    /**
     * Constructs a new authorization service exception with the specified detail message.
     * 
     * @param message the detail message
     */
    public ServiceException(String message) {
        super(message);
        this.errorCode = null;
        this.arguments = null;
    }

    /**
     * Constructs a new authorization service exception with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.arguments = null;
    }

    /**
     * Constructs a new authorization service exception with error code and message.
     * 
     * @param errorCode the error code for this exception
     * @param message the detail message
     */
    public ServiceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.arguments = null;
    }

    /**
     * Constructs a new authorization service exception with error code, message, and arguments.
     * 
     * @param errorCode the error code for this exception
     * @param message the detail message
     * @param arguments the arguments for message formatting
     */
    public ServiceException(String errorCode, String message, Object... arguments) {
        super(message);
        this.errorCode = errorCode;
        this.arguments = arguments;
    }

    /**
     * Constructs a new authorization service exception with error code, message, cause, and arguments.
     * 
     * @param errorCode the error code for this exception
     * @param message the detail message
     * @param cause the cause of this exception
     * @param arguments the arguments for message formatting
     */
    public ServiceException(String errorCode, String message, Throwable cause, Object... arguments) {
        super(message, cause);
        this.errorCode = errorCode;
        this.arguments = arguments;
    }

    /**
     * Gets the error code associated with this exception.
     * 
     * @return the error code, or null if no error code is set
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Gets the arguments associated with this exception.
     * 
     * @return the arguments array, or null if no arguments are set
     */
    public Object[] getArguments() {
        return arguments != null ? arguments.clone() : null;
    }

    /**
     * Checks if this exception has an error code.
     * 
     * @return true if an error code is present, false otherwise
     */
    public boolean hasErrorCode() {
        return errorCode != null && !errorCode.trim().isEmpty();
    }
} 