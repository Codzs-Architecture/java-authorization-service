package com.codzs.validation.domain;

import com.codzs.constant.domain.DomainVerificationMethodEnum;
import com.codzs.constant.organization.OrganizationConstants;
import com.codzs.entity.domain.Domain;
import com.codzs.entity.organization.Organization;
import com.codzs.exception.validation.ValidationException;
import com.codzs.service.organization.OrganizationService;
import com.codzs.service.domain.DomainService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
public class DomainBusinessValidator {

    private final OrganizationService organizationService;
    private final DomainService domainService;
    private final DomainVerificationMethodEnum domainVerificationMethodEnum;

    @Autowired
    public DomainBusinessValidator(OrganizationService organizationService,
                                  DomainService domainService,
                                  DomainVerificationMethodEnum domainVerificationMethodEnum) {
        this.organizationService = organizationService;
        this.domainService = domainService;
        this.domainVerificationMethodEnum = domainVerificationMethodEnum;
    }

    // ========== ENTRY POINT METHODS FOR ORGANIZATION APIs ==========

    /**
     * Validates domain addition for service layer.
     * Simplified validation for OrganizationDomainService.
     */
    public void validateDomainAddition(Organization organization, Domain domain) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateDomainAdditionForOrganization(organization, domain, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Domain addition validation failed", errors);
        }
    }

    /**
     * Validates domains for organization creation.
     * Entry point for: POST /api/v1/organizations
     */
    public void validateDomainsForOrganizationCreation(List<Domain> domains, 
                                                      List<ValidationException.ValidationError> errors) {
        if (domains == null || domains.isEmpty()) {
            return;
        }

        validateDomainCountLimit(domains, errors);
        validatePrimaryDomainUniqueness(domains, errors);
        validateDomainDuplicates(domains, errors);
        
        for (int i = 0; i < domains.size(); i++) {
            validateDomainForCreation(domains.get(i), i, errors);
        }
    }

    /**
     * Validates domain addition to existing organization.
     * Entry point for: POST /api/v1/organizations/{id}/domains
     */
    public void validateDomainAdditionForOrganization(Organization organization, Domain domain, 
                                                     List<ValidationException.ValidationError> errors) {
        validateDomainCountLimit(organization.getDomains(), errors);
        validateDomainForCreation(domain, -1, errors);
        validatePrimaryDomainConstraint(organization, domain, errors);
    }

    /**
     * Validates domain verification request.
     * Entry point for: PUT /api/v1/organizations/{id}/domains/{domainId}/verify
     */
    public void validateDomainVerification(Organization organization, String domainId, 
                                         String verificationMethod, List<ValidationException.ValidationError> errors) {
        Domain domain = findDomainInOrganization(organization, domainId, errors);
        if (domain == null) {
            return;
        }

        validateDomainVerificationRules(domain, verificationMethod, errors);
    }

    /**
     * Validates domain removal request.
     * Entry point for: DELETE /api/v1/organizations/{id}/domains/{domainId}
     */
    public void validateDomainRemoval(Organization organization, String domainId, 
                                    List<ValidationException.ValidationError> errors) {
        Domain domain = findDomainInOrganization(organization, domainId, errors);
        if (domain == null) {
            return;
        }

        validateDomainRemovalRules(organization, domain, errors);
    }

    /**
     * Validates domain update request.
     * Entry point for: PUT /api/v1/organizations/{id}/domains/{domainId}
     */
    public void validateDomainUpdate(Organization organization, Domain existingDomain, Domain updatedDomain) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        // Check if domain name is being changed to an existing one
        if (!existingDomain.getName().equals(updatedDomain.getName())) {
            validateDomainGlobalUniqueness(updatedDomain.getName(), "", errors);
        }
        
        validateDomainNameFormat(updatedDomain.getName(), "", errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Domain update validation failed", errors);
        }
    }

    /**
     * Validates setting primary domain request.
     * Entry point for: PUT /api/v1/organizations/{id}/domains/{domainId}/primary
     */
    public void validateSetPrimaryDomain(Organization organization, Domain domain) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        if (!domain.getIsVerified()) {
            errors.add(new ValidationException.ValidationError("domainId", 
                "Only verified domains can be set as primary"));
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Set primary domain validation failed", errors);
        }
    }

    /**
     * Validates domain verification request.
     * Entry point for: PUT /api/v1/organizations/{id}/domains/{domainId}/verify
     */
    public void validateDomainVerificationRequest(Organization organization, Domain domain, 
                                                String verificationMethod, String verificationToken) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        validateDomainVerificationRules(domain, verificationMethod, errors);
        
        // Additional validation for verification token
        if (!domainService.validateVerificationToken(domain, verificationToken, verificationMethod)) {
            errors.add(new ValidationException.ValidationError("verificationToken", "Invalid verification token"));
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Domain verification validation failed", errors);
        }
    }

    // ========== CORE VALIDATION METHODS ==========

    private void validateDomainCountLimit(List<Domain> domains, List<ValidationException.ValidationError> errors) {
        int currentDomainCount = domains != null ? domains.size() : 0;

        if (currentDomainCount > OrganizationConstants.MAX_DOMAINS_PER_ORGANIZATION) {
            errors.add(new ValidationException.ValidationError("domains", 
                "Cannot specify more than " + OrganizationConstants.MAX_DOMAINS_PER_ORGANIZATION + " domains"));
        }
    }

    private void validatePrimaryDomainUniqueness(List<Domain> domains, 
                                                List<ValidationException.ValidationError> errors) {
        long primaryDomainCount = domains != null ? domains.stream()
            .filter(domain -> domain.getIsPrimary() != null && domain.getIsPrimary())
            .count() : 0;
        
        if (primaryDomainCount > 1) {
            errors.add(new ValidationException.ValidationError("domains", 
                "Only one domain can be marked as primary"));
        }
    }

    private void validateDomainDuplicates(List<Domain> domains, 
                                        List<ValidationException.ValidationError> errors) {
        for (int i = 0; i < domains.size(); i++) {
            String domainName = domains.get(i).getName();
            for (int j = i + 1; j < domains.size(); j++) {
                if (domainName.equals(domains.get(j).getName())) {
                    errors.add(new ValidationException.ValidationError("domains[" + j + "].name", 
                        "Duplicate domain name: " + domainName));
                }
            }
        }
    }

    private void validateDomainForCreation(Domain domain, int index, 
                                         List<ValidationException.ValidationError> errors) {
        String fieldPrefix = index >= 0 ? "domains[" + index + "]" : "";
        
        validateDomainNameFormat(domain.getName(), fieldPrefix, errors);
        validateDomainGlobalUniqueness(domain.getName(), fieldPrefix, errors);
    }

    private void validateDomainNameFormat(String domainName, String fieldPrefix, 
                                        List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(domainName)) {
            return;
        }

        // Only business logic validations remain here
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
                StringUtils.hasText(fieldPrefix) ? fieldPrefix + ".name" : "name", 
                "Cannot use reserved or platform domain names"));
        }
    }

    private void validateDomainGlobalUniqueness(String domainName, String fieldPrefix, 
                                              List<ValidationException.ValidationError> errors) {
        if (StringUtils.hasText(domainName) && organizationService.isDomainAlreadyRegistered(domainName)) {
            errors.add(new ValidationException.ValidationError(
                StringUtils.hasText(fieldPrefix) ? fieldPrefix + ".name" : "name", 
                "Domain is already registered with another organization"));
        }
    }

    private void validatePrimaryDomainConstraint(Organization organization, Domain domain, 
                                               List<ValidationException.ValidationError> errors) {
        if (domain.getIsPrimary() != null && domain.getIsPrimary()) {
            boolean hasExistingPrimary = organization.getDomains() != null && 
                organization.getDomains().stream().anyMatch(Domain::getIsPrimary);
            
            if (hasExistingPrimary) {
                errors.add(new ValidationException.ValidationError("isPrimary", 
                    "Organization already has a primary domain. Only one primary domain is allowed."));
            }
        }
    }

    private void validateDomainVerificationRules(Domain domain, String verificationMethod, 
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

        // Use service layer to check verification expiry
        if (domainService.isVerificationExpired(domain)) {
            errors.add(new ValidationException.ValidationError("domainId", 
                "Domain verification token has expired"));
            return;
        }

        validateVerificationRequirements(domain, verificationMethod, errors);
    }

    private void validateVerificationRequirements(Domain domain, String verificationMethod, 
                                                 List<ValidationException.ValidationError> errors) {
        // Use service layer to validate verification token
        if (!domainService.isVerificationTokenValid(domain.getId(), domain.getVerificationToken())) {
            errors.add(new ValidationException.ValidationError("verificationMethod", 
                "Invalid or missing verification token for " + verificationMethod + " verification"));
        }
    }

    private void validateDomainRemovalRules(Organization organization, Domain domain, 
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

        // Use service layer to check for domain usage by active users
        if (organizationService.hasUsersInDomain(organization.getId(), domain.getName())) {
            int userCount = organizationService.getUserCountByDomain(organization.getId(), domain.getName());
            errors.add(new ValidationException.ValidationError("domainId", 
                "Cannot remove domain. " + userCount + " active users are using this domain."));
        }
    }

    private Domain findDomainInOrganization(Organization organization, String domainId, 
                                          List<ValidationException.ValidationError> errors) {
        // Domain ID required validation is handled by @NotBlank annotation in request DTOs
        
        if (organization.getDomains() == null) {
            errors.add(new ValidationException.ValidationError("domainId", "Domain not found in organization"));
            return null;
        }

        return organization.getDomains().stream()
            .filter(domain -> domainId.equals(domain.getId()))
            .findFirst()
            .orElseGet(() -> {
                errors.add(new ValidationException.ValidationError("domainId", "Domain not found in organization"));
                return null;
            });
    }
}