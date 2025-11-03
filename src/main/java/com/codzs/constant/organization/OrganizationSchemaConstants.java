package com.codzs.constant.organization;

import com.codzs.constant.common.CommonSchemaConstants;

/**
 * Constants for Organization Swagger/OpenAPI documentation.
 * Contains example values, allowable values, and patterns used in API documentation.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public class OrganizationSchemaConstants extends CommonSchemaConstants {
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
    public static final String EXAMPLE_CONNECTION_STRING = "mongodb://localhost:27017/codzs_auth_dev";
    
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
     * Example plan comment for documentation.
     */
    public static final String EXAMPLE_PLAN_COMMENT = "Upgrading to premium plan for additional features";
    
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

    // ========================= PATTERNS =========================
    
    /**
     * Regular expression pattern for organization abbreviation.
     * Only uppercase alphanumeric characters allowed.
     */
    public static final String ORG_ABBR_PATTERN = "^[A-Z0-9]+$";
    
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

    // ========================= SCHEMA DESCRIPTIONS =========================
    
    /**
     * Description for database configuration class.
     */
    public static final String DATABASE_CONFIG_DESCRIPTION = "Database configuration for organization";
    
    /**
     * Description for database connection string field.
     */
    public static final String CONNECTION_STRING_DESCRIPTION = "Database connection string";
    
    /**
     * Description for database certificate field.
     */
    public static final String CERTIFICATE_DESCRIPTION = "Database certificate";
    
    /**
     * Description for database schema configuration class.
     */
    public static final String DATABASE_SCHEMA_DESCRIPTION = "Database schema configuration";
    
    /**
     * Description for service type field.
     */
    public static final String SERVICE_TYPE_DESCRIPTION = "Type of service this schema supports";
    
    /**
     * Description for schema name field.
     */
    public static final String SCHEMA_NAME_DESCRIPTION = "Database schema name";
    
    /**
     * Description for schema description field.
     */
    public static final String SCHEMA_DESCRIPTION_DESCRIPTION = "Schema description";
    
    /**
     * Description for organization creation request class.
     */
    public static final String ORG_CREATE_REQUEST_DESCRIPTION = "Request DTO for creating a new organization";
    
    /**
     * Description for organization update request class.
     */
    public static final String ORG_UPDATE_REQUEST_DESCRIPTION = "Request DTO for updating an existing organization";
    
    /**
     * Description for organization metadata class.
     */
    public static final String ORG_METADATA_DESCRIPTION = "Organization metadata for categorization";
    
    /**
     * Description for organization settings class.
     */
    public static final String ORG_SETTINGS_DESCRIPTION = "Organization setting configuration";
    
    /**
     * Description for organization plan request class.
     */
    public static final String ORG_PLAN_REQUEST_DESCRIPTION = "Organization plan association request";
    
    /**
     * Description for organization name field.
     */
    public static final String ORG_NAME_DESCRIPTION = "Organization name";
    
    /**
     * Description for organization abbreviation field.
     */
    public static final String ORG_ABBR_DESCRIPTION = "Organization abbreviation";
    
    /**
     * Description for organization display name field.
     */
    public static final String ORG_DISPLAY_NAME_DESCRIPTION = "Organization display name";
    
    /**
     * Description for organization description field.
     */
    public static final String ORG_DESCRIPTION_DESCRIPTION = "Organization description";
    
    /**
     * Description for organization type field.
     */
    public static final String ORG_TYPE_DESCRIPTION = "Organization type";
    
    /**
     * Description for billing email field.
     */
    public static final String BILLING_EMAIL_DESCRIPTION = "Primary billing contact email";
    
    /**
     * Description for organization expiration date field.
     */
    public static final String EXPIRY_DATE_DESCRIPTION = "Organization expiration date";
    
    /**
     * Description for owner user IDs field.
     */
    public static final String OWNER_USER_IDS_DESCRIPTION = "List of owner user IDs";
    
    /**
     * Description for parent organization ID field.
     */
    public static final String PARENT_ORG_ID_DESCRIPTION = "Parent organization ID";
    
    /**
     * Description for organization industry field.
     */
    public static final String INDUSTRY_DESCRIPTION = "Organization industry";
    
    /**
     * Description for organization size field.
     */
    public static final String SIZE_DESCRIPTION = "Organization size by employee count";
    
    /**
     * Description for organization default language field.
     */
    public static final String LANGUAGE_DESCRIPTION = "Organization default language";
    
    /**
     * Description for organization default timezone field.
     */
    public static final String TIMEZONE_DESCRIPTION = "Organization default timezone";
    
    /**
     * Description for organization default currency field.
     */
    public static final String CURRENCY_DESCRIPTION = "Organization default currency";
    
    /**
     * Description for organization country field.
     */
    public static final String COUNTRY_DESCRIPTION = "Organization country code";
    
    // ========================= RESPONSE DESCRIPTIONS =========================
    
    /**
     * Description for database configuration response class.
     */
    public static final String DATABASE_CONFIG_RESPONSE_DESCRIPTION = "Database configuration response";
    
    /**
     * Description for database schema response class.
     */
    public static final String DATABASE_SCHEMA_RESPONSE_DESCRIPTION = "Database schema response";
    
    /**
     * Description for organization metadata response class.
     */
    public static final String ORG_METADATA_RESPONSE_DESCRIPTION = "Organization metadata response";
    
    /**
     * Description for organization plan response class.
     */
    public static final String ORG_PLAN_RESPONSE_DESCRIPTION = "Organization plan association response";
    
    /**
     * Description for organization response class.
     */
    public static final String ORG_RESPONSE_DESCRIPTION = "Organization response";
    
    /**
     * Description for organization settings response class.
     */
    public static final String ORG_SETTINGS_RESPONSE_DESCRIPTION = "Organization setting response";
    
    /**
     * Description for organization summary response class.
     */
    public static final String ORG_SUMMARY_RESPONSE_DESCRIPTION = "Organization summary response";
    
    /**
     * Description for schema unique identifier field.
     */
    public static final String SCHEMA_ID_DESCRIPTION = "Schema unique identifier";
    
    /**
     * Description for organization ID field.
     */
    public static final String ORG_ID_DESCRIPTION = "Organization ID";
    
    /**
     * Description for plan ID field.
     */
    public static final String PLAN_ID_DESCRIPTION = "Plan ID";
    
    /**
     * Description for plan comment field.
     */
    public static final String PLAN_COMMENT_DESCRIPTION = "Comment for plan association";
    
    /**
     * Description for plan valid from field.
     */
    public static final String PLAN_VALID_FROM_DESCRIPTION = "Plan valid from timestamp";
    
    /**
     * Description for plan valid to field.
     */
    public static final String PLAN_VALID_TO_DESCRIPTION = "Plan valid to timestamp";
    
    /**
     * Description for plan active status field.
     */
    public static final String PLAN_ACTIVE_DESCRIPTION = "Whether this plan association is active";
    
    /**
     * Description for organization plan association ID field.
     */
    public static final String ORG_PLAN_ID_DESCRIPTION = "Organization plan association unique identifier";
    
    /**
     * Description for database configuration in organization response.
     */
    public static final String DATABASE_RESPONSE_DESCRIPTION = "Database configuration for the organization";
    
    /**
     * Description for organization setting in response.
     */
    public static final String SETTING_RESPONSE_DESCRIPTION = "Organization setting";
    
    /**
     * Description for organization metadata in response.
     */
    public static final String METADATA_RESPONSE_DESCRIPTION = "Organization metadata";
    
    /**
     * Description for organization domains in response.
     */
    public static final String DOMAINS_RESPONSE_DESCRIPTION = "Organization domains";
    
    /**
     * Description for organization status field.
     */
    public static final String STATUS_DESCRIPTION = "Organization status";
    
    /**
     * Description for database schemas list.
     */
    public static final String SCHEMAS_LIST_DESCRIPTION = "List of database schemas";
    
    /**
     * Description for database test results.
     */
    public static final String TEST_RESULTS_DESCRIPTION = "Database connection test results (only included when testConnection=true)";
    
    // ========================= VALIDATION MESSAGES =========================
    
    /**
     * Error message for connection string validation.
     */
    public static final String CONNECTION_STRING_REQUIRED_MESSAGE = "Connection string is required";
    
    /**
     * Error message for certificate validation.
     */
    public static final String CERTIFICATE_REQUIRED_MESSAGE = "Certificate is required";
    
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
    
    /**
     * Error message for organization name size validation.
     */
    public static final String ORG_NAME_SIZE_MESSAGE = "Organization name must be between 2 and " + MAX_NAME_LENGTH + " characters";
    
    /**
     * Error message for organization abbreviation size validation.
     */
    public static final String ORG_ABBR_SIZE_MESSAGE = "Organization abbreviation must be between 2 and " + MAX_ABBR_LENGTH + " characters";
    
    /**
     * Error message for organization abbreviation pattern validation.
     */
    public static final String ORG_ABBR_PATTERN_MESSAGE = "Organization abbreviation must contain only uppercase alphanumeric characters";
    
    /**
     * Error message for display name size validation.
     */
    public static final String DISPLAY_NAME_SIZE_MESSAGE = "Display name must be between 2 and " + MAX_DISPLAY_NAME_LENGTH + " characters";
    
    /**
     * Error message for description size validation.
     */
    public static final String DESCRIPTION_SIZE_MESSAGE = "Description must not exceed " + MAX_DESCRIPTION_LENGTH + " characters";
    
    /**
     * Error message for billing email size validation.
     */
    public static final String BILLING_EMAIL_SIZE_MESSAGE = "Billing email must not exceed 255 characters";
    
    /**
     * Error message for organization ID validation.
     */
    public static final String ORG_ID_REQUIRED_MESSAGE = "Organization ID is required";
    
    /**
     * Error message for plan ID validation.
     */
    public static final String PLAN_ID_REQUIRED_MESSAGE = "Plan ID is required";
    
    /**
     * Error message for comment validation.
     */
    public static final String COMMENT_REQUIRED_MESSAGE = "Comment is required";
    
    /**
     * Error message for comment size validation.
     */
    public static final String COMMENT_SIZE_MESSAGE = "Comment must not exceed " + MAX_PLAN_COMMENT_LENGTH + " characters";
    
    /**
     * Error message for language size validation.
     */
    public static final String LANGUAGE_SIZE_MESSAGE = "Language must not exceed " + MAX_LANGUAGE_LENGTH + " characters";
    
    /**
     * Error message for timezone size validation.
     */
    public static final String TIMEZONE_SIZE_MESSAGE = "Timezone must not exceed " + MAX_TIMEZONE_LENGTH + " characters";
    
    /**
     * Error message for currency size validation.
     */
    public static final String CURRENCY_SIZE_MESSAGE = "Currency must not exceed " + MAX_CURRENCY_LENGTH + " characters";
    
    /**
     * Error message for country size validation.
     */
    public static final String COUNTRY_SIZE_MESSAGE = "Country must not exceed " + MAX_COUNTRY_LENGTH + " characters";
    
    /**
     * Error message for connection string size validation.
     */
    public static final String CONNECTION_STRING_SIZE_MESSAGE = "Connection string must not exceed 1000 characters";
    
    /**
     * Error message for certificate size validation.
     */
    public static final String CERTIFICATE_SIZE_MESSAGE = "Certificate must not exceed 5000 characters";
    
    /**
     * Error message for invalid organization type.
     */
    public static final String ORG_TYPE_INVALID_MESSAGE = "Invalid organization type";
    
    /**
     * Error message for invalid organization industry.
     */
    public static final String INDUSTRY_INVALID_MESSAGE = "Invalid organization industry";
    
    /**
     * Error message for invalid organization size.
     */
    public static final String SIZE_INVALID_MESSAGE = "Invalid organization size";
    
    /**
     * Error message for invalid service type.
     */
    public static final String SERVICE_TYPE_INVALID_MESSAGE = "Invalid service type";
    
    /**
     * Error message for schema name size validation.
     */
    public static final String SCHEMA_NAME_SIZE_MESSAGE = "Schema name must be between " + MIN_SCHEMA_NAME_LENGTH + " and " + MAX_SCHEMA_NAME_LENGTH + " characters";
    
    /**
     * Error message for schema description size validation.
     */
    public static final String SCHEMA_DESCRIPTION_SIZE_MESSAGE = "Description must not exceed " + MAX_DESCRIPTION_LENGTH + " characters";
    
    /**
     * Error message for invalid organization entity reference.
     */
    public static final String INVALID_ORG_ENTITY_MESSAGE = "Invalid organization ID. The referenced organization does not exist or is deleted.";
    
    /**
     * Error message for invalid plan entity reference.
     */
    public static final String INVALID_PLAN_ENTITY_MESSAGE = "Invalid plan ID. The referenced plan does not exist or is deleted.";
    
    /**
     * Error message for invalid user entity reference.
     */
    public static final String INVALID_USER_ENTITY_MESSAGE = "Invalid user ID(s). One or more referenced users do not exist or are deleted.";
    
    /**
     * Private constructor to prevent instantiation.
     */
    private OrganizationSchemaConstants() {
        super();
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}