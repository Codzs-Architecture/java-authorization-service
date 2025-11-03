package com.codzs.constant.common;

/**
 * Common constants shared across different schema constant classes.
 * Contains reusable UUID examples, timestamps, and other shared values for API documentation.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public class CommonSchemaConstants {
 
    // ========================= COMMON ENTITY ID EXAMPLES =========================
    
    /**
     * Example domain ID for documentation.
     */
    public static final String EXAMPLE_DOMAIN_ID = "550e8400-e29b-41d4-a716-446655440000";
    
    /**
     * Example organization ID for documentation.
     */
    public static final String EXAMPLE_ORGANIZATION_ID = "550e8400-e29b-41d4-a716-446655440000";
    
    /**
     * Example organization plan ID for documentation.
     */
    public static final String EXAMPLE_ORG_PLAN_ID = "550e8400-e29b-41d4-a716-446655440000";
    
    /**
     * Example parent organization ID for documentation.
     */
    public static final String EXAMPLE_PARENT_ORG_ID = "550e8400-e29b-41d4-a716-446655440001";
    
    /**
     * Example plan ID for documentation.
     */
    public static final String EXAMPLE_PLAN_ID = "550e8400-e29b-41d4-a716-446655440001";
    
    /**
     * Example tenant ID for documentation.
     */
    public static final String EXAMPLE_TENANT_ID = "550e8400-e29b-41d4-a716-446655440002";
    
    /**
     * Example correlation ID for documentation.
     */
    public static final String EXAMPLE_CORRELATION_ID = "req_550e8400-e29b-41d4-a716-446655440003";
    
    /**
     * Example schema ID for documentation.
     */
    public static final String EXAMPLE_SCHEMA_ID = "550e8400-e29b-41d4-a716-446655440005";
    
    /**
     * Example user ID list for documentation.
     */
    public static final String EXAMPLE_USER_ID_LIST = "[\"550e8400-e29b-41d4-a716-446655440000\"]";

    // ========================= COMMON TIMESTAMP EXAMPLES =========================
    
    /**
     * Example domain creation timestamp for documentation.
     */
    public static final String EXAMPLE_CREATED_ON = "2024-01-20T10:30:00Z";
    
    /**
     * Example domain verification timestamp for documentation.
     */
    public static final String EXAMPLE_MODIFIED_ON = "2024-01-20T16:30:00Z";
    
    /**
     * Example plan valid from timestamp for documentation.
     */
    public static final String EXAMPLE_START_FROM = "2024-01-20T16:30:00Z";
    
    /**
     * Example plan valid to timestamp for documentation.
     */
    public static final String EXAMPLE_END_TO = "2025-01-20T16:30:00Z";

    // ========================= COMMON VALIDATION CONSTANTS =========================
    
    /**
     * Private constructor to prevent instantiation.
     */
    protected CommonSchemaConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}