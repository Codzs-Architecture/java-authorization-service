package com.codzs.framework.exception.context;

import lombok.Builder;
import lombok.Data;

/**
 * Request context information for error responses.
 * Captures request-specific context to aid in debugging and tracking.
 * 
 * @author CodeGeneration Framework
 * @since 1.0
 */
@Data
@Builder
public class RequestContext {
    
    /**
     * Organization ID from header or path
     */
    private String organizationId;
    
    /**
     * Tenant ID from header
     */
    private String tenantId;
    
    /**
     * Correlation ID for request tracing
     */
    private String correlationId;
    
    /**
     * Request path that caused the error
     */
    private String requestPath;
    
    /**
     * HTTP method
     */
    private String httpMethod;
    
    /**
     * User agent if available
     */
    private String userAgent;
    
    /**
     * Client IP address
     */
    private String clientIp;
}