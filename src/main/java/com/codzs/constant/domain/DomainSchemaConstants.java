package com.codzs.constant.domain;

import com.codzs.constant.common.CommonSchemaConstants;

/**
 * Constants for domain schema documentation and defaults.
 * Provides fallback values for Swagger schema when dynamic configuration is unavailable.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public final class DomainSchemaConstants extends CommonSchemaConstants {

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
     * Maximum length for verification tokens.
     */
    public static final int VERIFICATION_TOKEN_MAX_LENGTH = 100;
    
    /**
     * Domain verification token expiry time in hours.
     * Verification tokens expire after this many hours.
     */
    public static final int DOMAIN_VERIFICATION_EXPIRY_HOURS = 24;

    // ========================= DYNAMIC FALLBACKS =========================
    
    /**
     * Default verification method example for schema documentation.
     * Actual default comes from DomainVerificationMethodEnum.getDefaultValue().
     */
    public static final String DEFAULT_VERIFICATION_METHOD_EXAMPLE = "DNS";
    
    // ========================= SCHEMA DESCRIPTIONS =========================
    
    /**
     * Description for domain configuration class.
     */
    public static final String DOMAIN_CONFIGURATION_DESCRIPTION = 
        "Domain configuration for organization";
    
    /**
     * Description for domain response class.
     */
    public static final String DOMAIN_RESPONSE_DESCRIPTION = 
        "Domain response for embedded domain";
    
    /**
     * Description for domain unique identifier field.
     */
    public static final String DOMAIN_ID_DESCRIPTION = 
        "Domain unique identifier";
    
    /**
     * Description for domain name field.
     */
    public static final String DOMAIN_NAME_DESCRIPTION = 
        "Domain name";
    
    /**
     * Description for domain verification status field.
     */
    public static final String DOMAIN_IS_VERIFIED_DESCRIPTION = 
        "Whether domain ownership is verified";
    
    /**
     * Description for primary domain status field.
     */
    public static final String DOMAIN_IS_PRIMARY_DESCRIPTION = 
        "Whether this is the primary domain";
    
    /**
     * Description for verification token field.
     */
    public static final String VERIFICATION_TOKEN_DESCRIPTION = 
        "Token for domain verification";
    
    /**
     * Description for domain creation timestamp field.
     */
    public static final String DOMAIN_CREATED_DATE_DESCRIPTION = 
        "Domain creation timestamp";
    
    /**
     * Description for domain verification timestamp field.
     */
    public static final String DOMAIN_VERIFIED_DATE_DESCRIPTION = 
        "Domain verification timestamp";
    
    /**
     * Enhanced description for verification method field in response DTOs.
     */
    public static final String VERIFICATION_METHOD_DESCRIPTION = 
        "Domain verification method. Options and defaults are configurable via system settings. " +
        "Typical options include: DNS (TXT record), EMAIL (email verification), FILE (file upload). " +
        "Note: Actual available options and default values are determined by system configuration " +
        "and may differ from the examples shown here.";
    
    /**
     * Description for verification method in request DTOs.
     */
    public static final String VERIFICATION_METHOD_REQUEST_DESCRIPTION = 
        "Domain verification method to use. Available options vary based on system configuration. " +
        "Use the verification-methods/options endpoint to get current available options and defaults. " +
        "Note: Actual available options are determined by system configuration.";

    
    // ========================= MESSAGE CONSTRAINTS =========================

    /**
     * Validation message for required domain name.
     */
    public static final String DOMAIN_NAME_REQUIRED_MESSAGE = 
        "Domain name is required";
    
    /**
     * Error message for domain name size validation.
     */
    public static final String DOMAIN_NAME_SIZE_MESSAGE = 
        "Domain name must be between " + DOMAIN_NAME_MIN_LENGTH + " and " + DOMAIN_NAME_MAX_LENGTH + " characters";
    
    /**
     * Validation message for required verification method.
     */
    public static final String VERIFICATION_METHOD_REQUIRED_MESSAGE = 
        "Verification method is required";
    
    /**
     * Validation message for invalid verification method.
     */
    public static final String VERIFICATION_METHOD_INVALID_MESSAGE = 
        "Invalid verification method";
    
    /**
     * Error message for verification token size validation.
     */
    public static final String VERIFICATION_TOKEN_SIZE_MESSAGE = 
        "Verification token must not exceed " + VERIFICATION_TOKEN_MAX_LENGTH + " characters";

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
     * Example domain verification status for documentation.
     */
    public static final String EXAMPLE_IS_VERIFIED = "false";
    
    /**
     * Example primary status for documentation.
     */
    public static final String EXAMPLE_IS_PRIMARY = "false";
    
    
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

    // ========================= UTILITY METHODS =========================
    
    /**
     * Common note for API documentation about dynamic configuration.
     */
    public static final String DYNAMIC_CONFIG_NOTE = 
        "Note: Actual available options and default values are determined by system configuration " +
        "and may differ from the examples shown here.";
    
    /**
     * Private constructor to prevent instantiation.
     */
    private DomainSchemaConstants() {
        super();
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}