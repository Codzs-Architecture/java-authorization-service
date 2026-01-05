package com.codzs.constant.subscription;

/**
 * Constants for Subscription-related business rules and validation.
 * Centralizes subscription configuration and business constraints.
 * Used for maintaining consistency across DTOs, entities, and documentation.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public final class SubscriptionConstants {

    // ========== VALIDATION CONSTANTS ==========
    
    /**
     * Maximum length for subscription status.
     */
    public static final int MAX_SUBSCRIPTION_STATUS_LENGTH = 20;
    
    /**
     * Maximum length for billing currency.
     */
    public static final int MAX_BILLING_CURRENCY_LENGTH = 10;
    
    /**
     * Maximum length for billing frequency.
     */
    public static final int MAX_BILLING_FREQUENCY_LENGTH = 20;
    
    /**
     * Maximum length for cancellation reason.
     */
    public static final int MAX_CANCELLATION_REASON_LENGTH = 500;

    // ========== VALIDATION MESSAGES ==========
    
    /**
     * Organization ID required validation message.
     */
    public static final String ORGANIZATION_ID_REQUIRED_MESSAGE = "Organization ID is required";
    
    /**
     * Plan ID required validation message.
     */
    public static final String PLAN_ID_REQUIRED_MESSAGE = "Plan ID is required";
    
    /**
     * Subscription status required validation message.
     */
    public static final String SUBSCRIPTION_STATUS_REQUIRED_MESSAGE = "Subscription status is required";
    
    /**
     * Start date required validation message.
     */
    public static final String START_DATE_REQUIRED_MESSAGE = "Start date is required";
    
    /**
     * Billing amount required validation message.
     */
    public static final String BILLING_AMOUNT_REQUIRED_MESSAGE = "Billing amount is required";
    
    /**
     * Billing amount validation message.
     */
    public static final String BILLING_AMOUNT_MIN_MESSAGE = "Billing amount must be non-negative";
    
    /**
     * Billing currency required validation message.
     */
    public static final String BILLING_CURRENCY_REQUIRED_MESSAGE = "Billing currency is required";
    
    /**
     * Billing frequency required validation message.
     */
    public static final String BILLING_FREQUENCY_REQUIRED_MESSAGE = "Billing frequency is required";
    
    /**
     * Auto renewal required validation message.
     */
    public static final String AUTO_RENEWAL_REQUIRED_MESSAGE = "Auto renewal setting is required";

    // ========== BUSINESS RULES ==========
    
    /**
     * Minimum billing amount in any currency.
     */
    public static final double MIN_BILLING_AMOUNT = 0.0;
    
    /**
     * Maximum billing amount in any currency.
     */
    public static final double MAX_BILLING_AMOUNT = 999999.99;
    
    /**
     * Trial period duration in days.
     */
    public static final int TRIAL_PERIOD_DAYS = 30;
    
    /**
     * Grace period after subscription expiry in days.
     */
    public static final int GRACE_PERIOD_DAYS = 7;
    
    /**
     * Maximum number of subscription renewals allowed.
     */
    public static final int MAX_RENEWAL_COUNT = 999;
    
    /**
     * Cancellation notice period in days.
     */
    public static final int CANCELLATION_NOTICE_DAYS = 30;

    // ========== DEFAULT VALUES ==========
    
    /**
     * Default subscription status for new subscriptions.
     */
    public static final String DEFAULT_SUBSCRIPTION_STATUS = "ACTIVE";
    
    /**
     * Default billing currency for new subscriptions.
     */
    public static final String DEFAULT_BILLING_CURRENCY = "USD";
    
    /**
     * Default billing frequency for new subscriptions.
     */
    public static final String DEFAULT_BILLING_FREQUENCY = "MONTHLY";
    
    /**
     * Default auto renewal setting for new subscriptions.
     */
    public static final Boolean DEFAULT_AUTO_RENEWAL = true;

    // ========== VALID VALUES ==========
    
    /**
     * Valid subscription statuses.
     */
    public static final String[] VALID_SUBSCRIPTION_STATUSES = {
        "PENDING", "ACTIVE", "SUSPENDED", "CANCELLED", "EXPIRED", "TRIAL"
    };
    
    /**
     * Valid billing frequencies.
     */
    public static final String[] VALID_BILLING_FREQUENCIES = {
        "DAILY", "WEEKLY", "MONTHLY", "QUARTERLY", "YEARLY"
    };
    
    /**
     * Valid billing currencies.
     */
    public static final String[] VALID_BILLING_CURRENCIES = {
        "USD", "EUR", "GBP", "INR", "CAD", "AUD", "JPY"
    };

    // ========== EXAMPLE VALUES ==========
    
    /**
     * Example subscription ID for documentation.
     */
    public static final String EXAMPLE_SUBSCRIPTION_ID = "sub_1234567890abcdef";
    
    /**
     * Example billing amount for documentation.
     */
    public static final Double EXAMPLE_BILLING_AMOUNT = 99.99;
    
    /**
     * Example cancellation reason for documentation.
     */
    public static final String EXAMPLE_CANCELLATION_REASON = "Customer requested cancellation";

    // Prevent instantiation
    private SubscriptionConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}