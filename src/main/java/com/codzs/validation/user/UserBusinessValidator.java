package com.codzs.validation.user;

import com.codzs.entity.user.User;
import com.codzs.constant.organization.OrganizationStatusEnum;
import com.codzs.constant.user.UserConstants;
import com.codzs.entity.organization.Organization;
import com.codzs.exception.validation.ValidationException;
import com.codzs.service.organization.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

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

    // ========== CORE VALIDATION METHODS ==========

    private void validateUserCreationFlow(User user, List<ValidationException.ValidationError> errors) {
        log.debug("Validating user creation for user: {}", user.getEmail());
        
        // Validate organization exists and is active using service layer
        validateOrganizationForUser(user.getOrganizationId(), errors);
        
        // Validate email domain against organization domains using service layer
        validateEmailDomainForOrganization(user.getEmail(), user.getOrganizationId(), errors);
        
        // Validate user business rules
        validateUserBusinessRules(user, errors);
        
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
        
        // Validate user business rules
        validateUserBusinessRules(user, errors);
        
        log.debug("Completed user update validation for user: {}", user.getEmail());
    }

    private void validateUserActivationFlow(User user, List<ValidationException.ValidationError> errors) {
        log.debug("Validating user activation for user: {}", user.getEmail());
        
        // Check if user is already active
        if (UserConstants.ACTIVE.equals(user.getStatus())) {
            errors.add(new ValidationException.ValidationError("status", "User is already active"));
            return;
        }
        
        // Validate organization is active using service layer
        validateOrganizationForUser(user.getOrganizationId(), errors);
        
        // TODO: Validate tenant is active when tenant service is available
        
        log.debug("Completed user activation validation for user: {}", user.getEmail());
    }

    private void validateUserDeactivationFlow(User user, List<ValidationException.ValidationError> errors) {
        log.debug("Validating user deactivation for user: {}", user.getEmail());
        
        // Check if user is already inactive
        if (!UserConstants.ACTIVE.equals(user.getStatus())) {
            errors.add(new ValidationException.ValidationError("status", "User is already inactive"));
            return;
        }
        
        // TODO: Check for active sessions when session service is available
        // TODO: Check for administrative role dependencies when role service is available
        
        log.debug("Completed user deactivation validation for user: {}", user.getEmail());
    }

    private void validateUserDeletionFlow(User user, List<ValidationException.ValidationError> errors) {
        log.debug("Validating user deletion for user: {}", user.getEmail());
        
        // Check if user can be deleted (must be inactive)
        if (UserConstants.ACTIVE.equals(user.getStatus())) {
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
                "Cannot create user under inactive organization"));
        }
    }

    private void validateEmailDomainForOrganization(String email, String organizationId, 
                                                   List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(organizationId)) {
            return;
        }
        
        // Extract domain from email
        String emailDomain = extractDomainFromEmail(email);
        if (emailDomain == null) {
            errors.add(new ValidationException.ValidationError("email", "Invalid email format"));
            return;
        }
        
        // Use service layer to fetch organization and check if email domain is registered
        Organization organization = organizationService.findById(organizationId);
        if (organization == null) {
            return; // Organization validation will handle this
        }
        
        // Check if email domain matches any registered organization domains
        boolean domainMatches = organization.getDomains() != null && 
            organization.getDomains().stream()
                .anyMatch(domain -> domain.getName().equalsIgnoreCase(emailDomain) && domain.getIsVerified());
        
        if (!domainMatches) {
            errors.add(new ValidationException.ValidationError("email", 
                "Email domain '" + emailDomain + "' is not registered or verified for this organization"));
        }
    }

    private void validateUserBusinessRules(User user, List<ValidationException.ValidationError> errors) {
        // Validate email format
        if (StringUtils.hasText(user.getEmail()) && !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            errors.add(new ValidationException.ValidationError("email", "Invalid email format"));
        }
        
        // Validate first name
        if (!StringUtils.hasText(user.getFirstName()) || user.getFirstName().trim().length() < 1) {
            errors.add(new ValidationException.ValidationError("firstName", "First name is required"));
        }
        
        if (StringUtils.hasText(user.getFirstName()) && user.getFirstName().length() > 50) {
            errors.add(new ValidationException.ValidationError("firstName", 
                "First name cannot exceed 50 characters"));
        }
        
        // Validate last name
        if (!StringUtils.hasText(user.getLastName()) || user.getLastName().trim().length() < 1) {
            errors.add(new ValidationException.ValidationError("lastName", "Last name is required"));
        }
        
        if (StringUtils.hasText(user.getLastName()) && user.getLastName().length() > 50) {
            errors.add(new ValidationException.ValidationError("lastName", 
                "Last name cannot exceed 50 characters"));
        }
        
        // Validate phone number format if provided
        if (StringUtils.hasText(user.getPhoneNumber())) {
            if (!user.getPhoneNumber().matches("^\\+?[1-9]\\d{1,14}$")) {
                errors.add(new ValidationException.ValidationError("phoneNumber", 
                    "Invalid phone number format"));
            }
        }
        
        // TODO: Add uniqueness validation for email within organization/tenant
        // This would require user repository access through service layer
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