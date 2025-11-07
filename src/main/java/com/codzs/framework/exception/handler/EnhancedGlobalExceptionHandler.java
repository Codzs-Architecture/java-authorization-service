package com.codzs.framework.exception.handler;

import com.codzs.framework.constant.HeaderConstant;
import com.codzs.framework.exception.type.BusinessException;
import com.codzs.framework.exception.bean.StandardErrorResponse;
import com.codzs.framework.exception.bean.ValidationError;
import com.codzs.framework.exception.context.MultiTenantErrorContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Enhanced global exception handler that extends the existing capabilities
 * with multi-tenant context and business-specific error handling.
 * 
 * @author CodeGeneration Framework
 * @since 1.0
 */
@Slf4j
@ControllerAdvice
public class EnhancedGlobalExceptionHandler {

    /**
     * Handles business exceptions with proper HTTP status mapping.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardErrorResponse> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        
        String errorId = generateErrorId();
        MultiTenantErrorContext context = extractMultiTenantContext(request);
        
        log.warn("Business exception [{}]: {} - Organization: {}, Tenant: {}", 
                errorId, ex.getMessage(), context.getOrganizationId(), context.getTenantId());
        
        StandardErrorResponse response = StandardErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(ex.getHttpStatus().value())
                .error(ex.getHttpStatus().getReasonPhrase())
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .multiTenantContext(context)
                .build();
        
        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    /**
     * Handles validation errors from @Valid annotations.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        String errorId = generateErrorId();
        MultiTenantErrorContext context = extractMultiTenantContext(request);
        
        List<ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .collect(Collectors.toList());
        
        log.warn("Validation exception [{}]: {} validation errors - Organization: {}", 
                errorId, validationErrors.size(), context.getOrganizationId());
        
        StandardErrorResponse response = StandardErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Request validation failed")
                .errorCode("VALIDATION_FAILED")
                .path(request.getRequestURI())
                .method(request.getMethod())
                .validationErrors(validationErrors)
                .multiTenantContext(context)
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles constraint violation exceptions from method-level validation.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<StandardErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        String errorId = generateErrorId();
        MultiTenantErrorContext context = extractMultiTenantContext(request);
        
        List<ValidationError> validationErrors = ex.getConstraintViolations()
                .stream()
                .map(this::mapConstraintViolation)
                .collect(Collectors.toList());
        
        log.warn("Constraint violation [{}]: {} violations - Organization: {}", 
                errorId, validationErrors.size(), context.getOrganizationId());
        
        StandardErrorResponse response = StandardErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Constraint Violation")
                .message("Request constraint validation failed")
                .errorCode("CONSTRAINT_VIOLATION")
                .path(request.getRequestURI())
                .method(request.getMethod())
                .validationErrors(validationErrors)
                .multiTenantContext(context)
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles missing request parameter exceptions.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<StandardErrorResponse> handleMissingParameterException(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        
        String errorId = generateErrorId();
        MultiTenantErrorContext context = extractMultiTenantContext(request);
        
        log.warn("Missing parameter [{}]: {} - Organization: {}", 
                errorId, ex.getMessage(), context.getOrganizationId());
        
        StandardErrorResponse response = StandardErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Missing Parameter")
                .message(String.format("Required parameter '%s' is missing", ex.getParameterName()))
                .errorCode("MISSING_PARAMETER")
                .path(request.getRequestURI())
                .method(request.getMethod())
                .multiTenantContext(context)
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles missing request header exceptions.
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<StandardErrorResponse> handleMissingHeaderException(
            MissingRequestHeaderException ex, HttpServletRequest request) {
        
        String errorId = generateErrorId();
        MultiTenantErrorContext context = extractMultiTenantContext(request);
        
        log.warn("Missing header [{}]: {} - Organization: {}", 
                errorId, ex.getMessage(), context.getOrganizationId());
        
        StandardErrorResponse response = StandardErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Missing Header")
                .message(String.format("Required header '%s' is missing", ex.getHeaderName()))
                .errorCode("MISSING_HEADER")
                .path(request.getRequestURI())
                .method(request.getMethod())
                .multiTenantContext(context)
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles method argument type mismatch exceptions.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<StandardErrorResponse> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        String errorId = generateErrorId();
        MultiTenantErrorContext context = extractMultiTenantContext(request);
        
        log.warn("Type mismatch [{}]: {} - Organization: {}", 
                errorId, ex.getMessage(), context.getOrganizationId());
        
        StandardErrorResponse response = StandardErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Type Mismatch")
                .message(String.format("Parameter '%s' should be of type %s", 
                        ex.getName(), ex.getRequiredType().getSimpleName()))
                .errorCode("TYPE_MISMATCH")
                .path(request.getRequestURI())
                .method(request.getMethod())
                .multiTenantContext(context)
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles HTTP method not supported exceptions.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<StandardErrorResponse> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        
        String errorId = generateErrorId();
        MultiTenantErrorContext context = extractMultiTenantContext(request);
        
        log.warn("Method not supported [{}]: {} - Organization: {}", 
                errorId, ex.getMessage(), context.getOrganizationId());
        
        StandardErrorResponse response = StandardErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .error("Method Not Allowed")
                .message(String.format("HTTP method '%s' is not supported for this endpoint", ex.getMethod()))
                .errorCode("METHOD_NOT_ALLOWED")
                .path(request.getRequestURI())
                .method(request.getMethod())
                .multiTenantContext(context)
                .build();
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    /**
     * Handles HTTP media type not supported exceptions.
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<StandardErrorResponse> handleMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        
        String errorId = generateErrorId();
        MultiTenantErrorContext context = extractMultiTenantContext(request);
        
        log.warn("Media type not supported [{}]: {} - Organization: {}", 
                errorId, ex.getMessage(), context.getOrganizationId());
        
        StandardErrorResponse response = StandardErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .error("Unsupported Media Type")
                .message("Content type is not supported")
                .errorCode("UNSUPPORTED_MEDIA_TYPE")
                .path(request.getRequestURI())
                .method(request.getMethod())
                .multiTenantContext(context)
                .build();
        
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    /**
     * Handles malformed JSON exceptions.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardErrorResponse> handleMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        
        String errorId = generateErrorId();
        MultiTenantErrorContext context = extractMultiTenantContext(request);
        
        log.warn("Message not readable [{}]: {} - Organization: {}", 
                errorId, ex.getMessage(), context.getOrganizationId());
        
        StandardErrorResponse response = StandardErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Malformed Request")
                .message("Request body is malformed or unreadable")
                .errorCode("MALFORMED_REQUEST")
                .path(request.getRequestURI())
                .method(request.getMethod())
                .multiTenantContext(context)
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles resource not found exceptions (404).
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<StandardErrorResponse> handleNoResourceFoundException(
            NoResourceFoundException ex, HttpServletRequest request) {
        
        String errorId = generateErrorId();
        MultiTenantErrorContext context = extractMultiTenantContext(request);
        
        log.warn("Resource not found [{}]: {} - Organization: {}", 
                errorId, ex.getMessage(), context.getOrganizationId());
        
        StandardErrorResponse response = StandardErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message("The requested resource was not found")
                .errorCode("RESOURCE_NOT_FOUND")
                .path(request.getRequestURI())
                .method(request.getMethod())
                .multiTenantContext(context)
                .build();
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Handles all other unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        String errorId = generateErrorId();
        MultiTenantErrorContext context = extractMultiTenantContext(request);
        
        log.error("Unexpected exception [{}]: {} - Organization: {}", 
                errorId, ex.getMessage(), context.getOrganizationId(), ex);
        
        StandardErrorResponse response = StandardErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .errorCode("INTERNAL_ERROR")
                .path(request.getRequestURI())
                .method(request.getMethod())
                .multiTenantContext(context)
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Helper methods

    private String generateErrorId() {
        return UUID.randomUUID().toString();
    }

    private MultiTenantErrorContext extractMultiTenantContext(HttpServletRequest request) {
        return MultiTenantErrorContext.builder()
                .organizationId(request.getHeader(HeaderConstant.HEADER_ORGANIZATION_ID))
                .tenantId(request.getHeader(HeaderConstant.HEADER_TENANT_ID))
                .correlationId(request.getHeader(HeaderConstant.HEADER_CORRELATION_ID))
                .requestPath(request.getRequestURI())
                .httpMethod(request.getMethod())
                .userAgent(request.getHeader("User-Agent"))
                .clientIp(getClientIpAddress(request))
                .build();
    }

    private String getClientIpAddress(HttpServletRequest request) {
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

    private ValidationError mapFieldError(FieldError fieldError) {
        return ValidationError.builder()
                .field(fieldError.getField())
                .rejectedValue(fieldError.getRejectedValue())
                .message(fieldError.getDefaultMessage())
                .build();
    }

    private ValidationError mapConstraintViolation(ConstraintViolation<?> violation) {
        return ValidationError.builder()
                .field(violation.getPropertyPath().toString())
                .rejectedValue(violation.getInvalidValue())
                .message(violation.getMessage())
                .build();
    }
}