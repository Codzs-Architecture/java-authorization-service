package com.codzs.validation.organization;

import com.codzs.entity.domain.Domain;
import com.codzs.entity.organization.Organization;
import com.codzs.exception.validation.ValidationException;
import com.codzs.service.organization.OrganizationDomainService;
import com.codzs.validation.domain.DomainBusinessValidator;
import com.codzs.service.domain.DomainService;

import org.springframework.beans.factory.annotation.Autowired;
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
public class OrganizationDomainBusinessValidator extends DomainBusinessValidator {

    private final OrganizationDomainService organizationDomainService;

    @Autowired
    public OrganizationDomainBusinessValidator(OrganizationDomainService organizationDomainService,
                                  DomainService<Organization> domainService) {
        super(domainService);
        this.organizationDomainService = organizationDomainService;
    }

    // ========== ENTRY POINT METHODS FOR ORGANIZATION APIs ==========

    /**
     * Validates domain addition for service layer.
     * Simplified validation for OrganizationDomainService.
     */
    public void validateDomainAddition(Organization organization, Domain domain, int maxCount) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateDomainAdditionForEntity(organization.getDomains(), domain, maxCount, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Domain addition validation failed", errors);
        }
    }

    /**
     * Validates domain removal request.
     * Entry point for: DELETE /api/v1/organizations/{id}/domains/{domainId}
     */
    public void validateDomainRemoval(Organization organization, String domainId) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();

        Domain domain = findDomainInEntity(organization.getDomains(), domainId, errors);
        if (domain == null) {
            return;
        }

        validateDomainRemovalRules(organization, domain, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Domain removal validation failed", errors);
        }
    }

    /**
     * Validates domain update request.
     * Entry point for: PUT /api/v1/organizations/{id}/domains/{domainId}
     */
    public void validateDomainUpdate(Domain existingDomain, Domain updatedDomain) {
        super.validateDomainUpdate(existingDomain, updatedDomain);
    }

    /**
     * Validates domain verification request.
     * Entry point for: PUT /api/v1/organizations/{id}/domains/{domainId}/verify
     */
    public void validateDomainVerificationRequest(Domain domain, String verificationMethod, String verificationToken) {
        super.validateDomainVerificationRequest(domain, verificationMethod, verificationToken);
    }

    /**
     * Validates setting primary domain request.
     * Entry point for: PUT /api/v1/organizations/{id}/domains/{domainId}/primary
     */
    public void validateSetPrimaryDomain(Domain domain) {
        super.validateSetPrimaryDomain(domain);
    }

    // ========== CORE VALIDATION METHODS ==========

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
        if (organizationDomainService.hasUsersInDomain(organization.getId(), domain.getName())) {
            int userCount = organizationDomainService.getUserCountByDomain(organization.getId(), domain.getName());
            errors.add(new ValidationException.ValidationError("domainId", 
                "Cannot remove domain. " + userCount + " active users are using this domain."));
        }
    }

}