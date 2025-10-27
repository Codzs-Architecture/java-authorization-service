package com.codzs.service.organization;

import com.codzs.entity.organization.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Organization-related business operations.
 * Provides entry point methods for organization API endpoints
 * with proper business validation and transaction management.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public interface OrganizationService {

    // ========== API FLOW METHODS ==========

    /**
     * Creates a new organization.
     * API: POST /api/v1/organizations
     *
     * @param organization the organization entity to create
     * @return the created organization entity
     */
    Organization createOrganization(Organization organization);

    /**
     * Updates an existing organization.
     * API: PUT /api/v1/organizations/{id}
     *
     * @param organization the organization entity with updates
     * @return the updated organization entity
     */
    Organization updateOrganization(Organization organization);

    /**
     * Retrieves organization by ID.
     * API: GET /api/v1/organizations/{id}
     *
     * @param organizationId the organization ID
     * @return the organization entity or null if not found
     */
    Organization getOrganizationById(String organizationId);

    /**
     * Retrieves organization by ID with optional field filtering.
     * API: GET /api/v1/organizations/{id}?include=settings,domains,plan
     *
     * @param organizationId the organization ID
     * @param include list of fields to include (settings, domains, plan)
     * @return the organization entity with filtered fields
     */
    Organization getOrganizationById(String organizationId, List<String> include);

    /**
     * Lists organizations with filters and pagination.
     * API: GET /api/v1/organizations
     *
     * @param statuses filter by statuses (optional)
     * @param organizationTypes filter by types (optional)
     * @param industries filter by industries (optional) 
     * @param sizes filter by sizes (optional)
     * @param searchText search in name/displayName (optional)
     * @param pageable pagination parameters
     * @return page of organization entities
     */
    Page<Organization> listOrganizations(List<String> statuses, 
                                       List<String> organizationTypes,
                                       List<String> industries, 
                                       List<String> sizes,
                                       String searchText, 
                                       Pageable pageable);

    /**
     * Activates an organization.
     * API: PUT /api/v1/organizations/{id}/activate
     *
     * @param organizationId the organization ID
     * @return the activated organization entity
     */
    Organization activateOrganization(String organizationId);

    /**
     * Deactivates an organization.
     * API: PUT /api/v1/organizations/{id}/deactivate
     *
     * @param organizationId the organization ID
     * @return the deactivated organization entity
     */
    Organization deactivateOrganization(String organizationId);

    /**
     * Associates a plan with an organization.
     * API: POST /api/v1/organizations/{id}/plans
     *
     * @param organizationId the organization ID
     * @param organizationPlan the organization plan entity to associate
     * @return the updated organization entity with plan
     */
    Organization associateOrganizationPlan(String organizationId, com.codzs.entity.organization.OrganizationPlan organizationPlan);


    /**
     * Gets child organizations with filters.
     * API: GET /api/v1/organizations/{id}/children
     *
     * @param parentId the parent organization ID
     * @param statuses filter by statuses (optional)
     * @param organizationTypes filter by types (optional)
     * @param pageable pagination parameters
     * @return page of child organization entities
     */
    Page<Organization> getChildOrganizations(String parentId,
                                           List<String> statuses,
                                           List<String> organizationTypes,
                                           Pageable pageable);

    /**
     * Gets organizations for autocomplete.
     * API: GET /api/v1/organizations/autocomplete
     *
     * @param statuses filter by statuses
     * @param searchQuery search query
     * @param pageable pagination parameters
     * @return list of organization entities
     */
    List<Organization> getOrganizationsForAutocomplete(List<String> statuses,
                                                     String searchQuery,
                                                     Pageable pageable);

    /**
     * Soft deletes an organization.
     * API: DELETE /api/v1/organizations/{id}
     *
     * @param organizationId the organization ID
     * @param deletedBy the user performing the deletion
     * @return the soft deleted organization entity
     */
    Organization deleteOrganization(String organizationId);

    // ========== UTILITY METHODS FOR BUSINESS VALIDATION ==========

    /**
     * Finds organization by ID (used by validators).
     *
     * @param organizationId the organization ID
     * @return organization entity or null if not found
     */
    Organization findById(String organizationId);

    /**
     * Checks if organization name already exists.
     *
     * @param name the organization name
     * @param excludeId ID to exclude from check (for updates)
     * @return true if name exists
     */
    boolean isNameAlreadyExists(String name, String excludeId);

    /**
     * Checks if organization abbreviation already exists.
     *
     * @param abbr the organization abbreviation
     * @param excludeId ID to exclude from check (for updates)
     * @return true if abbreviation exists
     */
    boolean isAbbrAlreadyExists(String abbr, String excludeId);

    /**
     * Checks if setting parent would create circular reference.
     *
     * @param parentId the proposed parent ID
     * @param childId the child organization ID
     * @return true if circular reference would be created
     */
    boolean wouldCreateCircularReference(String parentId, String childId);

    /**
     * Calculates organization hierarchy depth.
     *
     * @param organizationId the organization ID
     * @return hierarchy depth
     */
    int calculateOrganizationHierarchyDepth(String organizationId);

    /**
     * Checks if organization has active subscriptions.
     *
     * @param organizationId the organization ID
     * @return true if has active subscriptions
     */
    boolean hasActiveSubscriptions(String organizationId);

    /**
     * Checks if organization has active child organizations.
     *
     * @param organizationId the organization ID
     * @return true if has active children
     */
    boolean hasActiveChildOrganizations(String organizationId);

    /**
     * Checks if organization has active tenants.
     *
     * @param organizationId the organization ID
     * @return true if has active tenants
     */
    boolean hasActiveTenants(String organizationId);

}