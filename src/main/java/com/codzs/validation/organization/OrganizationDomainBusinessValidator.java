package com.codzs.validation.organization;

import com.codzs.entity.domain.Domain;
import com.codzs.entity.organization.Organization;
import com.codzs.exception.validation.ValidationException;
// import com.codzs.validation.domain.DomainBusinessValidator;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;

/**
 * Business validator for embedded Domain operations within organizations.
 * Focuses on domain business rules, verification logic, and organizational constraints.
 * Works with domains as embedded sub-objects rather than separate entities.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
public class OrganizationDomainBusinessValidator {

    public OrganizationDomainBusinessValidator() {
    }

    // ========== ENTRY POINT METHODS FOR ORGANIZATION APIs ==========

    /**
     * Validates domain addition for service layer.
     * Simplified validation for OrganizationDomainService.
     */
    public void validateDomainAddition(Organization organization, Domain domain, int maxCount, boolean isDomainAlreadyRegistered) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateDomainAdditionForEntity(organization.getDomains(), domain, maxCount, isDomainAlreadyRegistered, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Domain addition validation failed", errors);
        }
    }

    /**
     * Validates domain removal request.
     * Entry point for: DELETE /api/v1/organizations/{id}/domains/{domainId}
     */
    public void validateDomainRemoval(Organization organization, String domainId, boolean hasUsersInDomain, int userCountInDomain) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();

        Domain domain = findDomainInEntity(organization.getDomains(), domainId, errors);
        if (domain == null) {
            return;
        }

        validateDomainRemovalRules(organization, domain, hasUsersInDomain, userCountInDomain, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Domain removal validation failed", errors);
        }
    }

    /**
     * Validates domain update request.
     * Entry point for: PUT /api/v1/organizations/{id}/domains/{domainId}
     */
    public void validateDomainUpdate(Domain existingDomain, Domain updatedDomain, boolean isDomainAlreadyRegistered) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        // Check if domain name is being changed to an existing one
        if (!existingDomain.getName().equals(updatedDomain.getName())) {
            validateDomainGlobalUniqueness(updatedDomain.getName(), "", isDomainAlreadyRegistered, errors);
        }
        
        validateDomainNameFormat(updatedDomain.getName(), "", errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Domain update validation failed", errors);
        }
    }

    /**
     * Validates domain verification request.
     * Entry point for: PUT /api/v1/organizations/{id}/domains/{domainId}/verify
     */
    public void validateDomainVerificationRequest(Domain domain, String verificationMethod, String verificationToken, boolean isValidVerificationToken, boolean isVerificationExpired) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        validateDomainVerificationRules(domain, verificationMethod, isVerificationExpired, errors);
        
        // Additional validation for verification token
        if (!isValidVerificationToken) {
            errors.add(new ValidationException.ValidationError("verificationToken", "Invalid verification token"));
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Domain verification validation failed", errors);
        }
    }

    /**
     * Validates setting primary domain request.
     * Entry point for: PUT /api/v1/organizations/{id}/domains/{domainId}/primary
     */
    public void validateSetPrimaryDomain(Domain domain) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        if (!domain.getIsVerified()) {
            errors.add(new ValidationException.ValidationError("domainId", 
                "Only verified domains can be set as primary"));
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Set primary domain validation failed", errors);
        }
    }

    // ========== CORE VALIDATION METHODS ==========

    private void validateDomainRemovalRules(Organization organization, Domain domain, 
                                          boolean hasUsersInDomain, int userCountInDomain,
                                          List<ValidationException.ValidationError> errors) {
        if (domain.getIsPrimary() && organization.getDomains() != null && organization.getDomains().size() == 1) {
            errors.add(new ValidationException.ValidationError("domainId", 
                "Cannot remove the only domain from organization"));
            return;
        }

        if (domain.getIsPrimary() && organization.getDomains() != null && organization.getDomains().size() > 1) {
            boolean hasOtherPrimary = organization.getDomains().stream()
                .anyMatch(d -> !d.getId().equals(domain.getId()) && d.getIsPrimary());
            
            if (!hasOtherPrimary) {
                errors.add(new ValidationException.ValidationError("domainId", 
                    "Cannot remove primary domain. Set another domain as primary first."));
            }
        }

        // Check for domain usage by active users using data passed from service layer
        if (hasUsersInDomain) {
            errors.add(new ValidationException.ValidationError("domainId", 
                "Cannot remove domain. " + userCountInDomain + " active users are using this domain."));
        }
    }

    // ========== SUPPORTING VALIDATION METHODS ==========

    private void validateDomainAdditionForEntity(List<Domain> domains, Domain domain, int maxCount, boolean isDomainAlreadyRegistered,
                                                     List<ValidationException.ValidationError> errors) {
        validateDomainCountLimit(domains, maxCount, errors);
        validateDomainForCreation(domain, -1, isDomainAlreadyRegistered, errors);
        validatePrimaryDomainConstraint(domains, domain, errors);
    }

    private void validateDomainCountLimit(List<Domain> domains, int maxCount, List<ValidationException.ValidationError> errors) {
        int currentDomainCount = domains != null ? domains.size() : 0;

        if (currentDomainCount > maxCount) {
            errors.add(new ValidationException.ValidationError("domains", 
                "Cannot specify more than " + maxCount + " domains"));
        }
    }

    private void validateDomainForCreation(Domain domain, int index, boolean isDomainAlreadyRegistered,
                                         List<ValidationException.ValidationError> errors) {
        String fieldPrefix = index >= 0 ? "domains[" + index + "]" : "";
        
        validateDomainNameFormat(domain.getName(), fieldPrefix, errors);
        validateDomainGlobalUniqueness(domain.getName(), fieldPrefix, isDomainAlreadyRegistered, errors);
    }

    private void validatePrimaryDomainConstraint(List<Domain> domains, Domain domain, 
                                               List<ValidationException.ValidationError> errors) {
        if (domain.getIsPrimary() != null && domain.getIsPrimary()) {
            boolean hasExistingPrimary = domains != null && 
                domains.stream().anyMatch(Domain::getIsPrimary);
            
            if (hasExistingPrimary) {
                errors.add(new ValidationException.ValidationError("isPrimary", 
                    "We already has a primary domain. Only one primary domain is allowed."));
            }
        }
    }

    private void validateDomainNameFormat(String domainName, String fieldPrefix, 
                                        List<ValidationException.ValidationError> errors) {
        if (!org.springframework.util.StringUtils.hasText(domainName)) {
            return;
        }

        validateReservedDomains(domainName, fieldPrefix, errors);
    }

    private void validateReservedDomains(String domainName, String fieldPrefix, 
                                       List<ValidationException.ValidationError> errors) {
        String lowerDomain = domainName.toLowerCase();
        
        if (lowerDomain.contains("localhost") || lowerDomain.contains("127.0.0.1") || 
            lowerDomain.contains("0.0.0.0") || lowerDomain.endsWith(".local") ||
            lowerDomain.contains("codzs.com") || lowerDomain.startsWith("api.") || 
            lowerDomain.startsWith("admin.")) {
            errors.add(new ValidationException.ValidationError(
                org.springframework.util.StringUtils.hasText(fieldPrefix) ? fieldPrefix + ".name" : "name", 
                "Cannot use reserved or platform domain names"));
        }
    }

    private void validateDomainGlobalUniqueness(String domainName, String fieldPrefix, boolean isDomainAlreadyRegistered,
                                              List<ValidationException.ValidationError> errors) {
        if (org.springframework.util.StringUtils.hasText(domainName) && isDomainAlreadyRegistered) {
            errors.add(new ValidationException.ValidationError(
                org.springframework.util.StringUtils.hasText(fieldPrefix) ? fieldPrefix + ".name" : "name", 
                "Domain is already registered"));
        }
    }

    private void validateDomainVerificationRules(Domain domain, String verificationMethod, boolean isVerificationExpired,
                                                List<ValidationException.ValidationError> errors) {
        if (domain.getIsVerified()) {
            errors.add(new ValidationException.ValidationError("domainId", "Domain is already verified"));
            return;
        }

        if (!domain.getVerificationMethod().equals(verificationMethod)) {
            errors.add(new ValidationException.ValidationError("verificationMethod", 
                "Verification method does not match domain's configured method"));
            return;
        }

        if (isVerificationExpired) {
            errors.add(new ValidationException.ValidationError("domainId", 
                "Domain verification token has expired"));
            return;
        }
    }

    private Domain findDomainInEntity(List<Domain> domains, String domainId, 
                                          List<ValidationException.ValidationError> errors) {
        if (domains == null) {
            errors.add(new ValidationException.ValidationError("domainId", "Domain not found"));
            return null;
        }

        return domains.stream()
            .filter(domain -> domainId.equals(domain.getId()))
            .findFirst()
            .orElseGet(() -> {
                errors.add(new ValidationException.ValidationError("domainId", "Domain not found"));
                return null;
            });
    }

}