package com.codzs.constant.user;

/**
 * Constants for User-related business rules and validation.
 * Centralizes user configuration and business constraints.
 * Used for maintaining consistency across DTOs, entities, and documentation.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public final class UserConstants {

    // ========== VALIDATION CONSTANTS ==========
    
    /**
     * Maximum length for user first name.
     */
    public static final int MAX_FIRST_NAME_LENGTH = 50;
    
    /**
     * Maximum length for user last name.
     */
    public static final int MAX_LAST_NAME_LENGTH = 50;
    
    /**
     * Maximum length for user phone number.
     */
    public static final int MAX_PHONE_NUMBER_LENGTH = 20;
    
    /**
     * Maximum length for user preferred language.
     */
    public static final int MAX_PREFERRED_LANGUAGE_LENGTH = 10;
    
    /**
     * Maximum length for user timezone.
     */
    public static final int MAX_TIMEZONE_LENGTH = 50;
    
    /**
     * Maximum length for user type.
     */
    public static final int MAX_USER_TYPE_LENGTH = 20;
    
    /**
     * Maximum length for user status.
     */
    public static final int MAX_USER_STATUS_LENGTH = 20;

    // ========== VALIDATION MESSAGES ==========
    
    /**
     * Email required validation message.
     */
    public static final String EMAIL_REQUIRED_MESSAGE = "Email is required";
    
    /**
     * Invalid email format validation message.
     */
    public static final String EMAIL_FORMAT_MESSAGE = "Invalid email format";
    
    /**
     * First name required validation message.
     */
    public static final String FIRST_NAME_REQUIRED_MESSAGE = "First name is required";
    
    /**
     * First name size validation message.
     */
    public static final String FIRST_NAME_SIZE_MESSAGE = 
        "First name must not exceed " + MAX_FIRST_NAME_LENGTH + " characters";
    
    /**
     * Last name required validation message.
     */
    public static final String LAST_NAME_REQUIRED_MESSAGE = "Last name is required";
    
    /**
     * Last name size validation message.
     */
    public static final String LAST_NAME_SIZE_MESSAGE = 
        "Last name must not exceed " + MAX_LAST_NAME_LENGTH + " characters";
    
    /**
     * Phone number size validation message.
     */
    public static final String PHONE_NUMBER_SIZE_MESSAGE = 
        "Phone number must not exceed " + MAX_PHONE_NUMBER_LENGTH + " characters";
    
    /**
     * Phone number format validation message.
     */
    public static final String PHONE_NUMBER_FORMAT_MESSAGE = "Invalid phone number format";
    
    /**
     * Organization ID required validation message.
     */
    public static final String ORGANIZATION_ID_REQUIRED_MESSAGE = "Organization ID is required";
    
    /**
     * User status required validation message.
     */
    public static final String USER_STATUS_REQUIRED_MESSAGE = "User status is required";
    
    /**
     * User type required validation message.
     */
    public static final String USER_TYPE_REQUIRED_MESSAGE = "User type is required";
    
    /**
     * Active status required validation message.
     */
    public static final String ACTIVE_STATUS_REQUIRED_MESSAGE = "Active status is required";

    // ========== BUSINESS RULES ==========
    
    /**
     * Maximum number of failed login attempts before account lockout.
     */
    public static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;
    
    /**
     * Password reset token validity in hours.
     */
    public static final int PASSWORD_RESET_TOKEN_VALIDITY_HOURS = 24;
    
    /**
     * Email verification token validity in hours.
     */
    public static final int EMAIL_VERIFICATION_TOKEN_VALIDITY_HOURS = 48;
    
    /**
     * Session timeout in minutes.
     */
    public static final int SESSION_TIMEOUT_MINUTES = 60;

    // ========== DEFAULT VALUES ==========
    
    /**
     * Default user status for new users.
     */
    public static final String DEFAULT_USER_STATUS = "PENDING";
    
    /**
     * Default user type for new users.
     */
    public static final String DEFAULT_USER_TYPE = "REGULAR";
    
    /**
     * Default active status for new users.
     */
    public static final Boolean DEFAULT_IS_ACTIVE = true;
    
    /**
     * Default email verification status.
     */
    public static final Boolean DEFAULT_IS_EMAIL_VERIFIED = false;
    
    /**
     * Default phone verification status.
     */
    public static final Boolean DEFAULT_IS_PHONE_VERIFIED = false;
    
    /**
     * Default two-factor authentication status.
     */
    public static final Boolean DEFAULT_IS_TWO_FACTOR_ENABLED = false;

    // ========== EXAMPLE VALUES ==========
    
    /**
     * Example user email for documentation.
     */
    public static final String EXAMPLE_USER_EMAIL = "khaitan.nitin@codzs.com";
    
    /**
     * Example user first name for documentation.
     */
    public static final String EXAMPLE_FIRST_NAME = "Nitin";
    
    /**
     * Example user last name for documentation.
     */
    public static final String EXAMPLE_LAST_NAME = "Khaitan";
    
    /**
     * Example phone number for documentation.
     */
    public static final String EXAMPLE_PHONE_NUMBER = "+1234567890";

    // Prevent instantiation
    private UserConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}