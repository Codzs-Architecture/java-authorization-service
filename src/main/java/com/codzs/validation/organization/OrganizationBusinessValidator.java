package com.codzs.validation.organization;

import com.codzs.constant.organization.OrganizationConstants;
import com.codzs.constant.organization.OrganizationStatusEnum;
import com.codzs.entity.organization.Organization;
import com.codzs.framework.constant.CommonConstants;
import com.codzs.framework.exception.type.ValidationException;
import com.codzs.service.organization.OrganizationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    
    private final OrganizationPlanBusinessValidator organizationPlanBusinessValidator;

    @Autowired
    public OrganizationBusinessValidator(OrganizationPlanBusinessValidator organizationPlanBusinessValidator) {
        this.organizationPlanBusinessValidator = organizationPlanBusinessValidator;
    }

    // ========== ENTRY POINT METHODS FOR ORGANIZATION APIs ==========

    /**
     * Entry point for organization creation business validation.
     * API: POST /api/v1/organizations
     *
     * @param organization the organization entity
     * @throws ValidationException if business validation fails
     */
    public void validateOrganizationCreationFlow(Organization organization, boolean wouldCreateCircularReference, int depth, boolean isNameAlreadyExists, boolean isAbbrAlreadyExists, Optional<Organization> parentOrganization) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();

        validateOrganizationUniqueness(isNameAlreadyExists, isAbbrAlreadyExists, errors);
        validateOrganizationBusinessRules(organization, errors);
        validateOrganizationHierarchyRules(parentOrganization, null, wouldCreateCircularReference, depth, errors);

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
    public void validateOrganizationUpdateFlow(Organization organization, boolean wouldCreateCircularReference, int depth, boolean hasActiveSubscriptions, boolean isNameAlreadyExists, boolean isAbbrAlreadyExists, Optional<Organization> parentOrganization) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();

        Organization existingOrg = validateOrganizationExistsAndUpdatable(organization, errors);
        if (existingOrg == null) {
            throw new ValidationException("Organization update business validation failed", errors);
        }

        if (hasFieldChanged(organization.getName(), existingOrg.getName()) || 
            hasFieldChanged(organization.getAbbr(), existingOrg.getAbbr())) {
            validateOrganizationUniqueness(isNameAlreadyExists, isAbbrAlreadyExists, errors);
        }

        if (hasFieldChanged(organization.getParentOrganizationId(), existingOrg.getParentOrganizationId())) {
            validateOrganizationHierarchyRules(parentOrganization, organization.getId(), wouldCreateCircularReference, depth, errors);
        }

        validateUpdateBusinessConstraints(existingOrg, organization, hasActiveSubscriptions, errors);

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

        validateOrganizationExistsAndUpdatable(organization, errors);

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
    public Boolean validateOrganizationActivationFlow(Organization organization, Optional<Organization> parentOrganization) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();

        if (organization == null) {
            throw new ValidationException("Organization activation business validation failed", errors);
        }

        Boolean skipFutherStep = validateOrganizationStatusTransition(organization.getStatus(), OrganizationStatusEnum.ACTIVE, errors);
        if (skipFutherStep == CommonConstants.SKIP_FURTHER_STEP) {
            return skipFutherStep;
        }

        validateActivationBusinessRules(organization, parentOrganization, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Organization activation business validation failed", errors);
        }

        return !CommonConstants.SKIP_FURTHER_STEP;
    }

    /**
     * Entry point for organization deactivation business validation.
     * API: PUT /api/v1/organizations/{id}/deactivate
     *
     * @param organizationId the organization ID to deactivate
     * @throws ValidationException if business validation fails
     */
    public Boolean validateOrganizationDeactivationFlow(Organization organization, boolean hasActiveChildOrganizations, boolean hasActiveTenants) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();

        if (organization == null) {
            throw new ValidationException("Organization deactivation business validation failed", errors);
        }

        Boolean skipFutherStep = validateOrganizationStatusTransition(organization.getStatus(), OrganizationStatusEnum.SUSPENDED, errors);
        if (skipFutherStep == CommonConstants.SKIP_FURTHER_STEP) {
            return skipFutherStep;
        }

        validateDeactivationBusinessRules(organization, hasActiveChildOrganizations, hasActiveTenants, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Organization deactivation business validation failed", errors);
        }

        return !CommonConstants.SKIP_FURTHER_STEP;
    }

    /**
     * Entry point for organization deletion business validation.
     * API: DELETE /api/v1/organizations/{id}
     *
     * @param organization the organization entity to delete
     * @throws ValidationException if business validation fails
     */
    public Boolean validateOrganizationDeletionFlow(Organization organization, boolean hasActiveChildOrganizations, boolean hasActiveTenants) throws ValidationException {
        List<ValidationException.ValidationError> errors = new ArrayList<>();

        Boolean skipFutherStep = validateDeletionBusinessRules(organization, hasActiveChildOrganizations, hasActiveTenants, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Organization deletion business validation failed", errors);
        }

        return skipFutherStep;
    }

    // ========== CORE VALIDATION METHODS ==========

    private void validateOrganizationUniqueness(boolean isNameAlreadyExists, boolean isAbbrAlreadyExists, 
                                               List<ValidationException.ValidationError> errors) {
        validateOrganizationNameUniqueness(isNameAlreadyExists, errors);
        validateOrganizationAbbrUniqueness(isAbbrAlreadyExists, errors);
    }

    private void validateOrganizationBusinessRules(Organization organization, 
                                                  List<ValidationException.ValidationError> errors) {
        if (organization.getExpiresDate() != null && organization.getExpiresDate().isBefore(Instant.now())) {
            errors.add(new ValidationException.ValidationError("expiresDate", "Expiry date cannot be in the past"));
        }
    }

    private void validateOrganizationHierarchyRules(Optional<Organization> parentOrganization, String childId, 
                                                   boolean wouldCreateCircularReference, int depth,
                                                   List<ValidationException.ValidationError> errors) {
        if (!parentOrganization.isPresent()) {
            return;
        }
        
        Organization parent = validateOrganizationExistsAndActive(parentOrganization, errors);
        if (parent == null) {
            return;
        }
        
        if (parent.getId().equals(childId)) {
            errors.add(new ValidationException.ValidationError("parentOrganizationId", 
                "Organization cannot be its own parent"));
            return;
        }
        
        if (childId != null && wouldCreateCircularReference) {
            errors.add(new ValidationException.ValidationError("parentOrganizationId", 
                "Circular reference detected in organization hierarchy"));
        }
        
        if (depth >= OrganizationConstants.MAX_HIERARCHY_DEPTH) {
            errors.add(new ValidationException.ValidationError("parentOrganizationId", 
                "Maximum hierarchy depth of " + OrganizationConstants.MAX_HIERARCHY_DEPTH + " levels exceeded"));
        }
    }


    private void validateUpdateBusinessConstraints(Organization existingOrg, Organization updatedOrg, boolean hasActiveSubscriptions,
                                                 List<ValidationException.ValidationError> errors) {
        if (hasFieldChanged(updatedOrg.getOrganizationType(), existingOrg.getOrganizationType())) {
            if (hasActiveSubscriptions) {
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

    private void validateActivationBusinessRules(Organization organization, Optional<Organization> parentOrganization,
                                                List<ValidationException.ValidationError> errors) {
        if (organization.getDatabase() == null || !StringUtils.hasText(organization.getDatabase().getConnectionString())) {
            errors.add(new ValidationException.ValidationError("database", 
                "Organization must have valid database configuration before activation"));
        }

        if (StringUtils.hasText(organization.getParentOrganizationId())) {
            parentOrganization
                    .ifPresent(parent -> {
                        if (parent.getStatus() != OrganizationStatusEnum.ACTIVE) {
                            errors.add(new ValidationException.ValidationError("parentOrganizationId", 
                                "Parent organization must be active before child organization can be activated"));
                        }
                    });
        }
    }

    private void validateDeactivationBusinessRules(Organization organization, 
                                                  boolean hasActiveChildOrganizations, boolean hasActiveTenants,
                                                  List<ValidationException.ValidationError> errors) {
        if (hasActiveChildOrganizations) {
            errors.add(new ValidationException.ValidationError("childOrganizations", 
                "Cannot deactivate organization with active child organizations"));
        }

        if (hasActiveTenants) {
            errors.add(new ValidationException.ValidationError("tenants", 
                "Cannot deactivate organization with active tenants"));
        }
    }

    private Boolean validateDeletionBusinessRules(Organization organization, 
                                              boolean hasActiveChildOrganizations, boolean hasActiveTenants,
                                              List<ValidationException.ValidationError> errors) {
        
        if (organization.getStatus() == OrganizationStatusEnum.DELETED) {
            return CommonConstants.SKIP_FURTHER_STEP;
        }
        
        if (hasActiveChildOrganizations) {
            errors.add(new ValidationException.ValidationError("childOrganizations", 
                "Cannot delete organization with active child organizations"));
        }

        if (hasActiveTenants) {
            errors.add(new ValidationException.ValidationError("tenants", 
                "Cannot delete organization with active tenants"));
        }

        return !CommonConstants.SKIP_FURTHER_STEP;
    }

    private void validateOrganizationNameUniqueness(boolean isNameAlreadyExists, 
                                                   List<ValidationException.ValidationError> errors) {
        if (isNameAlreadyExists) {
            errors.add(new ValidationException.ValidationError("name", "Organization name already exists"));
        }
    }

    private void validateOrganizationAbbrUniqueness(boolean isAbbrAlreadyExists, 
                                                   List<ValidationException.ValidationError> errors) {
        if (isAbbrAlreadyExists) {
            errors.add(new ValidationException.ValidationError("abbr", "Organization abbreviation already exists"));
        }
    }

    private Organization validateOrganizationExistsAndActive(Optional<Organization> organization, 
                                                           List<ValidationException.ValidationError> errors) {
        if (organization.get() == null) {
            return null;
        }
        
        if (organization.get().getStatus() == OrganizationStatusEnum.DELETED) {
            errors.add(new ValidationException.ValidationError("organizationId", "Organization is deleted"));
            return null;
        }
        
        return organization.get();
    }

    private Organization validateOrganizationExistsAndUpdatable(Organization organization, 
                                                              List<ValidationException.ValidationError> errors) {
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

    private Boolean validateOrganizationStatusTransition(OrganizationStatusEnum currentStatus, 
                                                     OrganizationStatusEnum newStatus, 
                                                     List<ValidationException.ValidationError> errors) {
        // Skip validation if organization is already in target status (idempotent operation)
        if (currentStatus == newStatus) {
            return CommonConstants.SKIP_FURTHER_STEP;
        }
        
        if (currentStatus == OrganizationStatusEnum.DELETED) {
            errors.add(new ValidationException.ValidationError("status", 
                "Cannot change status of deleted organization"));
        }

        return !CommonConstants.SKIP_FURTHER_STEP;
    }

    private boolean hasFieldChanged(String newValue, String existingValue) {
        if (newValue == null) {
            return false;
        }
        return !newValue.equals(existingValue);
    }
}