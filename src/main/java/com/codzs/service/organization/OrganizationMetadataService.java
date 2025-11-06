package com.codzs.service.organization;

import java.util.List;
import java.util.Optional;

import com.codzs.entity.organization.Organization;
import com.codzs.entity.organization.OrganizationMetadata;

/**
 * Service interface for OrganizationMetadata-related business operations.
 * Manages organization metadata including industry and size
 * with proper business validation and transaction management. Follows entity-first design pattern.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public interface OrganizationMetadataService {

    // ========== API FLOW METHODS ==========

    /**
     * Updates organization metadata for an organization.
     * API: PUT /api/v1/organizations/{id}/metadata
     *
     * @param organizationId the organization ID
     * @param metadata the organization metadata entity
     * @return updated organization with new metadata
     */
    Organization updateOrganizationMetadata(String organizationId, OrganizationMetadata metadata);

    /**
     * Gets organization metadata for an organization.
     * API: GET /api/v1/organizations/{id}/metadata
     *
     * @param organizationId the organization ID
     * @return Optional containing organization metadata, or empty if not found
     */
    Optional<OrganizationMetadata> getOrganizationMetadata(String organizationId);

    /**
     * Updates organization industry.
     * API: PUT /api/v1/organizations/{id}/metadata/industry
     *
     * @param organizationId the organization ID
     * @param industry the new industry value
     * @return updated organization with new industry
     */
    Organization updateIndustry(String organizationId, String industry);

    /**
     * Updates organization size.
     * API: PUT /api/v1/organizations/{id}/metadata/size
     *
     * @param organizationId the organization ID
     * @param size the new size value
     * @return updated organization with new size
     */
    Organization updateSize(String organizationId, String size);

    // ========== UTILITY METHODS ==========

    /**
     * Validates organization metadata.
     *
     * @param organizationId the organization ID
     * @param metadata the organization metadata to validate
     * @return true if metadata is valid, false otherwise
     */
    boolean validateOrganizationMetadata(String organizationId, OrganizationMetadata metadata);

    /**
     * Checks if an industry is valid.
     *
     * @param industry the industry to validate
     * @return true if industry is valid, false otherwise
     */
    boolean isValidIndustry(String industry);

    /**
     * Checks if an organization size is valid.
     *
     * @param size the size to validate
     * @return true if size is valid, false otherwise
     */
    boolean isValidSize(String size);

    /**
     * Gets available industries.
     *
     * @return array of available industries
     */
    List<String> getAvailableIndustries();

    /**
     * Gets available organization sizes.
     *
     * @return array of available sizes
     */
    List<String> getAvailableSizes();
}