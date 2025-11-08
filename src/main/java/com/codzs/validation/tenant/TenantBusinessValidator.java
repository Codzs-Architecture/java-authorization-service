package com.codzs.validation.tenant;

import com.codzs.entity.tenant.Tenant;
import com.codzs.entity.organization.Organization;
import com.codzs.framework.constant.CommonConstants;
import com.codzs.framework.exception.type.ValidationException;
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
     * Validates tenant activation flow and returns skip flag.
     * Returns true if activation should be skipped (idempotent), false otherwise.
     */
    public Boolean validateTenantActivationFlow(Tenant tenant) {
        log.debug("Validating tenant activation for tenant: {}", tenant.getName());
        
        // Skip further processing if tenant is already active (idempotent operation)
        if (CommonConstants.ACTIVE.equals(tenant.getStatus())) { 
            log.debug("Tenant is already active, skipping further processing");
            return CommonConstants.SKIP_FURTHER_STEP;
        }
        
        // Validate organization is active using service layer
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateOrganizationForTenant(tenant.getOrganizationId(), errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Tenant activation validation failed", errors);
        }
        
        log.debug("Completed tenant activation validation for tenant: {}", tenant.getName());
        return !CommonConstants.SKIP_FURTHER_STEP;
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
     * Validates tenant deactivation flow and returns skip flag.
     * Returns true if deactivation should be skipped (idempotent), false otherwise.
     */
    public Boolean validateTenantDeactivationFlow(Tenant tenant) {
        log.debug("Validating tenant deactivation for tenant: {}", tenant.getName());
        
        // Skip further processing if tenant is already inactive (idempotent operation)
        if (!CommonConstants.ACTIVE.equals(tenant.getStatus())) {
            log.debug("Tenant is already inactive, skipping further processing");
            return CommonConstants.SKIP_FURTHER_STEP;
        }
        
        // Check for active subscriptions using service layer
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        if (subscriptionService.hasActiveSubscriptionsForTenant(tenant.getId())) {
            errors.add(new ValidationException.ValidationError("tenantId", 
                "Cannot deactivate tenant with active subscriptions"));
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Tenant deactivation validation failed", errors);
        }
        
        log.debug("Completed tenant deactivation validation for tenant: {}", tenant.getName());
        return !CommonConstants.SKIP_FURTHER_STEP;
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

    /**
     * Validates tenant deletion flow and returns skip flag.
     * Returns true if deletion should be skipped (already deleted), false otherwise.
     */
    public Boolean validateTenantDeletionFlow(Tenant tenant) {
        log.debug("Validating tenant deletion for tenant: {}", tenant.getName());
        
        // Skip further processing if tenant is already deleted (idempotent operation)
        if ("DELETED".equals(tenant.getStatus())) {
            log.debug("Tenant is already deleted, skipping further processing");
            return CommonConstants.SKIP_FURTHER_STEP;
        }
        
        // Check if tenant can be deleted (must be inactive)
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        if (CommonConstants.ACTIVE.equals(tenant.getStatus())) {
            errors.add(new ValidationException.ValidationError("status", 
                "Cannot delete active tenant. Deactivate first."));
        }
        
        // Check for active subscriptions using service layer
        if (subscriptionService.hasActiveSubscriptionsForTenant(tenant.getId())) {
            errors.add(new ValidationException.ValidationError("tenantId", 
                "Cannot delete tenant with active subscriptions"));
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Tenant deletion validation failed", errors);
        }
        
        // Active users and departments validation will be implemented when respective services are available
        
        log.debug("Completed tenant deletion validation for tenant: {}", tenant.getName());
        return !CommonConstants.SKIP_FURTHER_STEP;
    }

    // ========== CORE VALIDATION METHODS ==========

    private void validateTenantCreationFlow(Tenant tenant, List<ValidationException.ValidationError> errors) {
        log.debug("Validating tenant creation for tenant: {}", tenant.getName());
        
        // Validate organization exists and is active using service layer
        validateOrganizationForTenant(tenant.getOrganizationId(), errors);
        
        // TODO: Tenant capacity limits validation will be implemented when tenant repository is available
        
        // TODO: Tenant business rules validation is handled by entity annotations
        
        log.debug("Completed tenant creation validation for tenant: {}", tenant.getName());
    }

    private void validateTenantUpdateFlow(Tenant tenant, List<ValidationException.ValidationError> errors) {
        log.debug("Validating tenant update for tenant: {}", tenant.getName());
        
        // Validate organization exists if changing organization
        if (StringUtils.hasText(tenant.getOrganizationId())) {
            validateOrganizationForTenant(tenant.getOrganizationId(), errors);
        }
        
        // TODO: Tenant business rules validation is handled by entity annotations
        
        log.debug("Completed tenant update validation for tenant: {}", tenant.getName());
    }

    private void validateTenantActivationFlow(Tenant tenant, List<ValidationException.ValidationError> errors) {
        log.debug("Validating tenant activation for tenant: {}", tenant.getName());
        
        // Skip validation if tenant is already active (idempotent operation)
        if (CommonConstants.ACTIVE.equals(tenant.getStatus())) { 
            log.debug("Tenant is already active, skipping validation for idempotent operation");
            return;
        }
        
        // Validate organization is active using service layer
        validateOrganizationForTenant(tenant.getOrganizationId(), errors);
        
        log.debug("Completed tenant activation validation for tenant: {}", tenant.getName());
    }

    private void validateTenantDeactivationFlow(Tenant tenant, List<ValidationException.ValidationError> errors) {
        log.debug("Validating tenant deactivation for tenant: {}", tenant.getName());
        
        // Skip validation if tenant is already inactive (idempotent operation)
        if (!CommonConstants.ACTIVE.equals(tenant.getStatus())) {
            log.debug("Tenant is already inactive, skipping validation for idempotent operation");
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
        
        // Active users and departments validation will be implemented when respective services are available
        
        log.debug("Completed tenant deletion validation for tenant: {}", tenant.getName());
    }

    // ========== HELPER VALIDATION METHODS ==========

    private void validateOrganizationForTenant(String organizationId, List<ValidationException.ValidationError> errors) {
        // Organization ID required validation is handled by @NotBlank annotation in Tenant entity
        
        // Use service layer to fetch organization and validate it exists and is active
        organizationService.getOrganizationById(organizationId)
                .ifPresentOrElse(
                    organization -> {
                        if (!CommonConstants.ACTIVE.toString().equals(organization.getStatus().toString())) {
                            errors.add(new ValidationException.ValidationError("organizationId", 
                                "Cannot create tenant under inactive organization"));
                        }
                    },
                    () -> errors.add(new ValidationException.ValidationError("organizationId", 
                        "Organization not found with ID: " + organizationId))
                );
    }


}