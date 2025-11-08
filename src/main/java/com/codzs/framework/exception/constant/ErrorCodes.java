package com.codzs.framework.exception.constant;

/**
 * Generic error codes for framework-level exceptions.
 * These error codes are reusable across all microservices and represent
 * common HTTP, validation, and system-level errors.
 * 
 * @author CodeGeneration Framework
 * @since 1.0
 */
public final class ErrorCodes {
    
    // Private constructor to prevent instantiation
    private ErrorCodes() {}
    
    // ===== HTTP & REST API STANDARD ERRORS =====
    
    /**
     * Generic validation error
     */
    public static final String VALIDATION_ERROR = "validation_error";
    
    /**
     * Internal system error
     */
    public static final String INTERNAL_ERROR = "internal_error";
    
    /**
     * Requested resource not found
     */
    public static final String RESOURCE_NOT_FOUND = "resource_not_found";
    
    /**
     * HTTP method not allowed for endpoint
     */
    public static final String METHOD_NOT_ALLOWED = "method_not_allowed";
    
    /**
     * Unsupported media type in request
     */
    public static final String UNSUPPORTED_MEDIA_TYPE = "unsupported_media_type";
    
    /**
     * Bean validation constraint violation
     */
    public static final String CONSTRAINT_VIOLATION = "constraint_violation";
    
    /**
     * Required request parameter missing
     */
    public static final String MISSING_PARAMETER = "missing_parameter";
    
    /**
     * Required request header missing
     */
    public static final String MISSING_HEADER = "missing_header";
    
    /**
     * Parameter type mismatch
     */
    public static final String TYPE_MISMATCH = "type_mismatch";
    
    /**
     * Malformed request body
     */
    public static final String MALFORMED_REQUEST = "malformed_request";
    
    /**
     * System configuration error
     */
    public static final String CONFIGURATION_ERROR = "configuration_error";
    
    // ===== GENERIC VALIDATION ERRORS =====
    
    /**
     * Required field not provided
     */
    public static final String REQUIRED_FIELD_MISSING = "required_field_missing";
    
    /**
     * Field format is invalid
     */
    public static final String INVALID_FORMAT = "invalid_format";
    
    /**
     * Value is outside allowed range
     */
    public static final String VALUE_OUT_OF_RANGE = "value_out_of_range";
    
    /**
     * Invalid enumeration value
     */
    public static final String INVALID_ENUM_VALUE = "invalid_enum_value";
    
    /**
     * Duplicate value not allowed
     */
    public static final String DUPLICATE_VALUE = "duplicate_value";
    
    // ===== RATE LIMITING & THROTTLING =====
    
    /**
     * Request rate limit exceeded
     */
    public static final String RATE_LIMIT_EXCEEDED = "rate_limit_exceeded";
    
    /**
     * Usage quota exceeded
     */
    public static final String QUOTA_EXCEEDED = "quota_exceeded";
    
    // ===== GENERIC SECURITY ERRORS =====
    
    /**
     * General security violation
     */
    public static final String SECURITY_VIOLATION = "security_violation";
    
    /**
     * Invalid CSRF token
     */
    public static final String CSRF_TOKEN_INVALID = "csrf_token_invalid";
    
    /**
     * User session has expired
     */
    public static final String SESSION_EXPIRED = "session_expired";
    
    /**
     * Concurrent session limit reached
     */
    public static final String CONCURRENT_SESSION_LIMIT = "concurrent_session_limit";
    
    // ===== CORS ERRORS =====
    
    /**
     * Cross-origin request error
     */
    public static final String CORS_ERROR = "cors_error";
    
    /**
     * Origin not allowed for CORS
     */
    public static final String ORIGIN_NOT_ALLOWED = "origin_not_allowed";
}