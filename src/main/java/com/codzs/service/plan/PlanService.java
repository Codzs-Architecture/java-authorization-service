package com.codzs.service.plan;

import com.codzs.entity.plan.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Plan-related business operations.
 * Manages plan entities with proper business validation and transaction management.
 * Follows entity-first design pattern.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public interface PlanService {

    // ========== API FLOW METHODS ==========

    /**
     * Creates a new plan.
     * API: POST /api/v1/plans
     *
     * @param plan the plan entity to create
     * @return created plan entity
     */
    Plan createPlan(Plan plan);

    /**
     * Updates an existing plan.
     * API: PUT /api/v1/plans/{id}
     *
     * @param plan the plan entity with updates
     * @return updated plan entity
     */
    Plan updatePlan(Plan plan);

    /**
     * Gets a plan by ID.
     * API: GET /api/v1/plans/{id}
     *
     * @param planId the plan ID
     * @return plan entity or null if not found
     */
    Plan getPlanById(String planId);

    /**
     * Lists plans with filtering and pagination.
     * API: GET /api/v1/plans
     *
     * @param statuses list of plan statuses to filter by
     * @param planTypes list of plan types to filter by
     * @param searchText search text for plan name/description
     * @param pageable pagination parameters
     * @return page of plan entities
     */
    Page<Plan> listPlans(List<String> statuses, List<String> planTypes, String searchText, Pageable pageable);

    /**
     * Activates a plan.
     * API: PUT /api/v1/plans/{id}/activate
     *
     * @param planId the plan ID to activate
     * @return activated plan entity
     */
    Plan activatePlan(String planId);

    /**
     * Deactivates a plan.
     * API: PUT /api/v1/plans/{id}/deactivate
     *
     * @param planId the plan ID to deactivate
     * @return deactivated plan entity
     */
    Plan deactivatePlan(String planId);

    /**
     * Deprecates a plan.
     * API: PUT /api/v1/plans/{id}/deprecate
     *
     * @param planId the plan ID to deprecate
     * @return deprecated plan entity
     */
    Plan deprecatePlan(String planId);

    /**
     * Gets plans for autocomplete.
     * API: GET /api/v1/plans/autocomplete
     *
     * @param statuses list of plan statuses to filter by
     * @param searchQuery search query
     * @param pageable pagination parameters
     * @return list of plans for autocomplete
     */
    List<Plan> getPlansForAutocomplete(List<String> statuses, String searchQuery, Pageable pageable);

    /**
     * Soft deletes a plan.
     * API: DELETE /api/v1/plans/{id}
     *
     * @param planId the plan ID to delete
     * @param deletedBy the user performing the deletion
     * @return deleted plan entity
     */
    Plan deletePlan(String planId, String deletedBy);

    // ========== UTILITY METHODS FOR BUSINESS VALIDATION ==========

    /**
     * Finds a plan by ID.
     *
     * @param planId the plan ID
     * @return plan entity or null if not found
     */
    Plan findById(String planId);

    /**
     * Checks if plan name already exists.
     *
     * @param name the plan name to check
     * @param excludeId the plan ID to exclude from check (for updates)
     * @return true if name exists, false otherwise
     */
    boolean isNameAlreadyExists(String name, String excludeId);

    /**
     * Checks if plan code already exists.
     *
     * @param code the plan code to check
     * @param excludeId the plan ID to exclude from check (for updates)
     * @return true if code exists, false otherwise
     */
    boolean isCodeAlreadyExists(String code, String excludeId);

    /**
     * Checks if plan is compatible with organization type.
     *
     * @param planId the plan ID
     * @param organizationType the organization type
     * @return true if compatible, false otherwise
     */
    boolean isPlanCompatibleWithOrganizationType(String planId, String organizationType);

    /**
     * Checks if plan is compatible with organization size.
     *
     * @param planId the plan ID
     * @param organizationSize the organization size
     * @return true if compatible, false otherwise
     */
    boolean isPlanCompatibleWithOrganizationSize(String planId, String organizationSize);

    /**
     * Checks if plan is available in region.
     *
     * @param planId the plan ID
     * @param region the region code
     * @return true if available, false otherwise
     */
    boolean isPlanAvailableInRegion(String planId, String region);

    /**
     * Checks if transition between plan types is allowed.
     *
     * @param fromPlanType the current plan type
     * @param toPlanType the target plan type
     * @return true if transition allowed, false otherwise
     */
    boolean isTransitionAllowed(String fromPlanType, String toPlanType);

    /**
     * Checks if plan has capacity for new subscriptions.
     *
     * @param planId the plan ID
     * @return true if has capacity, false otherwise
     */
    boolean hasPlanCapacity(String planId);

    /**
     * Compares plan levels for hierarchy validation.
     *
     * @param parentPlanId the parent plan ID
     * @param childPlanId the child plan ID
     * @return comparison result (positive if parent >= child)
     */
    int comparePlanLevels(String parentPlanId, String childPlanId);

    /**
     * Checks if plan has active subscriptions.
     *
     * @param planId the plan ID
     * @return true if has active subscriptions, false otherwise
     */
    boolean hasActiveSubscriptions(String planId);

    /**
     * Gets plans by type.
     *
     * @param planType the plan type
     * @return list of plans with the specified type
     */
    List<Plan> getPlansByType(String planType);

    /**
     * Gets active plans.
     *
     * @return list of active plans
     */
    List<Plan> getActivePlans();
}