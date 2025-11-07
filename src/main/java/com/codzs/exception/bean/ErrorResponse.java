package com.codzs.exception.bean;

import java.time.LocalDateTime;
import java.util.List;

import com.codzs.context.oauth2.AuthenticationContextDetails;
import com.codzs.context.oauth2.DeviceContextDetails;
import com.codzs.exception.bean.oauth2.OAuth2ErrorDetails;

/**
 * Represents a standardized error response.
 * This class provides a consistent structure for error responses
 * including error details, timestamps, and contextual information.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
public class ErrorResponse {
    
    private String errorId;
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<FieldError> fieldErrors;
    private OAuth2ErrorDetails oauth2Error;
    private DeviceContextDetails deviceContext;
    private AuthenticationContextDetails authenticationContext;

    // Private constructor for builder pattern
    private ErrorResponse() {}

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getErrorId() {
        return errorId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public OAuth2ErrorDetails getOauth2Error() {
        return oauth2Error;
    }

    public DeviceContextDetails getDeviceContext() {
        return deviceContext;
    }

    public AuthenticationContextDetails getAuthenticationContext() {
        return authenticationContext;
    }

    /**
     * Builder class for ErrorResponse.
     */
    public static class Builder {
        private final ErrorResponse errorResponse;

        public Builder() {
            this.errorResponse = new ErrorResponse();
        }

        public Builder errorId(String errorId) {
            errorResponse.errorId = errorId;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            errorResponse.timestamp = timestamp;
            return this;
        }

        public Builder status(int status) {
            errorResponse.status = status;
            return this;
        }

        public Builder error(String error) {
            errorResponse.error = error;
            return this;
        }

        public Builder message(String message) {
            errorResponse.message = message;
            return this;
        }

        public Builder path(String path) {
            errorResponse.path = path;
            return this;
        }

        public Builder fieldErrors(List<FieldError> fieldErrors) {
            errorResponse.fieldErrors = fieldErrors;
            return this;
        }

        public Builder oauth2Error(OAuth2ErrorDetails oauth2Error) {
            errorResponse.oauth2Error = oauth2Error;
            return this;
        }

        public Builder deviceContext(DeviceContextDetails deviceContext) {
            errorResponse.deviceContext = deviceContext;
            return this;
        }

        public Builder authenticationContext(AuthenticationContextDetails authenticationContext) {
            errorResponse.authenticationContext = authenticationContext;
            return this;
        }

        public ErrorResponse build() {
            return errorResponse;
        }
    }
} 