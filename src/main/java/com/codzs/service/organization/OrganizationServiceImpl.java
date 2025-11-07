package com.codzs.service.organization;

import com.codzs.constant.organization.OrganizationConstants;
import com.codzs.constant.organization.OrganizationProjectionEnum;
import com.codzs.constant.organization.OrganizationStatusEnum;
import com.codzs.entity.domain.Domain;
import com.codzs.entity.organization.DatabaseSchema;
import com.codzs.entity.organization.Organization;
import com.codzs.entity.organization.OrganizationPlan;
import com.codzs.repository.organization.OrganizationRepository;
import com.codzs.validation.organization.OrganizationBusinessValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
public class OrganizationServiceImpl extends BaseOrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationBusinessValidator organizationBusinessValidator;
    private final ObjectMapper objectMapper;
    private final DatabaseSchemaService databaseSchemaService;
    private final OrganizationDomainService organizationDomainService;

    @Autowired
    public OrganizationServiceImpl(OrganizationRepository organizationRepository,
                                 OrganizationBusinessValidator organizationBusinessValidator,
                                 ObjectMapper objectMapper,
                                 DatabaseSchemaService databaseSchemaService,
                                 OrganizationDomainService organizationDomainService) {
        super(organizationRepository, objectMapper);
        this.organizationRepository = organizationRepository;
        this.organizationBusinessValidator = organizationBusinessValidator;
        this.objectMapper = objectMapper;
        this.databaseSchemaService = databaseSchemaService;
        this.organizationDomainService = organizationDomainService;
    }

    // ========== API FLOW METHODS ==========

    @Override
    @Transactional
    public Organization createOrganization(Organization organization) {
        log.debug("Creating organization with name: {}", organization.getName());
        
        boolean wouldCreateCircularReference = this.wouldCreateCircularReference(organization.getParentOrganizationId(), organization.getId());
        int depth = this.calculateOrganizationHierarchyDepth(organization.getParentOrganizationId());
        boolean isNameAlreadyExists = this.isNameAlreadyExists(organization.getName(), null);
        boolean isAbbrAlreadyExists = this.isAbbrAlreadyExists(organization.getAbbr(), null);
        Optional<Organization> parentOrganization = getOrgById(organization.getParentOrganizationId());

        // Business validation as first step
        organizationBusinessValidator.validateOrganizationCreationFlow(organization, wouldCreateCircularReference, depth, isNameAlreadyExists, isAbbrAlreadyExists, parentOrganization);
        
        // Business logic for creation
        applyCreationBusinessLogic(organization);
        
        // Save organization
        Organization savedOrganization = organizationRepository.save(organization);
        
        // Add default domain after organization is created
        try {
            createDefaultDomain(savedOrganization);
        } catch (Exception e) {
            log.error("Failed to create default domain for organization ID: {}", 
                     savedOrganization.getId(), e);
            // Continue as organization is already created, domain can be added manually later
        }
        
        // Add default AUTH schema after organization is created
        try {
            createDefaultAuthSchema(savedOrganization);
        } catch (Exception e) {
            log.error("Failed to create default AUTH schema for organization ID: {}", 
                     savedOrganization.getId(), e);
            // Continue as organization is already created, schema can be added manually later
        }
        
        log.info("Created organization with ID: {} and name: {}", 
                savedOrganization.getId(), savedOrganization.getName());
        
        // Return updated organization
        return getOrganizationAndValidate(organization.getId());
    }

    @Override
    @Transactional
    public Organization updateOrganization(Organization organization) {
        log.debug("Updating organization ID: {}", organization.getId());
        
        boolean wouldCreateCircularReference = this.wouldCreateCircularReference(organization.getParentOrganizationId(), organization.getId());
        int depth = this.calculateOrganizationHierarchyDepth(organization.getParentOrganizationId());
        boolean hasActiveSubscriptions = this.hasActiveSubscriptions(organization.getId());
        boolean isNameAlreadyExists = this.isNameAlreadyExists(organization.getName(), organization.getId());
        boolean isAbbrAlreadyExists = this.isAbbrAlreadyExists(organization.getAbbr(), organization.getId());
        Optional<Organization> parentOrganization = getOrgById(organization.getParentOrganizationId());

        // Business validation as first step
        organizationBusinessValidator.validateOrganizationUpdateFlow(organization, wouldCreateCircularReference, depth, hasActiveSubscriptions, isNameAlreadyExists, isAbbrAlreadyExists, parentOrganization);
        
        // Get existing organization to compare changes
        Organization existingOrg = getOrganizationAndValidate(organization.getId());
        
        // Update only changed fields using targeted MongoDB operations
        updateChangedFields(organization, existingOrg);
        
        log.info("Updated organization with ID: {}", organization.getId());
        
        // Return updated organization
        return getOrganizationAndValidate(organization.getId());
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
        
        Organization organization = getOrganizationAndValidate(organizationId);

        Optional<Organization> parentOrganization = this.getOrgById(organization.getParentOrganizationId());
        // Business validation as first step
        Boolean skipFutherStep = organizationBusinessValidator.validateOrganizationActivationFlow(organization, parentOrganization);
        
        if (!skipFutherStep) {
            // Use MongoDB operation to update status directly
            Instant now = Instant.now();
            String user = getCurrentUser();
            organizationRepository.updateOrganizationStatus(organizationId, OrganizationStatusEnum.ACTIVE, now, user);
            
            log.info("Activated organization with ID: {}", organizationId);
        }

        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    @Override
    @Transactional
    public Organization deactivateOrganization(String organizationId) {
        log.debug("Deactivating organization ID: {}", organizationId);
                
        Organization organization = getOrganizationAndValidate(organizationId);

        boolean hasActiveChildOrganizations = this.hasActiveChildOrganizations(organization.getId());

        boolean hasActiveTenants = this.hasActiveTenants(organization.getId());


        // Business validation as first step
        Boolean skipFutherStep = organizationBusinessValidator.validateOrganizationDeactivationFlow(organization, hasActiveChildOrganizations, hasActiveTenants);
        
        if (!skipFutherStep) {
            // Use MongoDB operation to update status directly
            Instant now = Instant.now();
            String user = getCurrentUser();
            organizationRepository.updateOrganizationStatus(organizationId, OrganizationStatusEnum.SUSPENDED, now, user);
            
            log.info("Deactivated organization with ID: {}", organizationId);
        }

        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    @Override
    @Transactional
    public Organization associateOrganizationPlan(String organizationId, OrganizationPlan organizationPlan) {
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
    public Organization deleteOrganization(String organizationId) {
        log.debug("Soft deleting organization ID: {}", organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        boolean hasActiveChildOrganizations = this.hasActiveChildOrganizations(organization.getId());
        boolean hasActiveTenants = this.hasActiveTenants(organization.getId());

            // Business validation for deletion
        Boolean skipFurtherStep = organizationBusinessValidator.validateOrganizationDeletionFlow(organization, hasActiveChildOrganizations, hasActiveTenants);
        
        if (skipFurtherStep) {
            // Use MongoDB operation to update status and deletion fields directly
            Instant now = Instant.now();
            String user = getCurrentUser();
            organizationRepository.updateOrganizationStatus(organizationId, OrganizationStatusEnum.DELETED, now, user);
            
            log.info("Soft deleted organization with ID: {}", organizationId);
        }
        
        // Return updated organization
        return getOrgById(organizationId).get();
    }

    // ========== UTILITY METHODS FOR BUSINESS VALIDATION ==========

    // @Override
    // public Optional<Organization> findById(String organizationId) {
    //     return organizationRepository.findByIdAndDeletedDateIsNull(organizationId);
    // }

    @Override
    public boolean isNameAlreadyExists(String name, String excludeId) {
        if (!StringUtils.hasText(name)) {
            return false;
        }
        
        if (StringUtils.hasText(excludeId)) {
            // For updates - check if name exists excluding current organization
            return organizationRepository.findByNameAndDeletedDateIsNull(name)
                    .map(org -> !org.getId().equals(excludeId))
                    .orElse(false);
        } else {
            // For creation - check if name exists at all
            return organizationRepository.existsByNameAndDeletedDateIsNull(name);
        }
    }

    @Override
    public boolean isAbbrAlreadyExists(String abbr, String excludeId) {
        if (!StringUtils.hasText(abbr)) {
            return false;
        }
        
        if (StringUtils.hasText(excludeId)) {
            // For updates - check if abbreviation exists excluding current organization
            return organizationRepository.findByAbbrAndDeletedDateIsNull(abbr)
                    .map(org -> !org.getId().equals(excludeId))
                    .orElse(false);
        } else {
            // For creation - check if abbreviation exists at all
            return organizationRepository.existsByAbbrAndDeletedDateIsNull(abbr);
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
            
            Optional<Organization> currentOpt = getOrgById(currentId);
            if (currentOpt.isEmpty()) {
                break;
            }
            Organization current = currentOpt.get();
            
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
            Optional<Organization> currentOpt = getOrgById(currentId);
            if (currentOpt.isEmpty()) {
                break;
            }
            Organization current = currentOpt.get();
            
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
                .findByParentOrganizationIdAndDeletedDateIsNull(organizationId);
        
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

    private void applyCreationBusinessLogic(Organization organization) {
        // Set creation defaults
        organization.setStatus(OrganizationStatusEnum.PENDING);
        
        if (!StringUtils.hasText(organization.getDisplayName())) {
            organization.setDisplayName(organization.getName());
        }

        log.debug("Applied creation business logic for organization: {}", organization.getName());
    }

    /**
     * Creates default domain for newly created organization.
     * The domain format is: <abbr>.codzs.com
     */
    private void createDefaultDomain(Organization organization) {
        log.debug("Creating default domain for organization ID: {}", organization.getId());
        
        Domain defaultDomain = createDefaultDomainObject(organization);
        organizationDomainService.addDomainToOrganization(organization.getId(), defaultDomain);
        
        log.info("Successfully created default domain {} for organization ID: {}", 
                defaultDomain.getName(), organization.getId());
    }

    /**
     * Creates default domain object with verified and primary status.
     */
    private Domain createDefaultDomainObject(Organization organization) {
        String domainName = organization.getAbbr().toLowerCase() + "." + OrganizationConstants.PLATFORM_DOMAIN;
        Domain domain = new Domain(domainName, "DNS");
        
        // Set as verified and primary by default
        domain.markAsVerified();
        domain.setAsPrimary();
        
        return domain;
    }

    /**
     * Creates default AUTH schema for newly created organization.
     * This schema is required for authentication services.
     */
    private void createDefaultAuthSchema(Organization organization) {
        log.debug("Creating default AUTH schema for organization ID: {}", organization.getId());
        
        // Only create AUTH schema if database configuration exists
        if (organization.getDatabase() == null) {
            log.debug("Skipping AUTH schema creation - no database configuration for organization ID: {}", 
                     organization.getId());
            return;
        }
        
        DatabaseSchema authSchema = createAuthSchemaObject(organization);
        databaseSchemaService.addDatabaseSchema(organization.getId(), authSchema);
        
        log.info("Successfully created default AUTH schema for organization ID: {}", organization.getId());
    }

    /**
     * Creates AUTH schema object with default values.
     */
    private DatabaseSchema createAuthSchemaObject(Organization organization) {
        DatabaseSchema schema = new DatabaseSchema();
        schema.setId(ObjectId.get().toString());
        schema.setForService("AUTH");
        schema.setSchemaName("auth"); // This will be auto-constructed to codzs_<org_abbr>_auth_<env>
        schema.setDescription("Default authentication schema for " + organization.getName());
        
        return schema;
    }

    // ========== ABBR-DOMAIN SYNC UTILITY METHODS ==========

    /**
     * Checks if a domain name follows the pattern {abbr}.codzs.com
     */
    private boolean isDefaultDomainPattern(String domainName) {
        if (domainName == null || domainName.trim().isEmpty()) {
            return false;
        }
        return domainName.toLowerCase().endsWith("." + OrganizationConstants.PLATFORM_DOMAIN) && 
               domainName.toLowerCase().matches("^[a-z0-9]+\\.codzs\\.com$");
    }


    /**
     * Constructs default domain name from abbr
     */
    private String constructDefaultDomainName(String abbr) {
        if (abbr == null || abbr.trim().isEmpty()) {
            return null;
        }
        return abbr.toLowerCase() + "." + OrganizationConstants.PLATFORM_DOMAIN;
    }

    /**
     * Finds the default domain ({abbr}.codzs.com) for an organization
     */
    private Domain findDefaultDomain(String organizationId) {
        List<Domain> domains = organizationDomainService.getDomainsForEntity(organizationId);
        return domains.stream()
                .filter(domain -> isDefaultDomainPattern(domain.getName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Updates organization abbr and synchronizes the corresponding default domain
     */
    private void updateAbbrWithDomainSync(String organizationId, String newAbbr, String oldAbbr, Instant now, String user) {
        log.debug("Updating abbr from {} to {} for organization ID: {} with domain sync", oldAbbr, newAbbr, organizationId);
        
        try {
            // Find the existing default domain
            Domain existingDefaultDomain = findDefaultDomain(organizationId);
            
            // Update organization abbr first
            organizationRepository.updateOrganizationAbbr(organizationId, newAbbr, now, user);
            
            // If default domain exists, update it to match new abbr
            if (existingDefaultDomain != null) {
                String newDomainName = constructDefaultDomainName(newAbbr);
                Domain updatedDomain = new Domain(existingDefaultDomain.getId(), newDomainName, existingDefaultDomain.getVerificationMethod());
                
                // Preserve existing domain properties
                updatedDomain.setIsVerified(existingDefaultDomain.getIsVerified());
                updatedDomain.setIsPrimary(existingDefaultDomain.getIsPrimary());
                updatedDomain.setVerifiedDate(existingDefaultDomain.getVerifiedDate());
                updatedDomain.setVerificationToken(existingDefaultDomain.getVerificationToken());
                
                organizationDomainService.updateDomainInOrganization(organizationId, updatedDomain);
                
                log.info("Successfully synchronized abbr and default domain for organization ID: {} from {}.codzs.com to {}.codzs.com", 
                        organizationId, oldAbbr.toLowerCase(), newAbbr.toLowerCase());
            } else {
                log.debug("No default domain found for organization ID: {}, only abbr was updated", organizationId);
            }
            
        } catch (Exception e) {
            log.error("Failed to sync abbr and domain for organization ID: {}", organizationId, e);
            throw e; // Re-throw to ensure transaction rollback
        }
    }


    private void updateChangedFields(Organization newOrg, Organization existingOrg) {
        Instant now = Instant.now();
        String user = getCurrentUser();
        
        // Update each field only if it has changed
        if (hasFieldChanged(newOrg.getName(), existingOrg.getName())) {
            organizationRepository.updateOrganizationName(newOrg.getId(), newOrg.getName(), now, user);
        }
        
        if (hasFieldChanged(newOrg.getAbbr(), existingOrg.getAbbr())) {
            updateAbbrWithDomainSync(newOrg.getId(), newOrg.getAbbr(), existingOrg.getAbbr(), now, user);
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

    // ========== ORGANIZATION RETRIEVAL METHODS ==========

    @Override
    public Optional<Organization> getOrganizationById(String organizationId) {
        log.debug("Getting organization by ID: {}", organizationId);
        
        return getOrgById(organizationId);
    }

    @Override
    public Optional<Organization> getOrganizationById(String organizationId, List<OrganizationProjectionEnum> include) {
        log.debug("Getting organization by ID: {} with include filters: {}", organizationId, include);
        return super.getOrgById(organizationId, include);
    }
}