package com.codzs.framework.exception.bean;

import com.codzs.framework.exception.context.RequestContext;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Standardized error response structure for all API errors.
 * Provides consistent error format across the application.
 * 
 * @author CodeGeneration Framework
 * @since 1.0
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardErrorResponse {
    
    /**
     * Unique identifier for this error occurrence
     */
    private String errorId;
    
    /**
     * Timestamp when the error occurred
     */
    private Instant timestamp;
    
    /**
     * HTTP status code
     */
    private int status;
    
    /**
     * HTTP status reason phrase
     */
    private String error;
    
    /**
     * Human-readable error message
     */
    private String message;
    
    /**
     * Application-specific error code
     */
    private String errorCode;
    
    /**
     * Request path that caused the error
     */
    private String path;
    
    /**
     * HTTP method used
     */
    private String method;
    
    /**
     * Multi-tenant context information
     */
    private RequestContext requestContext;
    
    /**
     * Validation errors (for validation failures)
     */
    private List<ValidationError> validationErrors;
    
    /**
     * Additional metadata about the error
     */
    private Object metadata;
    
    // ===== OAUTH2 SPECIFIC FIELDS (conditionally populated) =====
    
    /**
     * OAuth2 error code (e.g., "invalid_client", "invalid_grant")
     * Populated only for OAuth2-related errors
     */
    private String oauthError;
    
    /**
     * OAuth2 human-readable error description
     * Populated only for OAuth2-related errors
     */
    private String oauthErrorDescription;
    
    /**
     * OAuth2 documentation URI for more information about the error
     * Populated only for OAuth2-related errors
     */
    private String oauthErrorUri;
    
    /**
     * OAuth2 state parameter (for authorization flow)
     * Populated only for OAuth2 authorization flows
     */
    private String state;
    
    // ===== DEVICE FLOW FIELDS =====
    
    /**
     * Device flow device code
     * Populated only for device flow errors
     */
    private String deviceCode;
    
    /**
     * Device flow user code  
     * Populated only for device flow errors
     */
    private String userCode;
    
    /**
     * Device flow verification URI
     * Populated only for device flow errors
     */
    private String verificationUri;
    
    /**
     * Device flow polling interval in seconds
     * Populated only for device flow errors
     */
    private Integer interval;
    
    // ===== CORRELATION & TRACING =====
    
    /**
     * Correlation ID for tracking related operations across services
     */
    private String correlationId;
    
    /**
     * Creates a ResponseEntity with this error response and the specified HTTP status
     */
    public ResponseEntity<StandardErrorResponse> toResponseEntity(HttpStatus status) {
        return ResponseEntity.status(status).body(this);
    }
    
    /**
     * Builder class with enhanced methods for OAuth2 support
     */
    public static class StandardErrorResponseBuilder {
        
        /**
         * Sets OAuth2 error fields
         */
        public StandardErrorResponseBuilder oauth2Error(String error, String description) {
            this.oauthError = error;
            this.oauthErrorDescription = description;
            return this;
        }
        
        /**
         * Sets OAuth2 error with URI
         */
        public StandardErrorResponseBuilder oauth2Error(String error, String description, String uri) {
            this.oauthError = error;
            this.oauthErrorDescription = description;
            this.oauthErrorUri = uri;
            return this;
        }
        
        /**
         * Sets device flow specific fields
         */
        public StandardErrorResponseBuilder deviceFlow(String deviceCode, String userCode, String verificationUri) {
            this.deviceCode = deviceCode;
            this.userCode = userCode;
            this.verificationUri = verificationUri;
            return this;
        }
        
        /**
         * Sets device flow with polling interval
         */
        public StandardErrorResponseBuilder deviceFlow(String deviceCode, String userCode, String verificationUri, Integer interval) {
            this.deviceCode = deviceCode;
            this.userCode = userCode;
            this.verificationUri = verificationUri;
            this.interval = interval;
            return this;
        }
        
        /**
         * Generates and sets a new correlation ID
         */
        public StandardErrorResponseBuilder withCorrelationId() {
            this.correlationId = UUID.randomUUID().toString();
            return this;
        }
        
        /**
         * Sets current timestamp
         */
        public StandardErrorResponseBuilder withCurrentTimestamp() {
            this.timestamp = Instant.now();
            return this;
        }
    }
}