package com.codzs.validation.user;

import com.codzs.entity.user.User;
import com.codzs.exception.type.validation.ValidationException;
import com.codzs.constant.organization.OrganizationStatusEnum;
import com.codzs.framework.constant.CommonConstants;
import com.codzs.service.organization.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Business validator for User-related operations.
 * Handles user business rules, email domain validation, and organization/tenant relationships.
 * Uses service layer to fetch external data for validation.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
@Slf4j
public class UserBusinessValidator {

    private final OrganizationService organizationService;

    @Autowired
    public UserBusinessValidator(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    // ========== ENTRY POINT METHODS FOR USER APIs ==========

    /**
     * Validates user creation for service layer.
     * Entry point for: POST /api/v1/users
     */
    public void validateUserCreation(User user) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateUserCreationFlow(user, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("User creation validation failed", errors);
        }
    }

    /**
     * Validates user update for service layer.
     * Entry point for: PUT /api/v1/users/{id}
     */
    public void validateUserUpdate(User user) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateUserUpdateFlow(user, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("User update validation failed", errors);
        }
    }

    /**
     * Validates user activation for service layer.
     * Entry point for: PUT /api/v1/users/{id}/activate
     */
    public void validateUserActivation(User user) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateUserActivationFlow(user, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("User activation validation failed", errors);
        }
    }

    /**
     * Validates user activation flow and returns skip flag.
     * Returns true if activation should be skipped (idempotent), false otherwise.
     */
    public Boolean validateUserActivationFlow(User user) {
        log.debug("Validating user activation for user: {}", user.getEmail());
        
        // Skip further processing if user is already active (idempotent operation)
        if (CommonConstants.ACTIVE.equals(user.getStatus())) {
            log.debug("User is already active, skipping further processing");
            return CommonConstants.SKIP_FURTHER_STEP;
        }
        
        // Validate organization is active using service layer
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateOrganizationForUser(user.getOrganizationId(), errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("User activation validation failed", errors);
        }
        
        // TODO: Validate tenant is active when tenant service is available
        
        log.debug("Completed user activation validation for user: {}", user.getEmail());
        return !CommonConstants.SKIP_FURTHER_STEP;
    }

    /**
     * Validates user deactivation for service layer.
     * Entry point for: PUT /api/v1/users/{id}/deactivate
     */
    public void validateUserDeactivation(User user) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateUserDeactivationFlow(user, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("User deactivation validation failed", errors);
        }
    }

    /**
     * Validates user deactivation flow and returns skip flag.
     * Returns true if deactivation should be skipped (idempotent), false otherwise.
     */
    public Boolean validateUserDeactivationFlow(User user) {
        log.debug("Validating user deactivation for user: {}", user.getEmail());
        
        // Skip further processing if user is already inactive (idempotent operation)
        if (!CommonConstants.ACTIVE.equals(user.getStatus())) {
            log.debug("User is already inactive, skipping further processing");
            return CommonConstants.SKIP_FURTHER_STEP;
        }
        
        // TODO: Check for active sessions when session service is available
        // TODO: Check for administrative role dependencies when role service is available
        
        log.debug("Completed user deactivation validation for user: {}", user.getEmail());
        return !CommonConstants.SKIP_FURTHER_STEP;
    }

    /**
     * Validates user deletion for service layer.
     * Entry point for: DELETE /api/v1/users/{id}
     */
    public void validateUserDeletion(User user) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateUserDeletionFlow(user, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("User deletion validation failed", errors);
        }
    }

    /**
     * Validates user deletion flow and returns skip flag.
     * Returns true if deletion should be skipped (already deleted), false otherwise.
     */
    public Boolean validateUserDeletionFlow(User user) {
        log.debug("Validating user deletion for user: {}", user.getEmail());
        
        // Skip further processing if user is already deleted (idempotent operation)
        if ("DELETED".equals(user.getStatus())) {
            log.debug("User is already deleted, skipping further processing");
            return CommonConstants.SKIP_FURTHER_STEP;
        }
        
        // Check if user can be deleted (must be inactive)
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        if (CommonConstants.ACTIVE.equals(user.getStatus())) {
            errors.add(new ValidationException.ValidationError("status", 
                "Cannot delete active user. Deactivate first."));
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("User deletion validation failed", errors);
        }
        
        // TODO: Check for data ownership/audit trail requirements
        // TODO: Check for role/permission dependencies when role service is available
        
        log.debug("Completed user deletion validation for user: {}", user.getEmail());
        return !CommonConstants.SKIP_FURTHER_STEP;
    }

    // ========== CORE VALIDATION METHODS ==========

    private void validateUserCreationFlow(User user, List<ValidationException.ValidationError> errors) {
        log.debug("Validating user creation for user: {}", user.getEmail());
        
        // Validate organization exists and is active using service layer
        validateOrganizationForUser(user.getOrganizationId(), errors);
        
        // Validate email domain against organization domains using service layer
        validateEmailDomainForOrganization(user.getEmail(), user.getOrganizationId(), errors);
        
        // TODO: Validate tenant association when tenant service is available
        
        log.debug("Completed user creation validation for user: {}", user.getEmail());
    }

    private void validateUserUpdateFlow(User user, List<ValidationException.ValidationError> errors) {
        log.debug("Validating user update for user: {}", user.getEmail());
        
        // Validate organization exists if changing organization
        if (StringUtils.hasText(user.getOrganizationId())) {
            validateOrganizationForUser(user.getOrganizationId(), errors);
        }
        
        // Validate email domain if email is being changed
        if (StringUtils.hasText(user.getEmail()) && StringUtils.hasText(user.getOrganizationId())) {
            validateEmailDomainForOrganization(user.getEmail(), user.getOrganizationId(), errors);
        }
        
        log.debug("Completed user update validation for user: {}", user.getEmail());
    }

    private void validateUserActivationFlow(User user, List<ValidationException.ValidationError> errors) {
        log.debug("Validating user activation for user: {}", user.getEmail());
        
        // Skip validation if user is already active (idempotent operation)
        if (CommonConstants.ACTIVE.equals(user.getStatus())) {
            log.debug("User is already active, skipping validation for idempotent operation");
            return;
        }
        
        // Validate organization is active using service layer
        validateOrganizationForUser(user.getOrganizationId(), errors);
        
        // TODO: Validate tenant is active when tenant service is available
        
        log.debug("Completed user activation validation for user: {}", user.getEmail());
    }

    private void validateUserDeactivationFlow(User user, List<ValidationException.ValidationError> errors) {
        log.debug("Validating user deactivation for user: {}", user.getEmail());
        
        // Skip validation if user is already inactive (idempotent operation)
        if (!CommonConstants.ACTIVE.equals(user.getStatus())) {
            log.debug("User is already inactive, skipping validation for idempotent operation");
            return;
        }
        
        // TODO: Check for active sessions when session service is available
        // TODO: Check for administrative role dependencies when role service is available
        
        log.debug("Completed user deactivation validation for user: {}", user.getEmail());
    }

    private void validateUserDeletionFlow(User user, List<ValidationException.ValidationError> errors) {
        log.debug("Validating user deletion for user: {}", user.getEmail());
        
        // Check if user can be deleted (must be inactive)
        if (CommonConstants.ACTIVE.equals(user.getStatus())) {
            errors.add(new ValidationException.ValidationError("status", 
                "Cannot delete active user. Deactivate first."));
            return;
        }
        
        // TODO: Check for data ownership/audit trail requirements
        // TODO: Check for role/permission dependencies when role service is available
        
        log.debug("Completed user deletion validation for user: {}", user.getEmail());
    }

    // ========== HELPER VALIDATION METHODS ==========

    private void validateOrganizationForUser(String organizationId, List<ValidationException.ValidationError> errors) {
        // Organization ID required validation is handled by @NotBlank annotation in User entity
        
        // Use service layer to fetch organization and validate it exists and is active
        organizationService.getOrganizationById(organizationId)
                .ifPresentOrElse(
                    organization -> {
                        if (!OrganizationStatusEnum.ACTIVE.equals(organization.getStatus())) {
                            errors.add(new ValidationException.ValidationError("organizationId", 
                                "Cannot create user under inactive organization"));
                        }
                    },
                    () -> errors.add(new ValidationException.ValidationError("organizationId", 
                        "Organization not found with ID: " + organizationId))
                );
    }

    private void validateEmailDomainForOrganization(String email, String organizationId, 
                                                   List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(organizationId)) {
            return;
        }
        
        // Extract domain from email (email format validation is handled by entity annotations)
        String emailDomain = extractDomainFromEmail(email);
        if (emailDomain == null) {
            return; // Invalid email format - handled by entity annotations
        }
        
        // Use service layer to fetch organization and check if email domain is registered
        organizationService.getOrganizationById(organizationId)
                .ifPresent(organization -> {
                    // Check if email domain matches any registered organization domains
                    boolean domainMatches = organization.getDomains() != null && 
                        organization.getDomains().stream()
                            .anyMatch(domain -> domain.getName().equalsIgnoreCase(emailDomain) && domain.getIsVerified());
                    
                    if (!domainMatches) {
                        errors.add(new ValidationException.ValidationError("email", 
                            "Email domain '" + emailDomain + "' is not registered or verified for this organization"));
                    }
                });
    }


    private String extractDomainFromEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return null;
        }
        
        int atIndex = email.lastIndexOf('@');
        if (atIndex == -1 || atIndex == email.length() - 1) {
            return null;
        }
        
        return email.substring(atIndex + 1).toLowerCase();
    }
}