package com.codzs.constant.department;

/**
 * Constants for Department-related business rules and validation.
 * Centralizes department configuration and business constraints.
 * Used for maintaining consistency across DTOs, entities, and documentation.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public final class DepartmentConstants {

    // ========== VALIDATION CONSTANTS ==========
    
    /**
     * Minimum length for department name.
     */
    public static final int MIN_DEPARTMENT_NAME_LENGTH = 2;
    
    /**
     * Maximum length for department name.
     */
    public static final int MAX_DEPARTMENT_NAME_LENGTH = 100;
    
    /**
     * Maximum length for department code.
     */
    public static final int MAX_DEPARTMENT_CODE_LENGTH = 20;
    
    /**
     * Maximum length for department description.
     */
    public static final int MAX_DEPARTMENT_DESCRIPTION_LENGTH = 500;
    
    /**
     * Maximum length for department status.
     */
    public static final int MAX_DEPARTMENT_STATUS_LENGTH = 20;
    
    /**
     * Maximum length for cost center.
     */
    public static final int MAX_COST_CENTER_LENGTH = 20;
    
    /**
     * Maximum length for department location.
     */
    public static final int MAX_LOCATION_LENGTH = 100;
    
    /**
     * Maximum length for department phone number.
     */
    public static final int MAX_PHONE_NUMBER_LENGTH = 20;
    
    /**
     * Maximum length for department email address.
     */
    public static final int MAX_EMAIL_ADDRESS_LENGTH = 100;
    
    /**
     * Maximum length for budget currency.
     */
    public static final int MAX_BUDGET_CURRENCY_LENGTH = 10;

    // ========== VALIDATION PATTERNS ==========
    
    /**
     * Department code validation pattern (uppercase letters, numbers, and underscores only).
     */
    public static final String DEPARTMENT_CODE_PATTERN = "^[A-Z0-9_]+$";
    
    /**
     * Cost center validation pattern (uppercase letters, numbers, and hyphens only).
     */
    public static final String COST_CENTER_PATTERN = "^[A-Z0-9-]+$";

    // ========== VALIDATION MESSAGES ==========
    
    /**
     * Department name required validation message.
     */
    public static final String DEPARTMENT_NAME_REQUIRED_MESSAGE = "Department name is required";
    
    /**
     * Department name size validation message.
     */
    public static final String DEPARTMENT_NAME_SIZE_MESSAGE = 
        "Department name must be between " + MIN_DEPARTMENT_NAME_LENGTH + " and " + MAX_DEPARTMENT_NAME_LENGTH + " characters";
    
    /**
     * Department code size validation message.
     */
    public static final String DEPARTMENT_CODE_SIZE_MESSAGE = 
        "Department code must not exceed " + MAX_DEPARTMENT_CODE_LENGTH + " characters";
    
    /**
     * Department code pattern validation message.
     */
    public static final String DEPARTMENT_CODE_PATTERN_MESSAGE = 
        "Department code must contain only uppercase letters, numbers, and underscores";
    
    /**
     * Department description size validation message.
     */
    public static final String DEPARTMENT_DESCRIPTION_SIZE_MESSAGE = 
        "Description must not exceed " + MAX_DEPARTMENT_DESCRIPTION_LENGTH + " characters";
    
    /**
     * Organization ID required validation message.
     */
    public static final String ORGANIZATION_ID_REQUIRED_MESSAGE = "Organization ID is required";
    
    /**
     * Department status required validation message.
     */
    public static final String DEPARTMENT_STATUS_REQUIRED_MESSAGE = "Department status is required";
    
    /**
     * Active status required validation message.
     */
    public static final String ACTIVE_STATUS_REQUIRED_MESSAGE = "Active status is required";
    
    /**
     * Cost center size validation message.
     */
    public static final String COST_CENTER_SIZE_MESSAGE = 
        "Cost center must not exceed " + MAX_COST_CENTER_LENGTH + " characters";
    
    /**
     * Cost center pattern validation message.
     */
    public static final String COST_CENTER_PATTERN_MESSAGE = 
        "Cost center must contain only uppercase letters, numbers, and hyphens";
    
    /**
     * Max users validation message.
     */
    public static final String MAX_USERS_MIN_MESSAGE = "Max users must be at least 1";
    
    /**
     * Hierarchy level validation message.
     */
    public static final String HIERARCHY_LEVEL_MIN_MESSAGE = "Hierarchy level must be non-negative";

    // ========== BUSINESS RULES ==========
    
    /**
     * Maximum hierarchy depth allowed for departments.
     */
    public static final int MAX_HIERARCHY_DEPTH = 5;
    
    /**
     * Minimum hierarchy level (root departments).
     */
    public static final int MIN_HIERARCHY_LEVEL = 0;
    
    /**
     * Maximum hierarchy level allowed.
     */
    public static final int MAX_HIERARCHY_LEVEL = MAX_HIERARCHY_DEPTH - 1;
    
    /**
     * Minimum number of users allowed per department.
     */
    public static final int MIN_MAX_USERS = 1;
    
    /**
     * Default maximum number of users per department.
     */
    public static final int DEFAULT_MAX_USERS = 50;
    
    /**
     * Maximum number of users allowed per department.
     */
    public static final int ABSOLUTE_MAX_USERS = 1000;
    
    /**
     * Maximum budget amount in any currency.
     */
    public static final double MAX_BUDGET_AMOUNT = 999999999.99;
    
    /**
     * Minimum budget amount.
     */
    public static final double MIN_BUDGET_AMOUNT = 0.0;

    // ========== DEFAULT VALUES ==========
    
    /**
     * Default department status for new departments.
     */
    public static final String DEFAULT_DEPARTMENT_STATUS = "ACTIVE";
    
    /**
     * Default active status for new departments.
     */
    public static final Boolean DEFAULT_IS_ACTIVE = true;
    
    /**
     * Default hierarchy level for root departments.
     */
    public static final Integer DEFAULT_HIERARCHY_LEVEL = 0;
    
    /**
     * Default budget currency for new departments.
     */
    public static final String DEFAULT_BUDGET_CURRENCY = "USD";

    // ========== EXAMPLE VALUES ==========
    
    /**
     * Example department name for documentation.
     */
    public static final String EXAMPLE_DEPARTMENT_NAME = "Software Engineering";
    
    /**
     * Example department code for documentation.
     */
    public static final String EXAMPLE_DEPARTMENT_CODE = "SW_ENG";
    
    /**
     * Example department description for documentation.
     */
    public static final String EXAMPLE_DEPARTMENT_DESCRIPTION = "Software development and engineering teams";
    
    /**
     * Example cost center for documentation.
     */
    public static final String EXAMPLE_COST_CENTER = "CC-ENG-001";
    
    /**
     * Example department location for documentation.
     */
    public static final String EXAMPLE_LOCATION = "New York Office, Floor 5";

    // Prevent instantiation
    private DepartmentConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}