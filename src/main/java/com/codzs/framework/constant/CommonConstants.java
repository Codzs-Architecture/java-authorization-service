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
     * ISO-8601 timestamp pattern for Instant serialization/deserialization.
     * Compatible with java.time.Instant fields.
     */
    public static final String ISO_INSTANT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    
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

    // ========================= CORRELATION ID =========================
    
    /**
     * Default correlation ID prefix.
     */
    public static final String CORRELATION_ID_PREFIX = "req_";

    public static final Boolean SKIP_FURTHER_STEP = true;

    // ========================= CONSTRUCTOR =========================
    
    /**
     * Private constructor to prevent instantiation.
     */
    private CommonConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}