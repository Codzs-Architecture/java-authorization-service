package com.codzs.service.organization;

import com.codzs.constant.organization.OrganizationStatusEnum;
import com.codzs.entity.organization.Organization;
import com.codzs.repository.organization.OrganizationRepository;
import com.codzs.validation.organization.OrganizationBusinessValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Organization-related business operations.
 * Provides entry point methods for organization API endpoints
 * with proper business validation and transaction management.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationBusinessValidator organizationBusinessValidator;

    @Autowired
    public OrganizationServiceImpl(OrganizationRepository organizationRepository,
                                 OrganizationBusinessValidator organizationBusinessValidator) {
        this.organizationRepository = organizationRepository;
        this.organizationBusinessValidator = organizationBusinessValidator;
    }

    // ========== API FLOW METHODS ==========

    @Override
    @Transactional
    public Organization createOrganization(Organization organization) {
        log.debug("Creating organization with name: {}", organization.getName());
        
        // Business validation as first step
        organizationBusinessValidator.validateOrganizationCreationFlow(organization);
        
        // Business logic for creation
        applyCreationBusinessLogic(organization);
        
        // Save organization
        Organization savedOrganization = organizationRepository.save(organization);
        
        log.info("Created organization with ID: {} and name: {}", 
                savedOrganization.getId(), savedOrganization.getName());
        
        return savedOrganization;
    }

    @Override
    @Transactional
    public Organization updateOrganization(Organization organization) {
        log.debug("Updating organization ID: {}", organization.getId());
        
        // Business validation as first step
        organizationBusinessValidator.validateOrganizationUpdateFlow(organization);
        
        // Get existing organization to compare changes
        Organization existingOrg = getOrganizationAndValidate(organization.getId());
        
        // Update only changed fields using targeted MongoDB operations
        updateChangedFields(organization, existingOrg);
        
        log.info("Updated organization with ID: {}", organization.getId());
        
        // Return updated organization
        return getOrganizationAndValidate(organization.getId());
    }

    @Override
    public Organization getOrganizationById(String organizationId) {
        log.debug("Getting organization by ID: {}", organizationId);
        
        return organizationRepository.findByIdAndDeletedOnIsNull(organizationId)
                .orElse(null);
    }

    @Override
    public Page<Organization> listOrganizations(List<String> statuses, 
                                              List<String> organizationTypes,
                                              List<String> industries, 
                                              List<String> sizes,
                                              String searchText, 
                                              Pageable pageable) {
        log.debug("Listing organizations with filters - statuses: {}, types: {}, search: {}", 
                statuses, organizationTypes, searchText);
        
        // Convert string statuses to enum statuses
        List<OrganizationStatusEnum> statusEnums = convertToStatusEnums(statuses);
        
        // Normalize search text
        String normalizedSearchText = StringUtils.hasText(searchText) ? searchText.trim() : "";
        
        return organizationRepository.findWithFilters(
                statusEnums != null ? statusEnums : new ArrayList<>(),
                organizationTypes != null ? organizationTypes : new ArrayList<>(),
                industries != null ? industries : new ArrayList<>(),
                sizes != null ? sizes : new ArrayList<>(),
                normalizedSearchText,
                pageable
        );
    }

    @Override
    @Transactional
    public Organization activateOrganization(String organizationId) {
        log.debug("Activating organization ID: {}", organizationId);
        
        // Business validation as first step
        organizationBusinessValidator.validateOrganizationActivationFlow(organizationId);
        
        // Use MongoDB operation to update status directly
        Instant now = Instant.now();
        String user = "system"; // TODO: Get actual user from security context
        organizationRepository.updateOrganizationStatus(organizationId, OrganizationStatusEnum.ACTIVE, now, user);
        
        log.info("Activated organization with ID: {}", organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    @Override
    @Transactional
    public Organization deactivateOrganization(String organizationId) {
        log.debug("Deactivating organization ID: {}", organizationId);
        
        // Business validation as first step
        organizationBusinessValidator.validateOrganizationDeactivationFlow(organizationId);
        
        // Use MongoDB operation to update status directly
        Instant now = Instant.now();
        String user = "system"; // TODO: Get actual user from security context
        organizationRepository.updateOrganizationStatus(organizationId, OrganizationStatusEnum.SUSPENDED, now, user);
        
        log.info("Deactivated organization with ID: {}", organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    @Override
    @Transactional
    public Organization associateOrganizationPlan(String organizationId, com.codzs.entity.organization.OrganizationPlan organizationPlan) {
        log.debug("Associating plan to organization ID: {}", organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Business validation as first step
        organizationBusinessValidator.validateOrganizationPlanAssociationFlow(organization, organizationPlan);
        
        // TODO: Implement plan association using targeted field update when plan field is added to Organization entity
        // For now, set plan on organization and save (placeholder until plan field structure is defined)
        Organization updatedOrganization = organizationRepository.save(organization);
        
        log.info("Associated plan to organization with ID: {}", updatedOrganization.getId());
        
        return updatedOrganization;
    }


    @Override
    public Page<Organization> getChildOrganizations(String parentId,
                                                  List<String> statuses,
                                                  List<String> organizationTypes,
                                                  Pageable pageable) {
        log.debug("Getting child organizations for parent ID: {}", parentId);
        
        // Convert string statuses to enum statuses
        List<OrganizationStatusEnum> statusEnums = convertToStatusEnums(statuses);
        
        return organizationRepository.findChildrenWithFilters(
                parentId,
                statusEnums != null ? statusEnums : new ArrayList<>(),
                organizationTypes != null ? organizationTypes : new ArrayList<>(),
                pageable
        );
    }

    @Override
    public List<Organization> getOrganizationsForAutocomplete(List<String> statuses,
                                                            String searchQuery,
                                                            Pageable pageable) {
        log.debug("Getting organizations for autocomplete with query: {}", searchQuery);
        
        // Convert string statuses to enum statuses
        List<OrganizationStatusEnum> statusEnums = convertToStatusEnums(statuses);
        
        // Default to active organizations if no statuses provided
        if (statusEnums == null || statusEnums.isEmpty()) {
            statusEnums = Arrays.asList(OrganizationStatusEnum.ACTIVE);
        }
        
        String normalizedQuery = StringUtils.hasText(searchQuery) ? searchQuery.trim() : "";
        
        return organizationRepository.findForAutocomplete(statusEnums, normalizedQuery, pageable);
    }

    @Override
    @Transactional
    public Organization deleteOrganization(String organizationId, String deletedBy) {
        log.debug("Soft deleting organization ID: {} by user: {}", organizationId, deletedBy);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Business validation for deletion
        validateOrganizationDeletion(organization);
        
        // Use MongoDB operation to update status and deletion fields directly
        Instant now = Instant.now();
        organizationRepository.updateOrganizationStatus(organizationId, OrganizationStatusEnum.DELETED, now, deletedBy);
        
        log.info("Soft deleted organization with ID: {}", organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    // ========== UTILITY METHODS FOR BUSINESS VALIDATION ==========

    @Override
    public Organization findById(String organizationId) {
        return organizationRepository.findByIdAndDeletedOnIsNull(organizationId)
                .orElse(null);
    }

    @Override
    public boolean isNameAlreadyExists(String name, String excludeId) {
        if (!StringUtils.hasText(name)) {
            return false;
        }
        
        if (StringUtils.hasText(excludeId)) {
            // For updates - check if name exists excluding current organization
            return organizationRepository.findByNameAndDeletedOnIsNull(name)
                    .map(org -> !org.getId().equals(excludeId))
                    .orElse(false);
        } else {
            // For creation - check if name exists at all
            return organizationRepository.existsByNameAndDeletedOnIsNull(name);
        }
    }

    @Override
    public boolean isAbbrAlreadyExists(String abbr, String excludeId) {
        if (!StringUtils.hasText(abbr)) {
            return false;
        }
        
        if (StringUtils.hasText(excludeId)) {
            // For updates - check if abbreviation exists excluding current organization
            return organizationRepository.findByAbbrAndDeletedOnIsNull(abbr)
                    .map(org -> !org.getId().equals(excludeId))
                    .orElse(false);
        } else {
            // For creation - check if abbreviation exists at all
            return organizationRepository.existsByAbbrAndDeletedOnIsNull(abbr);
        }
    }

    @Override
    public boolean wouldCreateCircularReference(String parentId, String childId) {
        if (!StringUtils.hasText(parentId) || !StringUtils.hasText(childId)) {
            return false;
        }
        
        // Traverse up the hierarchy from parent to check if child is an ancestor
        String currentId = parentId;
        while (StringUtils.hasText(currentId)) {
            if (currentId.equals(childId)) {
                return true; // Circular reference detected
            }
            
            Organization current = findById(currentId);
            if (current == null) {
                break;
            }
            
            currentId = current.getParentOrganizationId();
        }
        
        return false;
    }

    @Override
    public int calculateOrganizationHierarchyDepth(String organizationId) {
        if (!StringUtils.hasText(organizationId)) {
            return 0;
        }
        
        int depth = 0;
        String currentId = organizationId;
        
        while (StringUtils.hasText(currentId)) {
            Organization current = findById(currentId);
            if (current == null) {
                break;
            }
            
            depth++;
            currentId = current.getParentOrganizationId();
        }
        
        return depth;
    }

    @Override
    public boolean hasActiveSubscriptions(String organizationId) {
        // TODO: Implement subscription check when subscription service is available
        log.debug("Checking active subscriptions for organization ID: {} - not implemented yet", organizationId);
        return false;
    }

    @Override
    public boolean hasActiveChildOrganizations(String organizationId) {
        if (!StringUtils.hasText(organizationId)) {
            return false;
        }
        
        List<Organization> children = organizationRepository
                .findByParentOrganizationIdAndDeletedOnIsNull(organizationId);
        
        return children.stream()
                .anyMatch(child -> child.getStatus() == OrganizationStatusEnum.ACTIVE);
    }

    @Override
    public boolean hasActiveTenants(String organizationId) {
        // TODO: Implement tenant check when tenant service is available
        
        log.debug("Checking active tenants for organization ID: {} - not implemented yet", organizationId);
        return false;
    }


    // ========== PRIVATE HELPER METHODS ==========

    private Organization getOrganizationAndValidate(String organizationId) {
        Organization organization = findById(organizationId);
        if (organization == null) {
            throw new com.codzs.exception.validation.ValidationException("Organization not found with ID: " + organizationId);
        }
        return organization;
    }

    private void applyCreationBusinessLogic(Organization organization) {
        // Set creation defaults
        organization.setStatus(OrganizationStatusEnum.PENDING);
        
        log.debug("Applied creation business logic for organization: {}", organization.getName());
    }




    private void validateOrganizationDeletion(Organization organization) {
        // Business validation for deletion
        organizationBusinessValidator.validateOrganizationDeletionFlow(organization);
        
        log.debug("Validated organization deletion for organization: {}", organization.getName());
    }

    private void updateChangedFields(Organization newOrg, Organization existingOrg) {
        Instant now = Instant.now();
        String user = "system"; // TODO: Get actual user from security context
        
        // Update each field only if it has changed
        if (hasFieldChanged(newOrg.getName(), existingOrg.getName())) {
            organizationRepository.updateOrganizationName(newOrg.getId(), newOrg.getName(), now, user);
        }
        
        if (hasFieldChanged(newOrg.getAbbr(), existingOrg.getAbbr())) {
            organizationRepository.updateOrganizationAbbr(newOrg.getId(), newOrg.getAbbr(), now, user);
        }
        
        if (hasFieldChanged(newOrg.getDisplayName(), existingOrg.getDisplayName())) {
            organizationRepository.updateOrganizationDisplayName(newOrg.getId(), newOrg.getDisplayName(), now, user);
        }
        
        if (hasFieldChanged(newOrg.getDescription(), existingOrg.getDescription())) {
            organizationRepository.updateOrganizationDescription(newOrg.getId(), newOrg.getDescription(), now, user);
        }
        
        if (newOrg.getStatus() != null && newOrg.getStatus() != existingOrg.getStatus()) {
            organizationRepository.updateOrganizationStatus(newOrg.getId(), newOrg.getStatus(), now, user);
        }
        
        if (hasFieldChanged(newOrg.getOrganizationType(), existingOrg.getOrganizationType())) {
            organizationRepository.updateOrganizationType(newOrg.getId(), newOrg.getOrganizationType(), now, user);
        }
        
        if (hasFieldChanged(newOrg.getBillingEmail(), existingOrg.getBillingEmail())) {
            organizationRepository.updateOrganizationBillingEmail(newOrg.getId(), newOrg.getBillingEmail(), now, user);
        }
        
        if (newOrg.getExpiresDate() != null && !newOrg.getExpiresDate().equals(existingOrg.getExpiresDate())) {
            organizationRepository.updateOrganizationExpiresDate(newOrg.getId(), newOrg.getExpiresDate(), now, user);
        }
        
        if (hasFieldChanged(newOrg.getParentOrganizationId(), existingOrg.getParentOrganizationId())) {
            organizationRepository.updateOrganizationParent(newOrg.getId(), newOrg.getParentOrganizationId(), now, user);
        }
    }
    
    private boolean hasFieldChanged(String newValue, String existingValue) {
        if (newValue == null && existingValue == null) {
            return false;
        }
        if (newValue == null || existingValue == null) {
            return true;
        }
        return !newValue.equals(existingValue);
    }


    private List<OrganizationStatusEnum> convertToStatusEnums(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        
        return statuses.stream()
                .filter(StringUtils::hasText)
                .map(status -> {
                    try {
                        return OrganizationStatusEnum.valueOf(status.toUpperCase().trim());
                    } catch (IllegalArgumentException e) {
                        log.warn("Invalid organization status: {}", status);
                        return null;
                    }
                })
                .filter(status -> status != null)
                .collect(Collectors.toList());
    }

}