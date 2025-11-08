package com.codzs.framework.exception.handler;

import com.codzs.framework.constant.HeaderConstant;
import com.codzs.framework.exception.bean.ValidationError;
import com.codzs.framework.exception.context.RequestContext;
import com.codzs.logger.constant.LoggerConstant;
import com.codzs.logger.context.CorrelationIdContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import org.slf4j.MDC;
import org.springframework.validation.FieldError;

import java.util.UUID;

/**
 * Base exception handler class providing common utility methods
 * for error handling across all exception handlers.
 * 
 * This class contains reusable methods for:
 * - Error ID generation
 * - Multi-tenant context extraction
 * - Client IP address resolution
 * - Validation error mapping
 * 
 * @author CodeGeneration Framework
 * @since 1.1
 */
public abstract class BaseExceptionHandler {

    // ===== ERROR ID GENERATION =====

    /**
     * Generates a unique error ID for tracking purposes.
     * 
     * @return unique error ID as UUID string
     */
    protected String generateErrorId() {
        return UUID.randomUUID().toString();
    }

    // ===== CONTEXT EXTRACTION =====

    /**
     * Extracts multi-tenant context information from HTTP request headers and framework context.
     * 
     * Correlation ID handling strategy:
     * 1. If correlation ID exists in ThreadLocal (from framework filter), use it
     * 2. If not, framework will generate a new one automatically
     * 3. Sets correlation ID in MDC for logging purposes
     * 
     * @param request the HTTP servlet request
     * @return RequestContext containing tenant, organization, and correlation information
     */
    protected RequestContext extractMultiTenantContext(HttpServletRequest request) {
        // Framework handles correlation ID: uses header if present, generates if not
        String correlationId = CorrelationIdContext.getOrGenerateCorrelationId();
        
        // Ensure correlation ID is in MDC for logging
        MDC.put(LoggerConstant.CORRELATION_ID, correlationId);
        
        return RequestContext.builder()
                .organizationId(request.getHeader(HeaderConstant.HEADER_ORGANIZATION_ID))
                .tenantId(request.getHeader(HeaderConstant.HEADER_TENANT_ID))
                .correlationId(correlationId)
                .requestPath(request.getRequestURI())
                .httpMethod(request.getMethod())
                .userAgent(request.getHeader("User-Agent"))
                .clientIp(getClientIpAddress(request))
                .build();
    }

    /**
     * Extracts the client IP address from the HTTP request.
     * Considers X-Forwarded-For and X-Real-IP headers for load balancer/proxy scenarios.
     * 
     * @param request the HTTP servlet request
     * @return client IP address
     */
    protected String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

    // ===== VALIDATION ERROR MAPPING =====

    /**
     * Maps Spring validation FieldError to framework ValidationError.
     * Used for converting @Valid annotation validation errors.
     * 
     * @param fieldError the Spring FieldError from validation
     * @return framework ValidationError object
     */
    protected ValidationError mapFieldError(FieldError fieldError) {
        return ValidationError.builder()
                .field(fieldError.getField())
                .rejectedValue(fieldError.getRejectedValue())
                .message(fieldError.getDefaultMessage())
                .build();
    }

    /**
     * Maps Bean Validation ConstraintViolation to framework ValidationError.
     * Used for converting method-level validation errors.
     * 
     * @param violation the Bean Validation ConstraintViolation
     * @return framework ValidationError object
     */
    protected ValidationError mapConstraintViolation(ConstraintViolation<?> violation) {
        return ValidationError.builder()
                .field(violation.getPropertyPath().toString())
                .rejectedValue(violation.getInvalidValue())
                .message(violation.getMessage())
                .build();
    }
}