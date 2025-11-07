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

    // ========== Database Configuration Constants ==========
    
    /**
     * MongoDB connection string prefix.
     */
    public static final String MONGODB_PREFIX = "mongodb://";
    
    /**
     * MongoDB SRV connection string prefix.
     */
    public static final String MONGODB_SRV_PREFIX = "mongodb+srv://";
    
    /**
     * Localhost hostname (not allowed in production).
     */
    public static final String LOCALHOST = "localhost";
    
    /**
     * Local loopback IP address (not allowed in production).
     */
    public static final String LOCALHOST_IP = "127.0.0.1";
    
    /**
     * PEM certificate begin marker.
     */
    public static final String CERTIFICATE_BEGIN_MARKER = "-----BEGIN CERTIFICATE-----";
    
    /**
     * PEM certificate end marker.
     */
    public static final String CERTIFICATE_END_MARKER = "-----END CERTIFICATE-----";

    // ========== Domain Validation Constants ==========
    
    /**
     * Reserved IP address (not allowed for domains).
     */
    public static final String RESERVED_IP_ADDRESS = "0.0.0.0";
    
    /**
     * Platform domain (reserved).
     */
    public static final String PLATFORM_DOMAIN = "codzs.com";
    
    /**
     * API subdomain prefix (reserved).
     */
    public static final String API_SUBDOMAIN_PREFIX = "api.";
    
    /**
     * Admin subdomain prefix (reserved).
     */
    public static final String ADMIN_SUBDOMAIN_PREFIX = "admin.";
}