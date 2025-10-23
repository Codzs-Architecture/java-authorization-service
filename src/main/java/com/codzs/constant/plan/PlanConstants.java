package com.codzs.constant.plan;

/**
 * Constants for Plan-related business rules and validation.
 * Centralizes plan configuration and business constraints.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public final class PlanConstants {

    // ========== PLAN BUSINESS CONSTRAINTS ==========
    
    /**
     * Maximum number of plans that can be active simultaneously
     */
    public static final int MAX_ACTIVE_PLANS = 100;
    
    /**
     * Minimum validity period for any plan (in days)
     */
    public static final int MIN_VALIDITY_PERIOD_DAYS = 1;
    
    /**
     * Maximum validity period for any plan (in days)
     */
    public static final int MAX_VALIDITY_PERIOD_DAYS = 3650; // 10 years
    
    /**
     * Default validity period for new plans (in days)
     */
    public static final int DEFAULT_VALIDITY_PERIOD_DAYS = 30;
    
    /**
     * Maximum number of users allowed in a plan (0 = unlimited)
     */
    public static final int MAX_PLAN_USER_LIMIT = 10000;
    
    /**
     * Maximum number of tenants allowed in a plan (0 = unlimited)
     */
    public static final int MAX_PLAN_TENANT_LIMIT = 1000;
    
    /**
     * Maximum storage limit in GB (0 = unlimited)
     */
    public static final long MAX_PLAN_STORAGE_LIMIT_GB = 10240; // 10TB
    
    /**
     * Maximum price for a plan in USD
     */
    public static final double MAX_PLAN_PRICE = 999999.99;

    // ========== PLAN TYPE HIERARCHIES ==========
    
    /**
     * Plan type hierarchy levels (higher number = higher level)
     */
    public static final int PLAN_LEVEL_BASIC = 1;
    public static final int PLAN_LEVEL_STANDARD = 2;
    public static final int PLAN_LEVEL_PREMIUM = 3;
    public static final int PLAN_LEVEL_ENTERPRISE = 4;
    public static final int PLAN_LEVEL_CUSTOM = 5;

    // ========== PLAN TRANSITIONS ==========
    
    /**
     * Minimum days before plan can be changed
     */
    public static final int MIN_PLAN_CHANGE_DAYS = 30;
    
    /**
     * Hours in advance for scheduling plan changes
     */
    public static final int PLAN_CHANGE_ADVANCE_HOURS = 24;

    // ========== PLAN VALIDATION ==========
    
    /**
     * Maximum length for plan name
     */
    public static final int MAX_PLAN_NAME_LENGTH = 100;
    
    /**
     * Maximum length for plan description
     */
    public static final int MAX_PLAN_DESCRIPTION_LENGTH = 1000;
    
    /**
     * Plan name validation pattern
     */
    public static final String PLAN_NAME_PATTERN = "^[a-zA-Z0-9\\s\\-_]+$";
    
    /**
     * Plan name validation pattern message
     */
    public static final String PLAN_NAME_PATTERN_MESSAGE = "Plan name can only contain letters, numbers, spaces, hyphens, and underscores";
    
    /**
     * Minimum value for max users in a plan.
     */
    public static final int MIN_PLAN_MAX_USERS = 1;
    
    /**
     * Minimum value for max tenants in a plan.
     */
    public static final int MIN_PLAN_MAX_TENANTS = 1;
    
    /**
     * Minimum value for storage limit in a plan.
     */
    public static final long MIN_PLAN_STORAGE_LIMIT = 0L;
    
    /**
     * Minimum value for plan price.
     */
    public static final String MIN_PLAN_PRICE = "0.0";

    // ========== PLAN TYPES ==========
    
    /**
     * Available plan types
     */
    public static final String PLAN_TYPE_BASIC = "BASIC";
    public static final String PLAN_TYPE_STANDARD = "STANDARD";
    public static final String PLAN_TYPE_PREMIUM = "PREMIUM";
    public static final String PLAN_TYPE_ENTERPRISE = "ENTERPRISE";
    public static final String PLAN_TYPE_CUSTOM = "CUSTOM";

    // ========== VALIDITY PERIOD UNITS ==========
    
    /**
     * Available validity period units
     */
    public static final String VALIDITY_UNIT_DAYS = "DAYS";
    public static final String VALIDITY_UNIT_MONTHS = "MONTHS";
    public static final String VALIDITY_UNIT_YEARS = "YEARS";

    // ========== ORGANIZATION TYPE COMPATIBILITY ==========
    
    /**
     * Organization types compatible with basic plans
     */
    public static final String[] BASIC_PLAN_COMPATIBLE_ORG_TYPES = {"INDIVIDUAL", "SMALL_BUSINESS"};
    
    /**
     * Organization types compatible with standard plans
     */
    public static final String[] STANDARD_PLAN_COMPATIBLE_ORG_TYPES = {"INDIVIDUAL", "SMALL_BUSINESS", "MEDIUM_BUSINESS"};
    
    /**
     * Organization types compatible with premium plans
     */
    public static final String[] PREMIUM_PLAN_COMPATIBLE_ORG_TYPES = {"SMALL_BUSINESS", "MEDIUM_BUSINESS", "LARGE_BUSINESS"};
    
    /**
     * Organization types compatible with enterprise plans
     */
    public static final String[] ENTERPRISE_PLAN_COMPATIBLE_ORG_TYPES = {"LARGE_BUSINESS", "ENTERPRISE"};

    // ========== ORGANIZATION SIZE COMPATIBILITY ==========
    
    /**
     * Organization sizes compatible with basic plans
     */
    public static final String[] BASIC_PLAN_COMPATIBLE_ORG_SIZES = {"SMALL"};
    
    /**
     * Organization sizes compatible with standard plans
     */
    public static final String[] STANDARD_PLAN_COMPATIBLE_ORG_SIZES = {"SMALL", "MEDIUM"};
    
    /**
     * Organization sizes compatible with premium plans
     */
    public static final String[] PREMIUM_PLAN_COMPATIBLE_ORG_SIZES = {"MEDIUM", "LARGE"};
    
    /**
     * Organization sizes compatible with enterprise plans
     */
    public static final String[] ENTERPRISE_PLAN_COMPATIBLE_ORG_SIZES = {"LARGE", "ENTERPRISE"};

    // ========== REGIONAL AVAILABILITY ==========
    
    /**
     * Default regions where all plans are available
     */
    public static final String[] DEFAULT_AVAILABLE_REGIONS = {"US", "EU", "APAC"};
    
    /**
     * Regions where enterprise plans are restricted
     */
    public static final String[] ENTERPRISE_RESTRICTED_REGIONS = {};

    // ========== PLAN CAPACITY LIMITS ==========
    
    /**
     * Maximum number of organizations per plan
     */
    public static final int MAX_ORGANIZATIONS_PER_PLAN = 10000;
    
    /**
     * Warning threshold for plan capacity (percentage)
     */
    public static final int PLAN_CAPACITY_WARNING_THRESHOLD = 80;

    // ========== PRIVATE CONSTRUCTOR ==========
    
    private PlanConstants() {
        // Utility class - prevent instantiation
    }
}