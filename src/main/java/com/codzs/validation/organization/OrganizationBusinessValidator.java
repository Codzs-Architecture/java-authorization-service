package com.codzs.validation.organization;

import com.codzs.constant.organization.OrganizationConstants;
import com.codzs.constant.organization.OrganizationStatusEnum;
import com.codzs.constant.organization.OrganizationTypeEnum;
import com.codzs.entity.organization.Organization;
import com.codzs.exception.validation.ValidationException;
import com.codzs.service.organization.OrganizationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Business validator for Organization-related operations.
 * Provides entry point methods for organization API endpoints
 * and delegates to specific validators for detailed validation logic.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
public class OrganizationBusinessValidator {
    
    private final OrganizationService organizationService;
    private final OrganizationPlanBusinessValidator organizationPlanBusinessValidator;
    private final OrganizationTypeEnum organizationTypeEnum;

    @Autowired
    public OrganizationBusinessValidator(OrganizationService organizationService,
                                       OrganizationPlanBusinessValidator organizationPlanBusinessValidator,
                                       OrganizationTypeEnum organizationTypeEnum) {
        this.organizationService = organizationService;
        this.organizationPlanBusinessValidator = organizationPlanBusinessValidator;
        this.organizationTypeEnum = organizationTypeEnum;
    }

    // ========== ENTRY POINT METHODS FOR ORGANIZATION APIs ==========

    /**
     * Entry point for organization creation business validation.
     * API: POST /api/v1/organizations
     *
     * @param organization the organization entity
     * @throws ValidationException if business validation fails
     */
    public void validateOrganizationCreationFlow(Organization organization) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();

        validateOrganizationUniqueness(organization.getName(), organization.getAbbr(), null, errors);
        validateOrganizationBusinessRules(organization, errors);
        validateOrganizationHierarchyRules(organization.getParentOrganizationId(), null, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Organization creation business validation failed", errors);
        }
    }

    /**
     * Entry point for organization update business validation.
     * API: PUT /api/v1/organizations/{id}
     *
     * @param organization the organization entity with updates
     * @throws ValidationException if business validation fails
     */
    public void validateOrganizationUpdateFlow(Organization organization) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();

        Organization existingOrg = validateOrganizationExistsAndUpdatable(organization.getId(), errors);
        if (existingOrg == null) {
            throw new ValidationException("Organization update business validation failed", errors);
        }

        if (hasFieldChanged(organization.getName(), existingOrg.getName()) || 
            hasFieldChanged(organization.getAbbr(), existingOrg.getAbbr())) {
            validateOrganizationUniqueness(organization.getName(), organization.getAbbr(), organization.getId(), errors);
        }

        if (hasFieldChanged(organization.getParentOrganizationId(), existingOrg.getParentOrganizationId())) {
            validateOrganizationHierarchyRules(organization.getParentOrganizationId(), organization.getId(), errors);
        }

        validateUpdateBusinessConstraints(existingOrg, organization, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Organization update business validation failed", errors);
        }
    }

    /**
     * Entry point for organization plan association business validation.
     * API: POST /api/v1/organizations/{id}/plans
     *
     * @param organization the organization entity
     * @param organizationPlan the organization plan entity
     * @throws ValidationException if business validation fails
     */
    public void validateOrganizationPlanAssociationFlow(Organization organization, com.codzs.entity.organization.OrganizationPlan organizationPlan) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();

        validateOrganizationExistsAndActive(organization.getId(), errors);

        organizationPlanBusinessValidator.validatePlanAssociationForOrganization(organization, organizationPlan, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Organization plan association business validation failed", errors);
        }
    }


    /**
     * Entry point for organization activation business validation.
     * API: PUT /api/v1/organizations/{id}/activate
     *
     * @param organizationId the organization ID to activate
     * @throws ValidationException if business validation fails
     */
    public void validateOrganizationActivationFlow(String organizationId) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();

        Organization organization = validateOrganizationExists(organizationId, errors);
        if (organization == null) {
            throw new ValidationException("Organization activation business validation failed", errors);
        }

        validateOrganizationStatusTransition(organization.getStatus(), OrganizationStatusEnum.ACTIVE, errors);
        validateActivationBusinessRules(organization, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Organization activation business validation failed", errors);
        }
    }

    /**
     * Entry point for organization deactivation business validation.
     * API: PUT /api/v1/organizations/{id}/deactivate
     *
     * @param organizationId the organization ID to deactivate
     * @throws ValidationException if business validation fails
     */
    public void validateOrganizationDeactivationFlow(String organizationId) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();

        Organization organization = validateOrganizationExists(organizationId, errors);
        if (organization == null) {
            throw new ValidationException("Organization deactivation business validation failed", errors);
        }

        validateOrganizationStatusTransition(organization.getStatus(), OrganizationStatusEnum.SUSPENDED, errors);
        validateDeactivationBusinessRules(organization, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Organization deactivation business validation failed", errors);
        }
    }

    /**
     * Entry point for organization deletion business validation.
     * API: DELETE /api/v1/organizations/{id}
     *
     * @param organization the organization entity to delete
     * @throws ValidationException if business validation fails
     */
    public void validateOrganizationDeletionFlow(Organization organization) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();

        validateDeletionBusinessRules(organization, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Organization deletion business validation failed", errors);
        }
    }

    // ========== CORE VALIDATION METHODS ==========

    private void validateOrganizationUniqueness(String name, String abbr, String excludeId, 
                                               List<ValidationException.ValidationError> errors) {
        if (StringUtils.hasText(name)) {
            validateOrganizationNameUniqueness(name, excludeId, errors);
        }
        if (StringUtils.hasText(abbr)) {
            validateOrganizationAbbrUniqueness(abbr, excludeId, errors);
        }
    }

    private void validateOrganizationBusinessRules(Organization organization, 
                                                  List<ValidationException.ValidationError> errors) {
        if (organization.getExpiresDate() != null && organization.getExpiresDate().isBefore(Instant.now())) {
            errors.add(new ValidationException.ValidationError("expiresDate", "Expiry date cannot be in the past"));
        }
    }

    private void validateOrganizationHierarchyRules(String parentId, String childId, 
                                                   List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(parentId)) {
            return;
        }
        
        Organization parent = validateOrganizationExistsAndActive(parentId, errors);
        if (parent == null) {
            return;
        }
        
        if (parentId.equals(childId)) {
            errors.add(new ValidationException.ValidationError("parentOrganizationId", 
                "Organization cannot be its own parent"));
            return;
        }
        
        if (childId != null && organizationService.wouldCreateCircularReference(parentId, childId)) {
            errors.add(new ValidationException.ValidationError("parentOrganizationId", 
                "Circular reference detected in organization hierarchy"));
        }
        
        int depth = organizationService.calculateOrganizationHierarchyDepth(parentId);
        if (depth >= OrganizationConstants.MAX_HIERARCHY_DEPTH) {
            errors.add(new ValidationException.ValidationError("parentOrganizationId", 
                "Maximum hierarchy depth of " + OrganizationConstants.MAX_HIERARCHY_DEPTH + " levels exceeded"));
        }
    }


    private void validateUpdateBusinessConstraints(Organization existingOrg, Organization updatedOrg, 
                                                 List<ValidationException.ValidationError> errors) {
        if (hasFieldChanged(updatedOrg.getOrganizationType(), existingOrg.getOrganizationType())) {
            if (organizationService.hasActiveSubscriptions(existingOrg.getId())) {
                errors.add(new ValidationException.ValidationError("organizationType", 
                    "Cannot change organization type while active subscriptions exist"));
            }
        }

        if (existingOrg.getStatus() == OrganizationStatusEnum.SUSPENDED) {
            if (hasFieldChanged(updatedOrg.getOrganizationType(), existingOrg.getOrganizationType()) ||
                hasFieldChanged(updatedOrg.getParentOrganizationId(), existingOrg.getParentOrganizationId())) {
                errors.add(new ValidationException.ValidationError("status", 
                    "Cannot modify organization type or hierarchy while suspended"));
            }
        }
    }

    private void validateActivationBusinessRules(Organization organization, 
                                                List<ValidationException.ValidationError> errors) {
        if (organization.getDatabase() == null || !StringUtils.hasText(organization.getDatabase().getConnectionString())) {
            errors.add(new ValidationException.ValidationError("database", 
                "Organization must have valid database configuration before activation"));
        }


        if (StringUtils.hasText(organization.getParentOrganizationId())) {
            Organization parent = organizationService.findById(organization.getParentOrganizationId());
            if (parent != null && parent.getStatus() != OrganizationStatusEnum.ACTIVE) {
                errors.add(new ValidationException.ValidationError("parentOrganizationId", 
                    "Parent organization must be active before child organization can be activated"));
            }
        }
    }

    private void validateDeactivationBusinessRules(Organization organization, 
                                                  List<ValidationException.ValidationError> errors) {
        if (organizationService.hasActiveChildOrganizations(organization.getId())) {
            errors.add(new ValidationException.ValidationError("childOrganizations", 
                "Cannot deactivate organization with active child organizations"));
        }

        if (organizationService.hasActiveTenants(organization.getId())) {
            errors.add(new ValidationException.ValidationError("tenants", 
                "Cannot deactivate organization with active tenants"));
        }
    }

    private void validateDeletionBusinessRules(Organization organization, 
                                              List<ValidationException.ValidationError> errors) {
        if (organizationService.hasActiveChildOrganizations(organization.getId())) {
            errors.add(new ValidationException.ValidationError("childOrganizations", 
                "Cannot delete organization with active child organizations"));
        }

        if (organizationService.hasActiveTenants(organization.getId())) {
            errors.add(new ValidationException.ValidationError("tenants", 
                "Cannot delete organization with active tenants"));
        }
    }

    private void validateOrganizationNameUniqueness(String name, String excludeId, 
                                                   List<ValidationException.ValidationError> errors) {
        if (organizationService.isNameAlreadyExists(name, excludeId)) {
            errors.add(new ValidationException.ValidationError("name", "Organization name already exists"));
        }
    }

    private void validateOrganizationAbbrUniqueness(String abbr, String excludeId, 
                                                   List<ValidationException.ValidationError> errors) {
        if (organizationService.isAbbrAlreadyExists(abbr, excludeId)) {
            errors.add(new ValidationException.ValidationError("abbr", "Organization abbreviation already exists"));
        }
    }

    private Organization validateOrganizationExists(String organizationId, 
                                                   List<ValidationException.ValidationError> errors) {
        // Organization ID required validation is handled by @NotBlank in request DTOs
        
        Organization organization = organizationService.findById(organizationId);
        if (organization == null) {
            errors.add(new ValidationException.ValidationError("organizationId", "Organization not found"));
            return null;
        }
        
        return organization;
    }

    private Organization validateOrganizationExistsAndActive(String organizationId, 
                                                           List<ValidationException.ValidationError> errors) {
        Organization organization = validateOrganizationExists(organizationId, errors);
        if (organization == null) {
            return null;
        }
        
        if (organization.getStatus() == OrganizationStatusEnum.DELETED) {
            errors.add(new ValidationException.ValidationError("organizationId", "Organization is deleted"));
            return null;
        }
        
        return organization;
    }

    private Organization validateOrganizationExistsAndUpdatable(String organizationId, 
                                                              List<ValidationException.ValidationError> errors) {
        Organization organization = validateOrganizationExists(organizationId, errors);
        if (organization == null) {
            return null;
        }
        
        if (organization.getStatus() == OrganizationStatusEnum.DELETED) {
            errors.add(new ValidationException.ValidationError("organizationId", 
                "Cannot update deleted organization"));
            return null;
        }
        
        return organization;
    }

    private void validateOrganizationStatusTransition(OrganizationStatusEnum currentStatus, 
                                                     OrganizationStatusEnum newStatus, 
                                                     List<ValidationException.ValidationError> errors) {
        // Skip validation if organization is already in target status (idempotent operation)
        if (currentStatus == newStatus) {
            return;
        }
        
        if (currentStatus == OrganizationStatusEnum.DELETED) {
            errors.add(new ValidationException.ValidationError("status", 
                "Cannot change status of deleted organization"));
        }
    }

    private boolean hasFieldChanged(String newValue, String existingValue) {
        if (newValue == null) {
            return false;
        }
        return !newValue.equals(existingValue);
    }
}