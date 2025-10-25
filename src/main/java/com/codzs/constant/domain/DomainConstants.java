package com.codzs.constant.domain;

/**
 * Constants for domain-related default values and configurations.
 * Used for maintaining consistency across DTOs, entities, and documentation.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public final class DomainConstants {

    // ========================= DEFAULT VALUES =========================
    
    /**
     * Default value for domain verification status.
     * New domains are unverified by default.
     */
    public static final String DEFAULT_IS_VERIFIED = "false";
    
    /**
     * Default value for primary domain status.
     * New domains are not primary by default.
     */
    public static final String DEFAULT_IS_PRIMARY = "false";
    
    /**
     * Default verification method for new domains.
     */
    public static final String DEFAULT_VERIFICATION_METHOD = "DNS";
    
    // ========================= EXAMPLE VALUES =========================
    
    /**
     * Example domain name for documentation.
     */
    public static final String EXAMPLE_DOMAIN_NAME = "codzs.com";
    
    /**
     * Example verification token for documentation.
     */
    public static final String EXAMPLE_VERIFICATION_TOKEN = "abc123xyz";
    
    /**
     * Example domain ID for documentation.
     */
    public static final String EXAMPLE_DOMAIN_ID = "550e8400-e29b-41d4-a716-446655440000";
    
    /**
     * Example domain verification status for documentation.
     */
    public static final String EXAMPLE_IS_VERIFIED = "true";
    
    /**
     * Example primary status for documentation.
     */
    public static final String EXAMPLE_IS_PRIMARY = "true";
    
    /**
     * Example domain creation timestamp for documentation.
     */
    public static final String EXAMPLE_CREATED_ON = "2024-01-20T10:30:00Z";
    
    /**
     * Example domain verification timestamp for documentation.
     */
    public static final String EXAMPLE_VERIFIED_ON = "2024-01-20T16:30:00Z";
    
    // ========================= BOOLEAN DEFAULTS =========================
    
    /**
     * Boolean default value for domain verification status.
     */
    public static final boolean BOOLEAN_DEFAULT_IS_VERIFIED = false;
    
    /**
     * Boolean default value for primary domain status.
     */
    public static final boolean BOOLEAN_DEFAULT_IS_PRIMARY = false;
    
    // ========================= VALIDATION PATTERNS =========================
    
    /**
     * Regular expression for validating domain names.
     * Supports standard domain name format with subdomains.
     */
    public static final String DOMAIN_NAME_PATTERN = 
        "^[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?(\\.[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?)*$";
    
    /**
     * Regular expression for business validation of domain names.
     * More strict pattern for business rules validation.
     */
    public static final String DOMAIN_BUSINESS_VALIDATION_PATTERN = 
        "^(?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.(?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.[A-Za-z]{2,}$";
    
    /**
     * Error message for invalid domain name format.
     */
    public static final String DOMAIN_NAME_PATTERN_MESSAGE = 
        "Domain name must be a valid domain format";
    
    // ========================= SIZE CONSTRAINTS =========================
    
    /**
     * Minimum length for domain names.
     */
    public static final int DOMAIN_NAME_MIN_LENGTH = 3;
    
    /**
     * Maximum length for domain names.
     */
    public static final int DOMAIN_NAME_MAX_LENGTH = 255;
    
    /**
     * Alias for business validation compatibility.
     */
    public static final int MAX_DOMAIN_NAME_LENGTH = DOMAIN_NAME_MAX_LENGTH;
    
    /**
     * Maximum length for verification tokens.
     */
    public static final int VERIFICATION_TOKEN_MAX_LENGTH = 100;
    
    /**
     * Error message for domain name size validation.
     */
    public static final String DOMAIN_NAME_SIZE_MESSAGE = 
        "Domain name must be between " + DOMAIN_NAME_MIN_LENGTH + " and " + DOMAIN_NAME_MAX_LENGTH + " characters";
    
    /**
     * Error message for verification token size validation.
     */
    public static final String VERIFICATION_TOKEN_SIZE_MESSAGE = 
        "Verification token must not exceed " + VERIFICATION_TOKEN_MAX_LENGTH + " characters";
    
    // ========================= TIMING CONSTRAINTS =========================
    
    /**
     * Domain verification token expiry time in hours.
     * Verification tokens expire after this many hours.
     */
    public static final int DOMAIN_VERIFICATION_EXPIRY_HOURS = 24;
    
    // ========================= CONSTRUCTOR =========================
    
    /**
     * Private constructor to prevent instantiation.
     */
    private DomainConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}