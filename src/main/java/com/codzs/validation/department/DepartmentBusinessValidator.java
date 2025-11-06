package com.codzs.validation.department;

import com.codzs.constant.organization.OrganizationStatusEnum;
import com.codzs.entity.department.Department;
import com.codzs.entity.organization.Organization;
import com.codzs.exception.validation.ValidationException;
import com.codzs.framework.constant.CommonConstants;
import com.codzs.service.organization.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Business validator for Department-related operations.
 * Handles department business rules, hierarchy validation, and organization/tenant relationships.
 * Uses service layer to fetch external data for validation.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
@Slf4j
public class DepartmentBusinessValidator {

    private final OrganizationService organizationService;

    @Autowired
    public DepartmentBusinessValidator(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    // ========== ENTRY POINT METHODS FOR DEPARTMENT APIs ==========

    /**
     * Validates department creation for service layer.
     * Entry point for: POST /api/v1/departments
     */
    public void validateDepartmentCreation(Department department) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateDepartmentCreationFlow(department, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Department creation validation failed", errors);
        }
    }

    /**
     * Validates department update for service layer.
     * Entry point for: PUT /api/v1/departments/{id}
     */
    public void validateDepartmentUpdate(Department department) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateDepartmentUpdateFlow(department, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Department update validation failed", errors);
        }
    }

    /**
     * Validates department activation for service layer.
     * Entry point for: PUT /api/v1/departments/{id}/activate
     */
    public void validateDepartmentActivation(Department department) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateDepartmentActivationFlow(department, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Department activation validation failed", errors);
        }
    }

    /**
     * Validates department deactivation for service layer.
     * Entry point for: PUT /api/v1/departments/{id}/deactivate
     */
    public void validateDepartmentDeactivation(Department department) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateDepartmentDeactivationFlow(department, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Department deactivation validation failed", errors);
        }
    }

    /**
     * Validates department deletion for service layer.
     * Entry point for: DELETE /api/v1/departments/{id}
     */
    public void validateDepartmentDeletion(Department department) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateDepartmentDeletionFlow(department, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Department deletion validation failed", errors);
        }
    }

    // ========== CORE VALIDATION METHODS ==========

    private void validateDepartmentCreationFlow(Department department, List<ValidationException.ValidationError> errors) {
        log.debug("Validating department creation for department: {}", department.getName());
        
        // Validate organization exists and is active using service layer
        validateOrganizationForDepartment(department.getOrganizationId(), errors);
        
        // Validate parent department hierarchy if specified
        if (StringUtils.hasText(department.getParentDepartmentId())) {
            // TODO: Department hierarchy validation will be implemented when department service is available
        }
        
        // TODO: Validate tenant association when tenant service is available
        
        log.debug("Completed department creation validation for department: {}", department.getName());
    }

    private void validateDepartmentUpdateFlow(Department department, List<ValidationException.ValidationError> errors) {
        log.debug("Validating department update for department: {}", department.getName());
        
        // Validate organization exists if changing organization
        if (StringUtils.hasText(department.getOrganizationId())) {
            validateOrganizationForDepartment(department.getOrganizationId(), errors);
        }
        
        // Validate parent department hierarchy if changing parent
        if (StringUtils.hasText(department.getParentDepartmentId())) {
            // TODO: Department hierarchy validation will be implemented when department service is available
            validateNoCircularReference(department, errors);
        }
        
        log.debug("Completed department update validation for department: {}", department.getName());
    }

    private void validateDepartmentActivationFlow(Department department, List<ValidationException.ValidationError> errors) {
        log.debug("Validating department activation for department: {}", department.getName());
        
        // Skip validation if department is already active (idempotent operation)
        if (CommonConstants.ACTIVE.equals(department.getStatus())) {
            log.debug("Department is already active, skipping validation for idempotent operation");
            return;
        }
        
        // Validate organization is active using service layer
        validateOrganizationForDepartment(department.getOrganizationId(), errors);
        
        // TODO: Tenant and parent department validation will be implemented when respective services are available
        
        log.debug("Completed department activation validation for department: {}", department.getName());
    }

    private void validateDepartmentDeactivationFlow(Department department, List<ValidationException.ValidationError> errors) {
        log.debug("Validating department deactivation for department: {}", department.getName());
        
        // Skip validation if department is already inactive (idempotent operation)
        if (!CommonConstants.ACTIVE.equals(department.getStatus())) {
            log.debug("Department is already inactive, skipping validation for idempotent operation");
            return;
        }
        
        // TODO: Active users and child departments validation will be implemented when respective services are available
        
        log.debug("Completed department deactivation validation for department: {}", department.getName());
    }

    private void validateDepartmentDeletionFlow(Department department, List<ValidationException.ValidationError> errors) {
        log.debug("Validating department deletion for department: {}", department.getName());
        
        // Check if department can be deleted (must be inactive)
        if (CommonConstants.ACTIVE.equals(department.getStatus())) {
            errors.add(new ValidationException.ValidationError("status", 
                "Cannot delete active department. Deactivate first."));
            return;
        }
        
        // TODO: Users, child departments, and role assignments validation will be implemented when respective services are available
        
        log.debug("Completed department deletion validation for department: {}", department.getName());
    }

    // ========== HELPER VALIDATION METHODS ==========

    private void validateOrganizationForDepartment(String organizationId, List<ValidationException.ValidationError> errors) {
        // Organization ID required validation is handled by @NotBlank annotation in Department entity
        
        // Use service layer to fetch organization and validate it exists and is active
        organizationService.getOrganizationById(organizationId)
                .ifPresentOrElse(
                    organization -> {
                        if (!OrganizationStatusEnum.ACTIVE.equals(organization.getStatus())) {
                            errors.add(new ValidationException.ValidationError("organizationId", 
                                "Cannot create department under inactive organization"));
                        }
                    },
                    () -> errors.add(new ValidationException.ValidationError("organizationId", 
                        "Organization not found with ID: " + organizationId))
                );
    }

    private void validateNoCircularReference(Department department, List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(department.getParentDepartmentId()) || !StringUtils.hasText(department.getId())) {
            return;
        }
        
        // Prevent setting self as parent
        if (department.getId().equals(department.getParentDepartmentId())) {
            errors.add(new ValidationException.ValidationError("parentDepartmentId", 
                "Department cannot be its own parent"));
            return;
        }
        
        // TODO: Full circular reference check will be implemented when department service is available
    }

}