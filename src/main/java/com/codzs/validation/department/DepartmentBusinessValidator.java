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
            validateDepartmentHierarchy(department, errors);
        }
        
        // Validate department business rules
        validateDepartmentBusinessRules(department, errors);
        
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
            validateDepartmentHierarchy(department, errors);
            validateNoCircularReference(department, errors);
        }
        
        // Validate department business rules
        validateDepartmentBusinessRules(department, errors);
        
        log.debug("Completed department update validation for department: {}", department.getName());
    }

    private void validateDepartmentActivationFlow(Department department, List<ValidationException.ValidationError> errors) {
        log.debug("Validating department activation for department: {}", department.getName());
        
        // Check if department is already active
        if (CommonConstants.ACTIVE.equals(department.getStatus())) {
            errors.add(new ValidationException.ValidationError("status", "Department is already active"));
            return;
        }
        
        // Validate organization is active using service layer
        validateOrganizationForDepartment(department.getOrganizationId(), errors);
        
        // TODO: Validate tenant is active when tenant service is available
        // TODO: Validate parent department is active when department service is available
        
        log.debug("Completed department activation validation for department: {}", department.getName());
    }

    private void validateDepartmentDeactivationFlow(Department department, List<ValidationException.ValidationError> errors) {
        log.debug("Validating department deactivation for department: {}", department.getName());
        
        // Check if department is already inactive
        if ("INACTIVE".equals(department.getStatus())) {
            errors.add(new ValidationException.ValidationError("status", "Department is already inactive"));
            return;
        }
        
        // TODO: Check for active users in department when user service is available
        // TODO: Check for active child departments when department service is available
        
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
        
        // TODO: Check for users in department when user service is available
        // TODO: Check for child departments when department service is available
        // TODO: Check for role assignments when role service is available
        
        log.debug("Completed department deletion validation for department: {}", department.getName());
    }

    // ========== HELPER VALIDATION METHODS ==========

    private void validateOrganizationForDepartment(String organizationId, List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(organizationId)) {
            errors.add(new ValidationException.ValidationError("organizationId", "Organization ID is required"));
            return;
        }
        
        // Use service layer to fetch organization and validate it exists and is active
        Organization organization = organizationService.findById(organizationId);
        if (organization == null) {
            errors.add(new ValidationException.ValidationError("organizationId", 
                "Organization not found with ID: " + organizationId));
            return;
        }
        
        if (!OrganizationStatusEnum.ACTIVE.equals(organization.getStatus())) {
            errors.add(new ValidationException.ValidationError("organizationId", 
                "Cannot create department under inactive organization"));
        }
    }

    private void validateDepartmentHierarchy(Department department, List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(department.getParentDepartmentId())) {
            return;
        }
        
        // TODO: Implement hierarchy depth validation when department service is available
        // This would check that the hierarchy depth doesn't exceed MAX_HIERARCHY_DEPTH
        // For now, we skip this validation
        log.debug("Department hierarchy validation not implemented yet for department: {}", department.getName());
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
        
        // TODO: Implement full circular reference check when department service is available
        // This would traverse the parent chain to ensure no circular references
        log.debug("Circular reference validation not fully implemented yet for department: {}", department.getName());
    }

    private void validateDepartmentBusinessRules(Department department, List<ValidationException.ValidationError> errors) {
        // Validate department name format and length
        if (!StringUtils.hasText(department.getName()) || department.getName().trim().length() < 2) {
            errors.add(new ValidationException.ValidationError("name", 
                "Department name must be at least 2 characters long"));
        }
        
        if (StringUtils.hasText(department.getName()) && department.getName().length() > 100) {
            errors.add(new ValidationException.ValidationError("name", 
                "Department name cannot exceed 100 characters"));
        }
        
        // Validate department code format if provided
        if (StringUtils.hasText(department.getCode())) {
            if (!department.getCode().matches("^[A-Z0-9_]+$")) {
                errors.add(new ValidationException.ValidationError("code", 
                    "Department code must contain only uppercase letters, numbers, and underscores"));
            }
            
            if (department.getCode().length() > 20) {
                errors.add(new ValidationException.ValidationError("code", 
                    "Department code cannot exceed 20 characters"));
            }
        }
        
        // Validate description length if provided
        if (StringUtils.hasText(department.getDescription()) && department.getDescription().length() > 500) {
            errors.add(new ValidationException.ValidationError("description", 
                "Department description cannot exceed 500 characters"));
        }
        
        // Validate cost center format if provided
        if (StringUtils.hasText(department.getCostCenter())) {
            if (!department.getCostCenter().matches("^[A-Z0-9-]+$")) {
                errors.add(new ValidationException.ValidationError("costCenter", 
                    "Cost center must contain only uppercase letters, numbers, and hyphens"));
            }
            
            if (department.getCostCenter().length() > 20) {
                errors.add(new ValidationException.ValidationError("costCenter", 
                    "Cost center cannot exceed 20 characters"));
            }
        }
        
        // Validate max users if specified
        if (department.getMaxUsers() != null && department.getMaxUsers() < 1) {
            errors.add(new ValidationException.ValidationError("maxUsers", 
                "Max users must be at least 1"));
        }
        
        // TODO: Add uniqueness validation for department name/code within organization/tenant
        // This would require department repository access through service layer
    }
}