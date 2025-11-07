package com.codzs.framework.exception.bean;

import com.codzs.framework.exception.context.MultiTenantErrorContext;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
    private LocalDateTime timestamp;
    
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
    private MultiTenantErrorContext multiTenantContext;
    
    /**
     * Validation errors (for validation failures)
     */
    private List<ValidationError> validationErrors;
    
    /**
     * Additional metadata about the error
     */
    private Object metadata;
}