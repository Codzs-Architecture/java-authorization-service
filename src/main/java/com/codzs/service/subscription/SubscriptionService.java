package com.codzs.service.subscription;

import com.codzs.entity.subscription.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Subscription-related business operations.
 * Manages subscription lifecycle, billing, and plan associations.
 * Used by validation layer to fetch subscription data for business rule validation.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public interface SubscriptionService {

    // ========== VALIDATION SUPPORT METHODS ==========

    /**
     * Checks if a plan has active subscriptions.
     * Used by plan validation layer to prevent plan deletion/modification
     * when active subscriptions exist.
     *
     * @param planId the plan ID
     * @return true if plan has active subscriptions
     */
    boolean hasActiveSubscriptions(String planId);

    /**
     * Gets count of active subscriptions for a plan.
     * Used by validation layer to provide detailed error messages.
     *
     * @param planId the plan ID
     * @return number of active subscriptions
     */
    long getActiveSubscriptionCount(String planId);

    /**
     * Checks if an organization has active subscriptions.
     * Used by organization validation layer before deletion/deactivation.
     *
     * @param organizationId the organization ID
     * @return true if organization has active subscriptions
     */
    boolean hasActiveSubscriptionsForOrganization(String organizationId);

    /**
     * Checks if a tenant has active subscriptions.
     * Used by tenant validation layer before deletion/deactivation.
     *
     * @param tenantId the tenant ID
     * @return true if tenant has active subscriptions
     */
    boolean hasActiveSubscriptionsForTenant(String tenantId);

    // ========== API FLOW METHODS ==========

    /**
     * Creates a new subscription.
     * API: POST /api/v1/subscriptions
     *
     * @param subscription the subscription entity to create
     * @return the created subscription entity
     */
    Subscription createSubscription(Subscription subscription);

    /**
     * Updates an existing subscription.
     * API: PUT /api/v1/subscriptions/{id}
     *
     * @param subscription the subscription entity with updates
     * @return the updated subscription entity
     */
    Subscription updateSubscription(Subscription subscription);

    /**
     * Gets subscription by ID.
     * API: GET /api/v1/subscriptions/{id}
     *
     * @param subscriptionId the subscription ID
     * @return the subscription entity or null if not found
     */
    Subscription getSubscriptionById(String subscriptionId);

    /**
     * Gets subscriptions for an organization.
     * API: GET /api/v1/organizations/{id}/subscriptions
     *
     * @param organizationId the organization ID
     * @param statuses filter by statuses (optional)
     * @param planIds filter by plan IDs (optional)
     * @param pageable pagination parameters
     * @return page of subscription entities
     */
    Page<Subscription> getSubscriptionsForOrganization(String organizationId,
                                                      List<String> statuses,
                                                      List<String> planIds,
                                                      Pageable pageable);

    /**
     * Gets subscriptions for a tenant.
     * API: GET /api/v1/tenants/{id}/subscriptions
     *
     * @param tenantId the tenant ID
     * @param statuses filter by statuses (optional)
     * @param pageable pagination parameters
     * @return page of subscription entities
     */
    Page<Subscription> getSubscriptionsForTenant(String tenantId,
                                                List<String> statuses,
                                                Pageable pageable);

    /**
     * Activates a subscription.
     * API: PUT /api/v1/subscriptions/{id}/activate
     *
     * @param subscriptionId the subscription ID
     * @param activatedBy the user performing the activation
     * @return the activated subscription entity
     */
    Subscription activateSubscription(String subscriptionId, String activatedBy);

    /**
     * Deactivates a subscription.
     * API: PUT /api/v1/subscriptions/{id}/deactivate
     *
     * @param subscriptionId the subscription ID
     * @param deactivatedBy the user performing the deactivation
     * @param reason the deactivation reason
     * @return the deactivated subscription entity
     */
    Subscription deactivateSubscription(String subscriptionId, String deactivatedBy, String reason);

    /**
     * Cancels a subscription.
     * API: PUT /api/v1/subscriptions/{id}/cancel
     *
     * @param subscriptionId the subscription ID
     * @param cancelledBy the user performing the cancellation
     * @param reason the cancellation reason
     * @return the cancelled subscription entity
     */
    Subscription cancelSubscription(String subscriptionId, String cancelledBy, String reason);

    // ========== UTILITY METHODS ==========

    /**
     * Finds subscription by ID (used by validators).
     *
     * @param subscriptionId the subscription ID
     * @return subscription entity or null if not found
     */
    Subscription findById(String subscriptionId);

    /**
     * Checks if subscription is renewable.
     *
     * @param subscriptionId the subscription ID
     * @return true if subscription can be renewed
     */
    boolean isRenewable(String subscriptionId);

    /**
     * Checks if subscription can be upgraded.
     *
     * @param subscriptionId the subscription ID
     * @param newPlanId the new plan ID
     * @return true if upgrade is possible
     */
    boolean canUpgrade(String subscriptionId, String newPlanId);

    /**
     * Checks if subscription can be downgraded.
     *
     * @param subscriptionId the subscription ID
     * @param newPlanId the new plan ID
     * @return true if downgrade is possible
     */
    boolean canDowngrade(String subscriptionId, String newPlanId);

    /**
     * Gets active subscription for organization and plan.
     *
     * @param organizationId the organization ID
     * @param planId the plan ID
     * @return active subscription or null if not found
     */
    Subscription getActiveSubscription(String organizationId, String planId);
}