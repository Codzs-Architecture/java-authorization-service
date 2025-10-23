package com.codzs.entity.subscription;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.codzs.framework.entity.BaseEntity;
import com.codzs.constant.subscription.SubscriptionConstants;

import java.time.Instant;

/**
 * MongoDB Document representing subscriptions within the Codzs Platform.
 * This entity stores subscription information including plan association,
 * billing details, and subscription lifecycle status.
 * 
 * Storage Database: codzs_billing_{env}
 * Collection: subscription
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Document(collection = "subscription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Subscription extends BaseEntity {

    @Id
    private String id;

    @NotBlank(message = SubscriptionConstants.ORGANIZATION_ID_REQUIRED_MESSAGE)
    @Indexed
    private String organizationId;

    @Indexed
    private String tenantId;

    @NotBlank(message = SubscriptionConstants.PLAN_ID_REQUIRED_MESSAGE)
    @Indexed
    private String planId;

    @NotBlank(message = SubscriptionConstants.SUBSCRIPTION_STATUS_REQUIRED_MESSAGE)
    @Size(max = SubscriptionConstants.MAX_SUBSCRIPTION_STATUS_LENGTH)
    @Indexed
    private String status;

    @NotNull(message = SubscriptionConstants.START_DATE_REQUIRED_MESSAGE)
    private Instant startDate;

    private Instant endDate;

    private Instant trialEndDate;

    @NotNull(message = SubscriptionConstants.BILLING_AMOUNT_REQUIRED_MESSAGE)
    @DecimalMin(value = "0.0", message = SubscriptionConstants.BILLING_AMOUNT_MIN_MESSAGE)
    @DecimalMax(value = "999999.99", message = "Billing amount exceeds maximum allowed")
    private Double billingAmount;

    @NotBlank(message = SubscriptionConstants.BILLING_CURRENCY_REQUIRED_MESSAGE)
    @Size(max = SubscriptionConstants.MAX_BILLING_CURRENCY_LENGTH)
    private String billingCurrency;

    @NotBlank(message = SubscriptionConstants.BILLING_FREQUENCY_REQUIRED_MESSAGE)
    @Size(max = SubscriptionConstants.MAX_BILLING_FREQUENCY_LENGTH)
    private String billingFrequency;

    private Instant nextBillingDate;

    private Instant lastBillingDate;

    @NotNull(message = SubscriptionConstants.AUTO_RENEWAL_REQUIRED_MESSAGE)
    private Boolean autoRenewal;

    @Size(max = SubscriptionConstants.MAX_CANCELLATION_REASON_LENGTH)
    private String cancellationReason;

    private Instant cancelledAt;

    private String cancelledBy;

    private String activatedBy;

    private Instant activatedAt;
}