package com.codzs.validation.organization;

import com.codzs.constant.organization.OrganizationConstants;
import com.codzs.constant.organization.OrganizationStatusEnum;
import com.codzs.entity.organization.Organization;
import com.codzs.framework.constant.CommonConstants;
import com.codzs.exception.bean.ValidationError;
import com.codzs.exception.type.ValidationException;

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
        List<ValidationError> errors = new ArrayList<>();

        validateOrganizationUniqueness(organization, isNameAlreadyExists, isAbbrAlreadyExists, errors);
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
        List<ValidationError> errors = new ArrayList<>();

        Organization existingOrg = validateOrganizationExistsAndUpdatable(organization, errors);
        if (existingOrg == null) {
            throw new ValidationException("Organization update business validation failed", errors);
        }

        if (hasFieldChanged(organization.getName(), existingOrg.getName()) || 
            hasFieldChanged(organization.getAbbr(), existingOrg.getAbbr())) {
            validateOrganizationUniqueness(organization, isNameAlreadyExists, isAbbrAlreadyExists, errors);
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
        List<ValidationError> errors = new ArrayList<>();

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
        List<ValidationError> errors = new ArrayList<>();

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
        List<ValidationError> errors = new ArrayList<>();

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
        List<ValidationError> errors = new ArrayList<>();

        Boolean skipFutherStep = validateDeletionBusinessRules(organization, hasActiveChildOrganizations, hasActiveTenants, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Organization deletion business validation failed", errors);
        }

        return skipFutherStep;
    }

    // ========== CORE VALIDATION METHODS ==========

    private void validateOrganizationUniqueness(Organization organization, boolean isNameAlreadyExists, boolean isAbbrAlreadyExists, 
                                               List<ValidationError> errors) {
        validateOrganizationNameUniqueness(organization, isNameAlreadyExists, errors);
        validateOrganizationAbbrUniqueness(organization, isAbbrAlreadyExists, errors);
    }

    private void validateOrganizationBusinessRules(Organization organization, 
                                                  List<ValidationError> errors) {
        if (organization.getExpiresDate() != null && organization.getExpiresDate().isBefore(Instant.now())) {
            errors.add(
                ValidationError
                    .builder()
                    .field("expiresDate")
                    .rejectedValue(organization.getExpiresDate())
                    .message("Expiry date cannot be in the past")
                    .build()
            );
        }
    }

    private void validateOrganizationHierarchyRules(Optional<Organization> parentOrganization, String childId, 
                                                   boolean wouldCreateCircularReference, int depth,
                                                   List<ValidationError> errors) {
        if (!parentOrganization.isPresent()) {
            return;
        }
        
        Organization parent = validateOrganizationExistsAndActive(parentOrganization, errors);
        if (parent == null) {
            return;
        }
        
        if (parent.getId().equals(childId)) {
            errors.add(
                ValidationError
                    .builder()
                    .field("parentOrganizationId")
                    .rejectedValue(parent.getId())
                    .message("Organization cannot be its own parent")
                    .build()
            );
            return;
        }
        
        if (childId != null && wouldCreateCircularReference) {
            errors.add(
                ValidationError
                    .builder()
                    .field("parentOrganizationId")
                    .rejectedValue(parent.getId())
                    .message("Circular reference detected in organization hierarchy")
                    .build()
            );
        }
        
        if (depth >= OrganizationConstants.MAX_HIERARCHY_DEPTH) {
            errors.add(
                ValidationError
                    .builder()
                    .field("parentOrganizationId")
                    .rejectedValue(parent.getId())
                    .message("Maximum hierarchy depth of " + OrganizationConstants.MAX_HIERARCHY_DEPTH + " levels exceeded")
                    .build()
            );
        }
    }


    private void validateUpdateBusinessConstraints(Organization existingOrg, Organization updatedOrg, boolean hasActiveSubscriptions,
                                                 List<ValidationError> errors) {
        if (hasFieldChanged(updatedOrg.getOrganizationType(), existingOrg.getOrganizationType())) {
            if (hasActiveSubscriptions) {
                errors.add(
                    ValidationError
                        .builder()
                        .field("organizationType")
                        .rejectedValue(updatedOrg.getOrganizationType())
                        .message("Cannot change organization type while active subscriptions exist")
                        .build()
                );
            }
        }

        if (existingOrg.getStatus() == OrganizationStatusEnum.SUSPENDED) {
            if (hasFieldChanged(updatedOrg.getOrganizationType(), existingOrg.getOrganizationType()) ||
                hasFieldChanged(updatedOrg.getParentOrganizationId(), existingOrg.getParentOrganizationId())) {
                errors.add(
                    ValidationError
                        .builder()
                        .field("status")
                        .rejectedValue(updatedOrg.getOrganizationType())
                        .message("Cannot modify organization type or hierarchy while suspended")
                        .build()
                );
            }
        }
    }

    private void validateActivationBusinessRules(Organization organization, Optional<Organization> parentOrganization,
                                                List<ValidationError> errors) {
        if (organization.getDatabase() == null || !StringUtils.hasText(organization.getDatabase().getConnectionString())) {
            errors.add(
                ValidationError
                    .builder()
                    .field("database")
                    .rejectedValue(organization.getDatabase())
                    .message("Organization must have valid database configuration before activation")
                    .build()
            );
        }

        if (StringUtils.hasText(organization.getParentOrganizationId())) {
            parentOrganization
                    .ifPresent(parent -> {
                        if (parent.getStatus() != OrganizationStatusEnum.ACTIVE) {
                            errors.add(
                                ValidationError
                                    .builder()
                                    .field("parentOrganizationId")
                                    .rejectedValue(parentOrganization.get().getId())
                                    .message("Parent organization must be active before child organization can be activated")
                                    .build()
                            );
                        }
                    });
        }
    }

    private void validateDeactivationBusinessRules(Organization organization, 
                                                  boolean hasActiveChildOrganizations, boolean hasActiveTenants,
                                                  List<ValidationError> errors) {
        if (hasActiveChildOrganizations) {
            errors.add(
                ValidationError
                    .builder()
                    .field("childOrganizations")
                    .rejectedValue(hasActiveChildOrganizations)
                    .message("Cannot deactivate organization with active child organizations")
                    .build()
            );
        }

        if (hasActiveTenants) {
            errors.add(
                ValidationError
                    .builder()
                    .field("tenants")
                    .rejectedValue(hasActiveTenants)
                    .message("Cannot deactivate organization with active tenants")
                    .build()
            );
        }
    }

    private Boolean validateDeletionBusinessRules(Organization organization, 
                                              boolean hasActiveChildOrganizations, boolean hasActiveTenants,
                                              List<ValidationError> errors) {
        
        if (organization.getStatus() == OrganizationStatusEnum.DELETED) {
            return CommonConstants.SKIP_FURTHER_STEP;
        }
        
        if (hasActiveChildOrganizations) {
            errors.add(
                ValidationError
                    .builder()
                    .field("childOrganizations")
                    .rejectedValue(hasActiveChildOrganizations)
                    .message("Cannot delete organization with active child organizations")
                    .build()
            );
        }

        if (hasActiveTenants) {
            errors.add(
                ValidationError
                    .builder()
                    .field("tenants")
                    .rejectedValue(hasActiveTenants)
                    .message("Cannot delete organization with active tenants")
                    .build()
            );
        }

        return !CommonConstants.SKIP_FURTHER_STEP;
    }

    private void validateOrganizationNameUniqueness(Organization organization, boolean isNameAlreadyExists, 
                                                   List<ValidationError> errors) {
        if (isNameAlreadyExists) {
            errors.add(
                ValidationError
                    .builder()
                    .field("name")
                    .rejectedValue(organization.getName())
                    .message("Organization name already exists")
                    .build()
            );
        }
    }

    private void validateOrganizationAbbrUniqueness(Organization organization, boolean isAbbrAlreadyExists, 
                                                   List<ValidationError> errors) {
        if (isAbbrAlreadyExists) {
            errors.add(
                ValidationError
                    .builder()
                    .field("abbr")
                    .rejectedValue(organization.getAbbr())
                    .message("Organization abbreviation already exists")
                    .build()
            );
        }
    }

    private Organization validateOrganizationExistsAndActive(Optional<Organization> organization, 
                                                           List<ValidationError> errors) {
        if (organization.get() == null) {
            return null;
        }
        
        if (organization.get().getStatus() == OrganizationStatusEnum.DELETED) {
            errors.add(
                ValidationError
                    .builder()
                    .field("organizationId")
                    .rejectedValue(organization.get().getId())
                    .message("Organization is deleted")
                    .build()
            );
            return null;
        }
        
        return organization.get();
    }

    private Organization validateOrganizationExistsAndUpdatable(Organization organization, 
                                                              List<ValidationError> errors) {
        if (organization == null) {
            return null;
        }
        
        if (organization.getStatus() == OrganizationStatusEnum.DELETED) {
            errors.add(
                ValidationError
                    .builder()
                    .field("organizationId")
                    .rejectedValue(organization.getId())
                    .message("Cannot update deleted organization")
                    .build()
            );
            return null;
        }
        
        return organization;
    }

    private Boolean validateOrganizationStatusTransition(OrganizationStatusEnum currentStatus, 
                                                     OrganizationStatusEnum newStatus, 
                                                     List<ValidationError> errors) {
        // Skip validation if organization is already in target status (idempotent operation)
        if (currentStatus == newStatus) {
            return CommonConstants.SKIP_FURTHER_STEP;
        }
        
        if (currentStatus == OrganizationStatusEnum.DELETED) {
            errors.add(
                ValidationError
                    .builder()
                    .field("status")
                    .rejectedValue(newStatus)
                    .message("Cannot change status of deleted organization")
                    .build()
            );
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