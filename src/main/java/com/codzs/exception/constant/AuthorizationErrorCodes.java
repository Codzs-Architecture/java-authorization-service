package com.codzs.exception.constant;

/**
 * Authorization service specific error codes.
 * These error codes are specific to OAuth2, authentication, and authorization
 * operations as defined by relevant RFCs and authorization service requirements.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
public final class AuthorizationErrorCodes {
    
    // Private constructor to prevent instantiation
    private AuthorizationErrorCodes() {}
    
    // ===== OAUTH2 STANDARD ERROR CODES (RFC 6749) =====
    
    /**
     * OAuth2: The request is missing a required parameter, includes an unsupported parameter value
     */
    public static final String INVALID_REQUEST = "invalid_request";
    
    /**
     * OAuth2: Client authentication failed
     */
    public static final String INVALID_CLIENT = "invalid_client";
    
    /**
     * OAuth2: The provided authorization grant is invalid, expired, revoked
     */
    public static final String INVALID_GRANT = "invalid_grant";
    
    /**
     * OAuth2: The authenticated client is not authorized to use this authorization grant type
     */
    public static final String UNAUTHORIZED_CLIENT = "unauthorized_client";
    
    /**
     * OAuth2: The authorization grant type is not supported by the authorization server
     */
    public static final String UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type";
    
    /**
     * OAuth2: The requested scope is invalid, unknown, or malformed
     */
    public static final String INVALID_SCOPE = "invalid_scope";
    
    /**
     * OAuth2: The resource owner or authorization server denied the request
     */
    public static final String ACCESS_DENIED = "access_denied";
    
    /**
     * OAuth2: The authorization server does not support obtaining an authorization code
     */
    public static final String UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type";
    
    /**
     * OAuth2: The authorization server encountered an unexpected condition
     */
    public static final String SERVER_ERROR = "server_error";
    
    /**
     * OAuth2: The authorization server is currently unable to handle the request
     */
    public static final String TEMPORARILY_UNAVAILABLE = "temporarily_unavailable";
    
    // ===== OAUTH2 DEVICE FLOW ERROR CODES (RFC 8628) =====
    
    /**
     * Device Flow: The authorization request is still pending
     */
    public static final String AUTHORIZATION_PENDING = "authorization_pending";
    
    /**
     * Device Flow: The client is polling too frequently
     */
    public static final String SLOW_DOWN = "slow_down";
    
    /**
     * Device Flow: The device_code has expired
     */
    public static final String EXPIRED_TOKEN = "expired_token";
    
    /**
     * Device Flow: The device flow is disabled for this client
     */
    public static final String DEVICE_FLOW_DISABLED = "device_flow_disabled";
    
    // ===== CLIENT AUTHENTICATION ERROR CODES =====
    
    /**
     * Client credentials not provided in request
     */
    public static final String MISSING_CLIENT_CREDENTIALS = "missing_client_credentials";
    
    /**
     * Provided client credentials are invalid
     */
    public static final String INVALID_CLIENT_CREDENTIALS = "invalid_client_credentials";
    
    /**
     * Client authentication method not supported
     */
    public static final String UNSUPPORTED_CLIENT_AUTH_METHOD = "unsupported_client_auth_method";
    
    // ===== TOKEN ERROR CODES =====
    
    /**
     * The access token provided is invalid
     */
    public static final String INVALID_TOKEN = "invalid_token";
    
    /**
     * The request requires higher privileges than provided by the access token
     */
    public static final String INSUFFICIENT_SCOPE = "insufficient_scope";
    
    /**
     * The access token has expired
     */
    public static final String TOKEN_EXPIRED = "token_expired";
    
    /**
     * The access token has been revoked
     */
    public static final String TOKEN_REVOKED = "token_revoked";
    
    // ===== USER AUTHENTICATION ERROR CODES =====
    
    /**
     * General authentication error
     */
    public static final String AUTHENTICATION_ERROR = "authentication_error";
    
    /**
     * General authorization error
     */
    public static final String AUTHORIZATION_ERROR = "authorization_error";
    
    /**
     * Invalid username or password
     */
    public static final String INVALID_CREDENTIALS = "invalid_credentials";
    
    /**
     * User account is locked
     */
    public static final String ACCOUNT_LOCKED = "account_locked";
    
    /**
     * User account is disabled
     */
    public static final String ACCOUNT_DISABLED = "account_disabled";
    
    /**
     * User password has expired
     */
    public static final String PASSWORD_EXPIRED = "password_expired";
    
    /**
     * Multi-factor authentication required
     */
    public static final String MULTI_FACTOR_REQUIRED = "multi_factor_required";
    
    // ===== FEDERATION ERROR CODES =====
    
    /**
     * General federation error
     */
    public static final String FEDERATION_ERROR = "federation_error";
    
    /**
     * External identity provider error
     */
    public static final String IDENTITY_PROVIDER_ERROR = "identity_provider_error";
    
    /**
     * Failed to extract claims from identity token
     */
    public static final String CLAIM_EXTRACTION_ERROR = "claim_extraction_error";
    
    // ===== DEVICE REGISTRATION ERROR CODES =====
    
    /**
     * Device registration failed
     */
    public static final String DEVICE_REGISTRATION_FAILED = "device_registration_failed";
    
    /**
     * Device not found in registry
     */
    public static final String DEVICE_NOT_FOUND = "device_not_found";
    
    /**
     * Device verification failed
     */
    public static final String DEVICE_VERIFICATION_FAILED = "device_verification_failed";
    
    // ===== PKCE ERROR CODES (RFC 7636) =====
    
    /**
     * PKCE: Invalid code verifier
     */
    public static final String INVALID_CODE_VERIFIER = "invalid_code_verifier";
    
    /**
     * PKCE: Invalid code challenge
     */
    public static final String INVALID_CODE_CHALLENGE = "invalid_code_challenge";
    
    /**
     * PKCE: Unsupported code challenge method
     */
    public static final String UNSUPPORTED_CODE_CHALLENGE_METHOD = "unsupported_code_challenge_method";
}