package com.codzs.service.organization;

import com.codzs.constant.organization.OrganizationProjectionEnum;
import com.codzs.entity.organization.Organization;
import com.codzs.framework.aware.audit.AuditorAwareImpl;
import com.codzs.framework.exception.util.ExceptionUtils;
import com.codzs.repository.organization.OrganizationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * Base service implementation for Organization-related operations.
 * Provides common helper methods for organization retrieval and validation
 * that are shared across multiple organization service implementations.
 * 
 * This class standardizes:
 * - Organization lookup patterns using Optional<T>
 * - Exception handling with consistent error messages
 * - Common logging patterns for organization operations
 * 
 * Can be used as:
 * 1. Base class for services that don't already extend another class
 * 2. Helper component via composition for services that already extend another class
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Slf4j
public class BaseOrganizationServiceImpl {

    private final OrganizationRepository organizationRepository;
    private final ObjectMapper objectMapper;
    private final AuditorAwareImpl auditorAware;

    public BaseOrganizationServiceImpl(OrganizationRepository organizationRepository, 
        ObjectMapper objectMapper) {
        this.organizationRepository = organizationRepository;
        this.objectMapper = objectMapper;
        this.auditorAware = new AuditorAwareImpl();
    }

    // ========== COMMON HELPER METHODS ==========

    /**
     * Gets the current authenticated user ID.
     * Uses AuditorAwareImpl to extract user from security context.
     * Falls back to "SYSTEM" if no authenticated user is available.
     * 
     * @return current user ID or "SYSTEM" for system operations
     */
    protected String getCurrentUser() {
        return auditorAware.getCurrentUserId();
    }

    protected Optional<Organization> getOrgById(String organizationId) {
        log.debug("Getting organization by ID: {}", organizationId);
        
        return organizationRepository.findByIdAndDeletedDateIsNull(organizationId);
    }

    protected Organization getOrganizationAndValidate(String organizationId) {
        return this.getOrgById(organizationId)
                .orElseThrow(() -> ExceptionUtils.organizationNotFound(organizationId));
    }

    // @Override 
    protected Optional<Organization> getOrgById(String organizationId, List<OrganizationProjectionEnum> include) {
        log.debug("Getting organization by ID: {} with include filters: {}", organizationId, include);
        
        return this.getOrgById(organizationId)
                .map(organization -> {
                    // If no include filter specified, return full organization
                    if (include == null || include.isEmpty()) {
                        return organization;
                    }
                    
                    // Apply filtering based on include parameters
                    return applyIncludeFiltering(organization, include);
                });
    }
    /**
     * Applies field filtering to organization based on include parameters.
     * Starts with complete organization and removes fields not in include list.
     */
    private Organization applyIncludeFiltering(Organization organization, List<OrganizationProjectionEnum> includeFields) {
        log.debug("Applying include filtering for fields: {}", includeFields);
        
        try {
            // Create a deep copy of the organization using Jackson ObjectMapper
            Organization filteredOrg = objectMapper.readValue(
                objectMapper.writeValueAsString(organization), 
                Organization.class
            );
            
            // Remove fields that are NOT in the include list
            if (!includeFields.contains((Object) OrganizationProjectionEnum.ORGANIZATION_SETTING)) {
                filteredOrg.setSetting(null);
                log.debug("Excluding setting field");
            }
            
            if (!includeFields.contains((Object) OrganizationProjectionEnum.ORGANIZATION_DOMAIN.toLowerCase())) {
                filteredOrg.setDomains(null);
                log.debug("Excluding domains field");
            }
            
            if (!includeFields.contains((Object) OrganizationProjectionEnum.ORGANIZATION_METADATA.toLowerCase())) {
                filteredOrg.setMetadata(null);
                log.debug("Excluding metadata field");
            }
            
            if (!includeFields.contains((Object) OrganizationProjectionEnum.ORGANIZATION_DATABASE.toLowerCase())) {
                filteredOrg.setDatabase(null);
                log.debug("Excluding database field");
            }
            
            // Note: Basic organization fields (id, name, status, etc.) are always included
            
            log.debug("Applied filtering - excluded fields not in: {}", includeFields);
            return filteredOrg;
            
        } catch (Exception e) {
            log.error("Failed to create deep copy of organization during filtering", e);
            throw new RuntimeException("Failed to apply include filtering", e);
        }
    }

    // ========== COMMON LOGGING UTILITIES ==========

    // /**
    //  * Logs debug message for organization operation start.
    //  * Provides consistent logging format across all organization services.
    //  * 
    //  * @param operation the operation being performed (e.g., "Getting", "Updating")
    //  * @param entity the entity type (e.g., "metadata", "setting", "domains")
    //  * @param organizationId the organization ID
    //  */
    // protected void logOperationStart(String operation, String entity, String organizationId) {
    //     log.debug("{} {} for organization ID: {}", operation, entity, organizationId);
    // }

    // /**
    //  * Logs info message for successful organization operation completion.
    //  * Provides consistent logging format across all organization services.
    //  * 
    //  * @param operation the operation that was completed (e.g., "Updated", "Retrieved")
    //  * @param entity the entity type (e.g., "metadata", "setting", "domains")
    //  * @param organizationId the organization ID
    //  */
    // protected void logOperationSuccess(String operation, String entity, String organizationId) {
    //     log.info("{} {} for organization ID: {}", operation, entity, organizationId);
    // }
}