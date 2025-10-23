package com.codzs.framework.constant;

/**
 * Common constants used across the entire application.
 * Contains shared patterns, formats, default values, and common constraints
 * that are used by multiple modules and components.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public final class CommonConstants {

    // ========================= DATE & TIME PATTERNS =========================
    
    /**
     * Standard UTC timestamp pattern for JSON serialization/deserialization.
     * Used in @JsonFormat and @Mapping annotations throughout the application.
     */
    public static final String UTC_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    
    /**
     * Alternative timestamp pattern with timezone offset.
     */
    public static final String TIMESTAMP_WITH_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";
    
    /**
     * Date-only pattern for date fields.
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    
    /**
     * Time-only pattern for time fields.
     */
    public static final String TIME_PATTERN = "HH:mm:ss";

    public static final String HTTP_PROTOCOL = "http://";
    public static final String HTTPS_PROTOCOL = "https://";
    
    /**
     * Default language setting.
     */
    public static final String DEFAULT_LANGUAGE = "en-US";
    
    /**
     * Default timezone setting.
     */
    public static final String DEFAULT_TIMEZONE = "UTC";
    
    /**
     * Default currency setting.
     */
    public static final String DEFAULT_CURRENCY = "USD";
    
    /**
     * Default country setting.
     */
    public static final String DEFAULT_COUNTRY = "US";
    
    /**
     * Default locale for new tenants.
     */
    public static final String DEFAULT_LOCALE = "en_US";
    
    public static final String ACTIVE = "ACTIVE";


    // ========================= VALIDATION PATTERNS =========================
    
    /**
     * UUID pattern for validating UUID strings.
     */
    public static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    
    /**
     * Email pattern for basic email validation. 
     */
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        
    /**
     * Phone number validation pattern (international format).
     */
    public static final String PHONE_NUMBER_PATTERN = "^\\+?[1-9]\\d{1,14}$";

    /**
     * Alphanumeric pattern for IDs and codes.
     */
    public static final String ALPHANUMERIC_PATTERN = "^[A-Za-z0-9]+$";
    
    /**
     * Alphanumeric with underscore pattern.
     */
    public static final String ALPHANUMERIC_UNDERSCORE_PATTERN = "^[A-Za-z0-9_]+$";

    // ========================= COMMON SIZE CONSTRAINTS =========================
    
    /**
     * Maximum length for standard text fields.
     */
    public static final int STANDARD_TEXT_MAX_LENGTH = 255;
    
    /**
     * Maximum length for description fields.
     */
    public static final int DESCRIPTION_MAX_LENGTH = 1000;
    
    /**
     * Maximum length for comment fields.
     */
    public static final int COMMENT_MAX_LENGTH = 2000;
    
    /**
     * Maximum length for short text fields.
     */
    public static final int SHORT_TEXT_MAX_LENGTH = 100;
    
    /**
     * Maximum length for very short text fields (codes, abbreviations).
     */
    public static final int VERY_SHORT_TEXT_MAX_LENGTH = 50;
    
    /**
     * Minimum length for most text fields.
     */
    public static final int TEXT_MIN_LENGTH = 2;

    // ========================= HTTP STATUS CODES =========================
    
    /**
     * HTTP 200 OK status code.
     */
    public static final int HTTP_OK = 200;
    
    /**
     * HTTP 201 Created status code.
     */
    public static final int HTTP_CREATED = 201;
    
    /**
     * HTTP 400 Bad Request status code.
     */
    public static final int HTTP_BAD_REQUEST = 400;
    
    /**
     * HTTP 401 Unauthorized status code.
     */
    public static final int HTTP_UNAUTHORIZED = 401;
    
    /**
     * HTTP 403 Forbidden status code.
     */
    public static final int HTTP_FORBIDDEN = 403;
    
    /**
     * HTTP 404 Not Found status code.
     */
    public static final int HTTP_NOT_FOUND = 404;
    
    /**
     * HTTP 409 Conflict status code.
     */
    public static final int HTTP_CONFLICT = 409;
    
    /**
     * HTTP 500 Internal Server Error status code.
     */
    public static final int HTTP_INTERNAL_ERROR = 500;

    // ========================= COMMON DEFAULT VALUES =========================
    
    /**
     * Default page size for pagination.
     */
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * Maximum page size for pagination.
     */
    public static final int MAX_PAGE_SIZE = 100;
    
    /**
     * Default page number for pagination (0-based).
     */
    public static final int DEFAULT_PAGE_NUMBER = 1;
    
    /**
     * Default sort direction.
     */
    public static final String DEFAULT_SORT_DIRECTION = "ASC";
    
    /**
     * Default encoding.
     */
    public static final String DEFAULT_ENCODING = "UTF-8";

    // ========================= CACHE TTL VALUES (seconds) =========================
    
    /**
     * Short cache TTL (5 minutes).
     */
    public static final long CACHE_TTL_SHORT = 300;
    
    /**
     * Medium cache TTL (30 minutes).
     */
    public static final long CACHE_TTL_MEDIUM = 1800;
    
    /**
     * Long cache TTL (1 hour).
     */
    public static final long CACHE_TTL_LONG = 3600;
    
    /**
     * Very long cache TTL (24 hours).
     */
    public static final long CACHE_TTL_VERY_LONG = 86400;

    // ========================= COMMON ERROR MESSAGES =========================
    
    /**
     * Generic required field error message.
     */
    public static final String FIELD_REQUIRED_MESSAGE = "This field is required";
    
    /**
     * Generic invalid format error message.
     */
    public static final String INVALID_FORMAT_MESSAGE = "Invalid format";
    
    /**
     * Generic field too long error message.
     */
    public static final String FIELD_TOO_LONG_MESSAGE = "Field value is too long";
    
    /**
     * Generic field too short error message.
     */
    public static final String FIELD_TOO_SHORT_MESSAGE = "Field value is too short";
    
    /**
     * Generic invalid email error message.
     */
    public static final String INVALID_EMAIL_MESSAGE = "Invalid email format";
    
    /**
     * Generic invalid UUID error message.
     */
    public static final String INVALID_UUID_MESSAGE = "Invalid UUID format";

    // ========================= CORRELATION ID =========================
    
    /**
     * Header name for correlation ID.
     */
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    
    /**
     * Default correlation ID prefix.
     */
    public static final String CORRELATION_ID_PREFIX = "req_";

    // ========================= CONSTRUCTOR =========================
    
    /**
     * Private constructor to prevent instantiation.
     */
    private CommonConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}