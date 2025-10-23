package com.codzs.constant.tenant;

/**
 * Constants for Tenant-related business rules and validation.
 * Centralizes tenant configuration and business constraints.
 * Used for maintaining consistency across DTOs, entities, and documentation.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public final class TenantConstants {

    // ========== VALIDATION CONSTANTS ==========
    
    /**
     * Minimum length for tenant name.
     */
    public static final int MIN_TENANT_NAME_LENGTH = 2;
    
    /**
     * Maximum length for tenant name.
     */
    public static final int MAX_TENANT_NAME_LENGTH = 100;
    
    /**
     * Maximum length for tenant code.
     */
    public static final int MAX_TENANT_CODE_LENGTH = 20;
    
    /**
     * Maximum length for tenant description.
     */
    public static final int MAX_TENANT_DESCRIPTION_LENGTH = 500;
    
    /**
     * Maximum length for tenant status.
     */
    public static final int MAX_TENANT_STATUS_LENGTH = 20;
    
    /**
     * Maximum length for tenant timezone.
     */
    public static final int MAX_TIMEZONE_LENGTH = 50;
    
    /**
     * Maximum length for tenant locale.
     */
    public static final int MAX_LOCALE_LENGTH = 10;
    
    /**
     * Maximum length for tenant currency.
     */
    public static final int MAX_CURRENCY_LENGTH = 10;

    // ========== VALIDATION PATTERNS ==========
    
    /**
     * Tenant code validation pattern (uppercase letters, numbers, and underscores only).
     */
    public static final String TENANT_CODE_PATTERN = "^[A-Z0-9_]+$";

    // ========== VALIDATION MESSAGES ==========
    
    /**
     * Tenant name required validation message.
     */
    public static final String TENANT_NAME_REQUIRED_MESSAGE = "Tenant name is required";
    
    /**
     * Tenant name size validation message.
     */
    public static final String TENANT_NAME_SIZE_MESSAGE = 
        "Tenant name must be between " + MIN_TENANT_NAME_LENGTH + " and " + MAX_TENANT_NAME_LENGTH + " characters";
    
    /**
     * Tenant code size validation message.
     */
    public static final String TENANT_CODE_SIZE_MESSAGE = 
        "Tenant code must not exceed " + MAX_TENANT_CODE_LENGTH + " characters";
    
    /**
     * Tenant code pattern validation message.
     */
    public static final String TENANT_CODE_PATTERN_MESSAGE = 
        "Tenant code must contain only uppercase letters, numbers, and underscores";
    
    /**
     * Tenant description size validation message.
     */
    public static final String TENANT_DESCRIPTION_SIZE_MESSAGE = 
        "Description must not exceed " + MAX_TENANT_DESCRIPTION_LENGTH + " characters";
    
    /**
     * Organization ID required validation message.
     */
    public static final String ORGANIZATION_ID_REQUIRED_MESSAGE = "Organization ID is required";
    
    /**
     * Tenant status required validation message.
     */
    public static final String TENANT_STATUS_REQUIRED_MESSAGE = "Tenant status is required";
    
    /**
     * Active status required validation message.
     */
    public static final String ACTIVE_STATUS_REQUIRED_MESSAGE = "Active status is required";
    
    /**
     * Max users validation message.
     */
    public static final String MAX_USERS_MIN_MESSAGE = "Max users must be at least 1";
    
    /**
     * Max departments validation message.
     */
    public static final String MAX_DEPARTMENTS_MIN_MESSAGE = "Max departments must be at least 1";
    
    /**
     * Storage limit validation message.
     */
    public static final String STORAGE_LIMIT_MIN_MESSAGE = "Storage limit must be non-negative";

    // ========== BUSINESS RULES ==========
    
    /**
     * Minimum number of users allowed per tenant.
     */
    public static final int MIN_MAX_USERS = 1;
    
    /**
     * Default maximum number of users per tenant.
     */
    public static final int DEFAULT_MAX_USERS = 100;
    
    /**
     * Maximum number of users allowed per tenant.
     */
    public static final int ABSOLUTE_MAX_USERS = 10000;
    
    /**
     * Minimum number of departments allowed per tenant.
     */
    public static final int MIN_MAX_DEPARTMENTS = 1;
    
    /**
     * Default maximum number of departments per tenant.
     */
    public static final int DEFAULT_MAX_DEPARTMENTS = 50;
    
    /**
     * Maximum number of departments allowed per tenant.
     */
    public static final int ABSOLUTE_MAX_DEPARTMENTS = 1000;
    
    /**
     * Minimum storage limit in bytes (0 = no limit).
     */
    public static final long MIN_STORAGE_LIMIT = 0L;
    
    /**
     * Default storage limit in GB.
     */
    public static final long DEFAULT_STORAGE_LIMIT_GB = 10L;
    
    /**
     * Maximum storage limit in GB.
     */
    public static final long MAX_STORAGE_LIMIT_GB = 1000L;

    // ========== DEFAULT VALUES ==========
    
    /**
     * Default tenant status for new tenants.
     */
    public static final String DEFAULT_TENANT_STATUS = "ACTIVE";
    
    /**
     * Default active status for new tenants.
     */
    public static final Boolean DEFAULT_IS_ACTIVE = true;

    // ========== EXAMPLE VALUES ==========
    
    /**
     * Example tenant name for documentation.
     */
    public static final String EXAMPLE_TENANT_NAME = "Engineering Department";
    
    /**
     * Example tenant code for documentation.
     */
    public static final String EXAMPLE_TENANT_CODE = "ENG_DEPT";
    
    /**
     * Example tenant description for documentation.
     */
    public static final String EXAMPLE_TENANT_DESCRIPTION = "Software engineering and development teams";

    // Prevent instantiation
    private TenantConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}