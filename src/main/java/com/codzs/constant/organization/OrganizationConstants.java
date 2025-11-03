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

    public static final String ORGANIZATION_SETTING = "SETTING";
    public static final String ORGANIZATION_DOMAIN = "DOMAIN";
    public static final String ORGANIZATION_METADATA = "METADATA";
    public static final String ORGANIZATION_DATABASE = "DATABASE";

    public static final String SETTING_TIMEZONE = "timezone";
    public static final String SETTING_LANGUAGE = "language";
    public static final String SETTING_CURRENCY = "currency";
    public static final String SETTING_COUNTRY = "country";

    public static final Boolean DEFAULT_ORGANIZATION_PLAN_IS_ACTIVE = true;    

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
}