package com.codzs.validation.tenant;

import com.codzs.entity.tenant.Tenant;
import com.codzs.entity.organization.Organization;
import com.codzs.exception.validation.ValidationException;
import com.codzs.framework.constant.CommonConstants;
import com.codzs.service.organization.OrganizationService;
import com.codzs.service.subscription.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Business validator for Tenant-related operations.
 * Handles tenant business rules, organization relationships, and capacity constraints.
 * Uses service layer to fetch external data for validation.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
@Slf4j
public class TenantBusinessValidator {

    private final OrganizationService organizationService;
    private final SubscriptionService subscriptionService;

    @Autowired
    public TenantBusinessValidator(OrganizationService organizationService,
                                 SubscriptionService subscriptionService) {
        this.organizationService = organizationService;
        this.subscriptionService = subscriptionService;
    }

    // ========== ENTRY POINT METHODS FOR TENANT APIs ==========

    /**
     * Validates tenant creation for service layer.
     * Entry point for: POST /api/v1/tenants
     */
    public void validateTenantCreation(Tenant tenant) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateTenantCreationFlow(tenant, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Tenant creation validation failed", errors);
        }
    }

    /**
     * Validates tenant update for service layer.
     * Entry point for: PUT /api/v1/tenants/{id}
     */
    public void validateTenantUpdate(Tenant tenant) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateTenantUpdateFlow(tenant, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Tenant update validation failed", errors);
        }
    }

    /**
     * Validates tenant activation for service layer.
     * Entry point for: PUT /api/v1/tenants/{id}/activate
     */
    public void validateTenantActivation(Tenant tenant) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateTenantActivationFlow(tenant, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Tenant activation validation failed", errors);
        }
    }

    /**
     * Validates tenant deactivation for service layer.
     * Entry point for: PUT /api/v1/tenants/{id}/deactivate
     */
    public void validateTenantDeactivation(Tenant tenant) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateTenantDeactivationFlow(tenant, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Tenant deactivation validation failed", errors);
        }
    }

    /**
     * Validates tenant deletion for service layer.
     * Entry point for: DELETE /api/v1/tenants/{id}
     */
    public void validateTenantDeletion(Tenant tenant) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateTenantDeletionFlow(tenant, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Tenant deletion validation failed", errors);
        }
    }

    // ========== CORE VALIDATION METHODS ==========

    private void validateTenantCreationFlow(Tenant tenant, List<ValidationException.ValidationError> errors) {
        log.debug("Validating tenant creation for tenant: {}", tenant.getName());
        
        // Validate organization exists and is active using service layer
        validateOrganizationForTenant(tenant.getOrganizationId(), errors);
        
        // Validate tenant capacity limits using service layer
        validateTenantCapacityLimits(tenant.getOrganizationId(), errors);
        
        // Validate tenant business rules
        validateTenantBusinessRules(tenant, errors);
        
        log.debug("Completed tenant creation validation for tenant: {}", tenant.getName());
    }

    private void validateTenantUpdateFlow(Tenant tenant, List<ValidationException.ValidationError> errors) {
        log.debug("Validating tenant update for tenant: {}", tenant.getName());
        
        // Validate organization exists if changing organization
        if (StringUtils.hasText(tenant.getOrganizationId())) {
            validateOrganizationForTenant(tenant.getOrganizationId(), errors);
        }
        
        // Validate tenant business rules
        validateTenantBusinessRules(tenant, errors);
        
        log.debug("Completed tenant update validation for tenant: {}", tenant.getName());
    }

    private void validateTenantActivationFlow(Tenant tenant, List<ValidationException.ValidationError> errors) {
        log.debug("Validating tenant activation for tenant: {}", tenant.getName());
        
        // Check if tenant is already active
        if (CommonConstants.ACTIVE.equals(tenant.getStatus())) { 
            errors.add(new ValidationException.ValidationError("status", "Tenant is already active"));
            return;
        }
        
        // Validate organization is active using service layer
        validateOrganizationForTenant(tenant.getOrganizationId(), errors);
        
        log.debug("Completed tenant activation validation for tenant: {}", tenant.getName());
    }

    private void validateTenantDeactivationFlow(Tenant tenant, List<ValidationException.ValidationError> errors) {
        log.debug("Validating tenant deactivation for tenant: {}", tenant.getName());
        
        // Check if tenant is already inactive
        if (!CommonConstants.ACTIVE.equals(tenant.getStatus())) {
            errors.add(new ValidationException.ValidationError("status", "Tenant is already inactive"));
            return;
        }
        
        // Check for active subscriptions using service layer
        if (subscriptionService.hasActiveSubscriptionsForTenant(tenant.getId())) {
            errors.add(new ValidationException.ValidationError("tenantId", 
                "Cannot deactivate tenant with active subscriptions"));
        }
        
        log.debug("Completed tenant deactivation validation for tenant: {}", tenant.getName());
    }

    private void validateTenantDeletionFlow(Tenant tenant, List<ValidationException.ValidationError> errors) {
        log.debug("Validating tenant deletion for tenant: {}", tenant.getName());
        
        // Check if tenant can be deleted (must be inactive)
        if (CommonConstants.ACTIVE.equals(tenant.getStatus())) {
            errors.add(new ValidationException.ValidationError("status", 
                "Cannot delete active tenant. Deactivate first."));
            return;
        }
        
        // Check for active subscriptions using service layer
        if (subscriptionService.hasActiveSubscriptionsForTenant(tenant.getId())) {
            errors.add(new ValidationException.ValidationError("tenantId", 
                "Cannot delete tenant with active subscriptions"));
        }
        
        // TODO: Add validation for active users in tenant when user service is available
        // TODO: Add validation for active departments in tenant when department service is available
        
        log.debug("Completed tenant deletion validation for tenant: {}", tenant.getName());
    }

    // ========== HELPER VALIDATION METHODS ==========

    private void validateOrganizationForTenant(String organizationId, List<ValidationException.ValidationError> errors) {
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
        
        if (!"ACTIVE".equals(organization.getStatus())) {
            errors.add(new ValidationException.ValidationError("organizationId", 
                "Cannot create tenant under inactive organization"));
        }
    }

    private void validateTenantCapacityLimits(String organizationId, List<ValidationException.ValidationError> errors) {
        // TODO: Implement tenant capacity validation when tenant service/repository is available
        // This would check current tenant count against organization limits
        log.debug("Tenant capacity validation not implemented yet for organization: {}", organizationId);
    }

    private void validateTenantBusinessRules(Tenant tenant, List<ValidationException.ValidationError> errors) {
        // Validate tenant name format and length
        if (!StringUtils.hasText(tenant.getName()) || tenant.getName().trim().length() < 2) {
            errors.add(new ValidationException.ValidationError("name", 
                "Tenant name must be at least 2 characters long"));
        }
        
        if (StringUtils.hasText(tenant.getName()) && tenant.getName().length() > 100) {
            errors.add(new ValidationException.ValidationError("name", 
                "Tenant name cannot exceed 100 characters"));
        }
        
        // Validate tenant code format if provided
        if (StringUtils.hasText(tenant.getCode())) {
            if (!tenant.getCode().matches("^[A-Z0-9_]+$")) {
                errors.add(new ValidationException.ValidationError("code", 
                    "Tenant code must contain only uppercase letters, numbers, and underscores"));
            }
            
            if (tenant.getCode().length() > 20) {
                errors.add(new ValidationException.ValidationError("code", 
                    "Tenant code cannot exceed 20 characters"));
            }
        }
        
        // Validate description length if provided
        if (StringUtils.hasText(tenant.getDescription()) && tenant.getDescription().length() > 500) {
            errors.add(new ValidationException.ValidationError("description", 
                "Tenant description cannot exceed 500 characters"));
        }
        
        // TODO: Add uniqueness validation for tenant name/code within organization
        // This would require tenant repository access through service layer
    }
}