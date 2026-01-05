package com.codzs.service.organization;

import com.codzs.entity.organization.Organization;
import com.codzs.entity.organization.OrganizationPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

/**
 * Service interface for OrganizationPlan-related business operations.
 * Manages organization-plan associations with proper business validation
 * and transaction management. Follows entity-first design pattern.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public interface OrganizationPlanService {

    // ========== API FLOW METHODS ==========

    /**
     * Associates a plan with an organization.
     * API: POST /api/v1/organizations/{id}/plans
     *
     * @param organization the organization entity
     * @param organizationPlan the organization plan entity
     * @return created organization plan association
     */
    OrganizationPlan associatePlanWithOrganization(Organization organization, OrganizationPlan organizationPlan);

    /**
     * Updates an existing organization plan association.
     * API: PUT /api/v1/organizations/{id}/plans/{planAssociationId}
     *
     * @param organization the organization entity
     * @param organizationPlan the organization plan entity with updates
     * @return updated organization plan association
     */
    OrganizationPlan updateOrganizationPlan(Organization organization, OrganizationPlan organizationPlan);

    /**
     * Gets the current active plan for an organization.
     * API: GET /api/v1/organizations/{id}/plans/current
     *
     * @param organizationId the organization ID
     * @return current active organization plan or null if not found
     */
    OrganizationPlan getCurrentActivePlan(String organizationId);

    /**
     * Gets plan history for an organization with pagination.
     * API: GET /api/v1/organizations/{id}/plans
     *
     * @param organizationId the organization ID
     * @param pageable pagination parameters
     * @return page of organization plan history
     */
    Page<OrganizationPlan> getOrganizationPlanHistory(String organizationId, Pageable pageable);

    /**
     * Gets plan history for an organization within a date range.
     * API: GET /api/v1/organizations/{id}/plans?startDate={startDate}&endDate={endDate}
     *
     * @param organizationId the organization ID
     * @param startDate start date for filtering
     * @param endDate end date for filtering
     * @param pageable pagination parameters
     * @return page of organization plan history within date range
     */
    Page<OrganizationPlan> getOrganizationPlanHistory(String organizationId, Instant startDate, 
                                                     Instant endDate, Pageable pageable);

    /**
     * Activates an organization plan association.
     * API: PUT /api/v1/organizations/{id}/plans/{planAssociationId}/activate
     *
     * @param organizationId the organization ID
     * @param planAssociationId the plan association ID
     * @return activated organization plan
     */
    OrganizationPlan activateOrganizationPlan(String organizationId, String planAssociationId);

    /**
     * Deactivates an organization plan association.
     * API: PUT /api/v1/organizations/{id}/plans/{planAssociationId}/deactivate
     *
     * @param organizationId the organization ID
     * @param planAssociationId the plan association ID
     * @return deactivated organization plan
     */
    OrganizationPlan deactivateOrganizationPlan(String organizationId, String planAssociationId);

    /**
     * Changes organization plan (deactivates current and activates new).
     * API: POST /api/v1/organizations/{id}/plans/change
     *
     * @param organizationId the organization ID
     * @param newOrganizationPlan the new organization plan entity
     * @return new active organization plan
     */
    OrganizationPlan changeOrganizationPlan(String organizationId, OrganizationPlan newOrganizationPlan);

    /**
     * Removes an organization plan association (soft delete).
     * API: DELETE /api/v1/organizations/{id}/plans/{planAssociationId}
     *
     * @param organizationId the organization ID
     * @param planAssociationId the plan association ID
     * @param deletedBy the user performing the deletion
     * @return deleted organization plan
     */
    OrganizationPlan removeOrganizationPlan(String organizationId, String planAssociationId, String deletedBy);

    /**
     * Processes expired organization plans.
     * API: POST /api/v1/admin/organization-plans/process-expired
     *
     * @return list of processed expired organization plans
     */
    List<OrganizationPlan> processExpiredOrganizationPlans();

    // ========== UTILITY METHODS FOR BUSINESS VALIDATION ==========

    /**
     * Finds an organization plan by ID.
     *
     * @param planAssociationId the plan association ID
     * @return organization plan or null if not found
     */
    OrganizationPlan findById(String planAssociationId);

    /**
     * Finds an organization plan by organization and plan association ID.
     *
     * @param organizationId the organization ID
     * @param planAssociationId the plan association ID
     * @return organization plan or null if not found
     */
    OrganizationPlan findByOrganizationAndId(String organizationId, String planAssociationId);

    /**
     * Checks if organization already has the specified plan active.
     *
     * @param organizationId the organization ID
     * @param planId the plan ID
     * @return true if organization has active plan, false otherwise
     */
    boolean hasActivePlan(String organizationId, String planId);

    /**
     * Checks if there are conflicting plans for the given validity period.
     *
     * @param organizationId the organization ID
     * @param validFrom the validity start date
     * @param validTo the validity end date
     * @param excludePlanAssociationId the plan association ID to exclude from check
     * @return true if conflicts exist, false otherwise
     */
    boolean hasConflictingPlans(String organizationId, Instant validFrom, Instant validTo, String excludePlanAssociationId);

    /**
     * Gets current user count for an organization.
     * Used for plan limit validation.
     *
     * @param organizationId the organization ID
     * @return current user count
     */
    int getCurrentUserCount(String organizationId);

    /**
     * Gets current tenant count for an organization.
     * Used for plan limit validation.
     *
     * @param organizationId the organization ID
     * @return current tenant count
     */
    int getCurrentTenantCount(String organizationId);

    /**
     * Gets current storage usage for an organization.
     * Used for plan limit validation.
     *
     * @param organizationId the organization ID
     * @return current storage usage in bytes
     */
    long getCurrentStorageUsage(String organizationId);

    /**
     * Checks if organization has valid payment method.
     * Used for paid plan validation.
     *
     * @param organizationId the organization ID
     * @return true if has valid payment method, false otherwise
     */
    boolean hasValidPaymentMethod(String organizationId);

    /**
     * Gets active plan usage count for a specific plan.
     * Used for plan capacity validation.
     *
     * @param planId the plan ID
     * @return active usage count
     */
    long getActivePlanUsageCount(String planId);

    /**
     * Gets all organization plans for an organization.
     * Used for bulk operations.
     *
     * @param organizationId the organization ID
     * @return list of organization plans
     */
    List<OrganizationPlan> getAllOrganizationPlans(String organizationId);

    /**
     * Checks if organization has any plan associations.
     *
     * @param organizationId the organization ID
     * @return true if has plans, false otherwise
     */
    boolean hasAnyPlans(String organizationId);

    /**
     * Gets the latest plan association for an organization.
     *
     * @param organizationId the organization ID
     * @return latest organization plan or null if none found
     */
    OrganizationPlan getLatestPlan(String organizationId);
}