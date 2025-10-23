package com.codzs.constant.organization;

/**
 * Constants for Organization Swagger/OpenAPI documentation.
 * Contains example values, allowable values, and patterns used in API documentation.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public class OrganizationSwaggerConstants {
    // ========================= EXAMPLE VALUES =========================
    
    /**
     * Example organization name for documentation.
     */
    public static final String EXAMPLE_ORGANIZATION_NAME = "Codzs Corporation";
    
    /**
     * Example organization abbreviation for documentation.
     */
    public static final String EXAMPLE_ORGANIZATION_ABBR = "CODZS";
    
    /**
     * Example organization display name for documentation.
     */
    public static final String EXAMPLE_ORGANIZATION_DISPLAY_NAME = "Codzs Corporation Inc.";
    
    /**
     * Example organization description for documentation.
     */
    public static final String EXAMPLE_ORGANIZATION_DESCRIPTION = "Leading provider of enterprise solutions";
    
    /**
     * Example billing email for documentation.
     */
    public static final String EXAMPLE_BILLING_EMAIL = "billing@codzs.com";
    
    /**
     * Example expiry date for documentation.
     */
    public static final String EXAMPLE_EXPIRY_DATE = "2025-12-31T23:59:59Z";
    
    /**
     * Example user ID list for documentation.
     */
    public static final String EXAMPLE_USER_ID_LIST = "[\"550e8400-e29b-41d4-a716-446655440000\"]";
    
    /**
     * Example parent organization ID for documentation.
     */
    public static final String EXAMPLE_PARENT_ORG_ID = "550e8400-e29b-41d4-a716-446655440001";
    
    /**
     * Example organization ID for documentation.
     */
    public static final String EXAMPLE_ORGANIZATION_ID = "550e8400-e29b-41d4-a716-446655440000";
    
    // ========================= SETTINGS EXAMPLES =========================
    
    /**
     * Example timezone for documentation.
     */
    public static final String EXAMPLE_TIMEZONE = "America/New_York";
    
    /**
     * Example industry for documentation.
     */
    public static final String EXAMPLE_INDUSTRY = "TECHNOLOGY";
    
    /**
     * Example organization size for documentation.
     */
    public static final String EXAMPLE_SIZE = "11-200";
    
    // ========================= DATABASE EXAMPLES =========================
    
    /**
     * Example database connection string for documentation.
     */
    public static final String EXAMPLE_CONNECTION_STRING = "mongodb://localhost:27017/codzs_acme_auth_dev";
    
    /**
     * Example certificate for documentation.
     */
    public static final String EXAMPLE_CERTIFICATE = "-----BEGIN CERTIFICATE-----\\nMIIDXTCCAkWgAwIBAgIJAKL...\\n-----END CERTIFICATE-----";
    
    /**
     * Example service type for documentation.
     */
    public static final String EXAMPLE_SERVICE_TYPE = "auth";
    
    /**
     * Example schema name for documentation.
     */
    public static final String EXAMPLE_SCHEMA_NAME = "codzs_auth_dev";
    
    /**
     * Example schema description for documentation.
     */
    public static final String EXAMPLE_SCHEMA_DESCRIPTION = "Authentication service schema for Codzs organization";
    
    // ========================= PLAN EXAMPLES =========================
    
    /**
     * Example plan ID for documentation.
     */
    public static final String EXAMPLE_PLAN_ID = "550e8400-e29b-41d4-a716-446655440001";
    
    /**
     * Example plan comment for documentation.
     */
    public static final String EXAMPLE_PLAN_COMMENT = "Upgrading to premium plan for additional features";
    
    /**
     * Example plan valid from timestamp for documentation.
     */
    public static final String EXAMPLE_PLAN_VALID_FROM = "2024-01-20T16:30:00Z";
    
    /**
     * Example plan valid to timestamp for documentation.
     */
    public static final String EXAMPLE_PLAN_VALID_TO = "2025-01-20T16:30:00Z";
    
    /**
     * Example organization plan ID for documentation.
     */
    public static final String EXAMPLE_ORG_PLAN_ID = "550e8400-e29b-41d4-a716-446655440000";
    
    /**
     * Example plan association active status for documentation.
     */
    public static final String EXAMPLE_PLAN_ACTIVE = "true";
    
    // ========================= ORGANIZATION TYPE EXAMPLES =========================
    
    /**
     * Example organization type for documentation.
     */
    public static final String EXAMPLE_ORGANIZATION_TYPE = "ENTERPRISE";
    
    // ========================= ALLOWABLE VALUES =========================
    
    /**
     * Allowed industry types for organizations.
     */
    public static final String[] ALLOWED_INDUSTRIES_ARRAY = {
        "TECHNOLOGY", "FINANCE", "HEALTHCARE", "EDUCATION", "RETAIL", 
        "MANUFACTURING", "CONSULTING", "MEDIA", "NONPROFIT", "GOVERNMENT", 
        "REAL_ESTATE", "CONSTRUCTION", "TRANSPORTATION", "ENERGY", 
        "TELECOMMUNICATIONS", "OTHER"
    };
    
    /**
     * Allowed organization sizes.
     */
    public static final String[] ALLOWED_SIZES_ARRAY = {
        "1-10", "11-200", "201-500", "500+"
    };
    
    /**
     * Allowed service types for database schemas.
     */
    public static final String[] ALLOWED_SERVICE_TYPES_ARRAY = {
        "auth", "billing", "analytics", "audit", "resource", "bff"
    };
}