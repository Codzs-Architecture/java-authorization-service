package com.codzs.constant.organization;

/**
 * Constants for Organization business logic and validation.
 * Contains business rules, validation constraints, default values, and cache configurations.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public final class OrganizationConstants {
    // ========== Default Values ==========
    
    /**
     * Default role context type for organizations.
     */
    public static final String DEFAULT_ROLE_CONTEXT_TYPE = "ORGANIZATION";

    // ========== Validation Constants ==========
    
    /**
     * Maximum length for organization name.
     */
    public static final int MAX_NAME_LENGTH = 100;
    
    /**
     * Maximum length for organization abbreviation.
     */
    public static final int MAX_ABBR_LENGTH = 10;
    
    /**
     * Maximum length for organization display name.
     */
    public static final int MAX_DISPLAY_NAME_LENGTH = 255;
    
    /**
     * Maximum length for organization description.
     */
    public static final int MAX_DESCRIPTION_LENGTH = 1000;
    
    /**
     * Maximum length for language setting.
     */
    public static final int MAX_LANGUAGE_LENGTH = 50;
    
    /**
     * Maximum length for timezone setting.
     */
    public static final int MAX_TIMEZONE_LENGTH = 50;
    
    /**
     * Maximum length for currency setting.
     */
    public static final int MAX_CURRENCY_LENGTH = 10;
    
    /**
     * Maximum length for country setting.
     */
    public static final int MAX_COUNTRY_LENGTH = 10;
    
    /**
     * Minimum length for database schema name.
     */
    public static final int MIN_SCHEMA_NAME_LENGTH = 5;
    
    /**
     * Maximum length for database schema name.
     */
    public static final int MAX_SCHEMA_NAME_LENGTH = 100;
    
    /**
     * Maximum length for plan comment.
     */
    public static final int MAX_PLAN_COMMENT_LENGTH = 1000;

    // ========== Business Rules ==========
    
    /**
     * Maximum number of organizations a user can be part of.
     */
    public static final int MAX_ORGANIZATIONS_PER_USER = 10;
    
    /**
     * Maximum number of domains per organization.
     */
    public static final int MAX_DOMAINS_PER_ORGANIZATION = 5;
    
    /**
     * Maximum number of databases per organization.
     */
    public static final int MAX_DATABASES_PER_ORGANIZATION = 3;
    
    /** 
     * Maximum depth of organization hierarchy.
     */
    public static final int MAX_HIERARCHY_DEPTH = 5;
    
    /**
     * Length of domain verification token.
     */
    public static final int DOMAIN_VERIFICATION_TOKEN_LENGTH = 32;
    
    /**
     * Domain verification expiry time in hours.
     */
    public static final int DOMAIN_VERIFICATION_EXPIRY_HOURS = 24;

    // ========== Cache TTL (in seconds) ==========
    
    /**
     * Cache TTL for organization data (1 hour).
     */
    public static final long CACHE_TTL_ORGANIZATION = 3600;
    
    /**
     * Cache TTL for organization list data (30 minutes).
     */
    public static final long CACHE_TTL_ORGANIZATION_LIST = 1800;
    
    /**
     * Cache TTL for organization plans data (2 hours).
     */
    public static final long CACHE_TTL_ORGANIZATION_PLANS = 7200;

    // ========================= PATTERNS =========================
    
    /**
     * Regular expression pattern for database schema names.
     * Format: codzs_<org_abbr>_<service>_<env>
     */
    public static final String SCHEMA_NAME_PATTERN = "^codzs_[a-zA-Z0-9_]+_[a-zA-Z0-9_]+_[a-zA-Z0-9_]+$";

    // ========================= PATTERN MESSAGES =========================
    
    /**
     * Error message for invalid schema name pattern.
     */
    public static final String SCHEMA_NAME_PATTERN_MESSAGE = "Schema name must follow pattern: codzs_<org_abbr>_<service>_<env>";

    // ========================= VALIDATION MESSAGES =========================
    
    /**
     * Error message for organization name validation.
     */
    public static final String ORG_NAME_REQUIRED_MESSAGE = "Organization name is required";
    
    /**
     * Error message for organization abbreviation validation.
     */
    public static final String ORG_ABBR_REQUIRED_MESSAGE = "Organization abbreviation is required";
    
    /**
     * Error message for organization display name validation.
     */
    public static final String ORG_DISPLAY_NAME_REQUIRED_MESSAGE = "Organization display name is required";
    
    /**
     * Error message for organization type validation.
     */
    public static final String ORG_TYPE_REQUIRED_MESSAGE = "Organization type is required";
    
    /**
     * Error message for billing email validation.
     */
    public static final String BILLING_EMAIL_REQUIRED_MESSAGE = "Billing email is required";
    
    /**
     * Error message for billing email format validation.
     */
    public static final String BILLING_EMAIL_FORMAT_MESSAGE = "Billing email must be a valid email address";
    
    /**
     * Error message for service type validation.
     */
    public static final String SERVICE_TYPE_REQUIRED_MESSAGE = "Service type is required";
    
    /**
     * Error message for schema name validation.
     */
    public static final String SCHEMA_NAME_REQUIRED_MESSAGE = "Schema name is required";
}