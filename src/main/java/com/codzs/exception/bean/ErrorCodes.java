package com.codzs.exception.bean;
// package com.codzs.exception.handler;

// /**
//  * Constants for error codes used throughout the application.
//  * This class provides centralized error code definitions for consistent
//  * error handling across the authorization service.
//  * 
//  * @author Nitin Khaitan
//  * @since 1.1
//  */
// public final class ErrorCodes {
    
//     // Private constructor to prevent instantiation
//     private ErrorCodes() {}
    
//     // OAuth2 Standard Error Codes
//     public static final String INVALID_REQUEST = "invalid_request";
//     public static final String INVALID_CLIENT = "invalid_client";
//     public static final String INVALID_GRANT = "invalid_grant";
//     public static final String UNAUTHORIZED_CLIENT = "unauthorized_client";
//     public static final String UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type";
//     public static final String INVALID_SCOPE = "invalid_scope";
//     public static final String ACCESS_DENIED = "access_denied";
//     public static final String UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type";
//     public static final String SERVER_ERROR = "server_error";
//     public static final String TEMPORARILY_UNAVAILABLE = "temporarily_unavailable";
    
//     // Device Flow Error Codes
//     public static final String AUTHORIZATION_PENDING = "authorization_pending";
//     public static final String SLOW_DOWN = "slow_down";
//     public static final String EXPIRED_TOKEN = "expired_token";
//     public static final String DEVICE_FLOW_DISABLED = "device_flow_disabled";
    
//     // Custom Application Error Codes
//     public static final String VALIDATION_ERROR = "validation_error";
//     public static final String AUTHENTICATION_ERROR = "authentication_error";
//     public static final String AUTHORIZATION_ERROR = "authorization_error";
//     public static final String CONFIGURATION_ERROR = "configuration_error";
//     public static final String INTERNAL_ERROR = "internal_error";
//     public static final String RESOURCE_NOT_FOUND = "resource_not_found";
//     public static final String METHOD_NOT_ALLOWED = "method_not_allowed";
//     public static final String UNSUPPORTED_MEDIA_TYPE = "unsupported_media_type";
    
//     // Client Authentication Error Codes
//     public static final String MISSING_CLIENT_CREDENTIALS = "missing_client_credentials";
//     public static final String INVALID_CLIENT_CREDENTIALS = "invalid_client_credentials";
//     public static final String UNSUPPORTED_CLIENT_AUTH_METHOD = "unsupported_client_auth_method";
    
//     // Token Error Codes
//     public static final String INVALID_TOKEN = "invalid_token";
//     public static final String INSUFFICIENT_SCOPE = "insufficient_scope";
//     public static final String TOKEN_EXPIRED = "token_expired";
//     public static final String TOKEN_REVOKED = "token_revoked";
    
//     // User Authentication Error Codes
//     public static final String INVALID_CREDENTIALS = "invalid_credentials";
//     public static final String ACCOUNT_LOCKED = "account_locked";
//     public static final String ACCOUNT_DISABLED = "account_disabled";
//     public static final String PASSWORD_EXPIRED = "password_expired";
//     public static final String MULTI_FACTOR_REQUIRED = "multi_factor_required";
    
//     // Validation Error Codes
//     public static final String REQUIRED_FIELD_MISSING = "required_field_missing";
//     public static final String INVALID_FORMAT = "invalid_format";
//     public static final String VALUE_OUT_OF_RANGE = "value_out_of_range";
//     public static final String INVALID_ENUM_VALUE = "invalid_enum_value";
//     public static final String DUPLICATE_VALUE = "duplicate_value";
    
//     // Rate Limiting Error Codes
//     public static final String RATE_LIMIT_EXCEEDED = "rate_limit_exceeded";
//     public static final String QUOTA_EXCEEDED = "quota_exceeded";
    
//     // Federation Error Codes
//     public static final String FEDERATION_ERROR = "federation_error";
//     public static final String IDENTITY_PROVIDER_ERROR = "identity_provider_error";
//     public static final String CLAIM_EXTRACTION_ERROR = "claim_extraction_error";
    
//     // Device Registration Error Codes
//     public static final String DEVICE_REGISTRATION_FAILED = "device_registration_failed";
//     public static final String DEVICE_NOT_FOUND = "device_not_found";
//     public static final String DEVICE_VERIFICATION_FAILED = "device_verification_failed";
    
//     // PKCE Error Codes
//     public static final String INVALID_CODE_VERIFIER = "invalid_code_verifier";
//     public static final String INVALID_CODE_CHALLENGE = "invalid_code_challenge";
//     public static final String UNSUPPORTED_CODE_CHALLENGE_METHOD = "unsupported_code_challenge_method";
    
//     // CORS Error Codes
//     public static final String CORS_ERROR = "cors_error";
//     public static final String ORIGIN_NOT_ALLOWED = "origin_not_allowed";
    
//     // Security Error Codes
//     public static final String SECURITY_VIOLATION = "security_violation";
//     public static final String CSRF_TOKEN_INVALID = "csrf_token_invalid";
//     public static final String SESSION_EXPIRED = "session_expired";
//     public static final String CONCURRENT_SESSION_LIMIT = "concurrent_session_limit";
// } 