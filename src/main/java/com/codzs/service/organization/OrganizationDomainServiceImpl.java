package com.codzs.service.organization;

import com.codzs.constant.organization.OrganizationConstants;
import com.codzs.entity.domain.Domain;
import com.codzs.entity.organization.Organization;
import com.codzs.exception.validation.ValidationException;
import com.codzs.repository.organization.OrganizationDomainRepository;
import com.codzs.service.domain.DomainServiceImpl;
import com.codzs.validation.organization.OrganizationDomainBusinessValidator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation for Domain-related business operations within organizations.
 * Manages domain operations as embedded objects within organizations
 * with proper business validation and transaction management.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class OrganizationDomainServiceImpl extends DomainServiceImpl<Organization> implements OrganizationDomainService {

    private final OrganizationService organizationService;
    private final OrganizationDomainBusinessValidator organizationDomainBusinessValidator;
    
    @Autowired
    public OrganizationDomainServiceImpl(OrganizationDomainRepository domainRepository,
                           OrganizationService organizationService,
                           OrganizationDomainBusinessValidator organizationDomainBusinessValidator) {
        super(domainRepository);
        this.organizationService = organizationService;
        this.organizationDomainBusinessValidator = organizationDomainBusinessValidator;
    }

    // ========== API FLOW METHODS ==========

    @Override
    @Transactional
    public Organization addDomainToOrganization(String organizationId, Domain domain) {
        log.debug("Adding domain {} to organization ID: {}", domain.getName(), organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Business validation for domain addition
        organizationDomainBusinessValidator.validateDomainAddition(organization, domain, OrganizationConstants.MAX_DOMAINS_PER_ORGANIZATION);
        
        // Ensure domain has ID if not provided
        if (domain.getId() == null) {
            domain.setId(UUID.randomUUID().toString());
        }
        
        // Use MongoDB array operation to add domain directly
        domainRepository.addDomainToEntity(organizationId, domain);
        
        log.info("Added domain {} to organization ID: {}", domain.getName(), organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    @Override
    @Transactional
    public Organization updateDomainInOrganization(String organizationId, Domain domain) {
        log.debug("Updating domain {} in organization ID: {}", domain.getId(), organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Find existing domain in organization
        Domain existingDomain = findDomainInOrganization(organization, domain.getId());
        if (existingDomain == null) {
            throw new ValidationException("Domain not found with ID: " + domain.getId());
        }
        
        // Business validation for domain update
        organizationDomainBusinessValidator.validateDomainUpdate(existingDomain, domain);
        
        // Use MongoDB array operation to update entire domain in one go
        domainRepository.updateDomain(organizationId, domain.getId(), domain);
        
        log.info("Updated domain {} in organization ID: {}", domain.getId(), organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    @Override
    @Transactional
    public Organization removeDomainFromOrganization(String organizationId, String domainId) {
        log.debug("Removing domain {} from organization ID: {}", domainId, organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Find domain to remove
        Domain domainToRemove = findDomainInOrganization(organization, domainId);
        if (domainToRemove == null) {
            throw new ValidationException("Domain not found with ID: " + domainId);
        }
        
        // Business validation for domain removal
        organizationDomainBusinessValidator.validateDomainRemoval(organization, domainId);
        
        // Use MongoDB array operation to remove domain directly
        domainRepository.removeDomainFromEntity(organizationId, domainId);
        
        log.info("Removed domain {} from organization ID: {}", domainId, organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    @Override
    @Transactional
    public Organization verifyDomainInOrganization(String organizationId, String domainId, 
                                                 String verificationMethod, String verificationToken) {
        log.debug("Verifying domain {} in organization ID: {}", domainId, organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Find domain to verify
        Domain domain = findDomainInOrganization(organization, domainId);
        if (domain == null) {
            throw new ValidationException("Domain not found with ID: " + domainId);
        }
        
        // Business validation for domain verification
        organizationDomainBusinessValidator.validateDomainVerificationRequest(domain, verificationMethod, verificationToken);
        
        // Use MongoDB array operation to update verification status
        domainRepository.updateDomainVerificationStatus(organizationId, domainId, Instant.now());
        
        log.info("Verified domain {} in organization ID: {}", domainId, organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    @Override
    @Transactional
    public Organization setPrimaryDomain(String organizationId, String domainId) {
        log.debug("Setting domain {} as primary for organization ID: {}", domainId, organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Find domain to set as primary
        Domain domain = findDomainInOrganization(organization, domainId);
        if (domain == null) {
            throw new ValidationException("Domain not found with ID: " + domainId);
        }
        
        // Business validation for setting primary domain
        organizationDomainBusinessValidator.validateSetPrimaryDomain(domain);
        
        // Use MongoDB array operations to update primary domain settings
        domainRepository.unsetAllPrimaryDomains(organizationId);
        domainRepository.setPrimaryDomain(organizationId, domainId);
        
        log.info("Set domain {} as primary for organization ID: {}", domainId, organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    @Override
    public List<Domain> getDomainsForEntity(String organizationId) {
        log.debug("Getting domains for organization ID: {}", organizationId);
        
        Organization organization = organizationService.findById(organizationId);
        if (organization == null) {
            log.warn("Organization not found with ID: {}", organizationId);
            return new ArrayList<>();
        }
        
        return organization.getDomains() != null ? organization.getDomains() : new ArrayList<>();
    }

    @Override
    public Domain getDomainInEntity(String organizationId, String domainId) {
        log.debug("Getting domain {} for organization ID: {}", domainId, organizationId);
        
        Organization organization = organizationService.findById(organizationId);
        if (organization == null) {
            log.warn("Organization not found with ID: {}", organizationId);
            return null;
        }
        
        return findDomainInOrganization(organization, domainId);
    }

    @Override
    @Transactional
    public Organization regenerateDomainVerificationToken(String organizationId, String domainId) {
        log.debug("Regenerating verification token for domain {} in organization ID: {}", domainId, organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Find domain
        Domain domain = findDomainInOrganization(organization, domainId);
        if (domain == null) {
            throw new ValidationException("Domain not found with ID: " + domainId);
        }
        
        // Generate new verification token
        String newToken = generateVerificationToken(domain.getName(), domain.getVerificationMethod());
        
        // Use MongoDB array operation to update verification token
        domainRepository.updateDomainVerificationToken(organizationId, domainId, newToken);
        
        log.info("Regenerated verification token for domain {} in organization ID: {}", domainId, organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }
 
    @Override
    public String getDomainVerificationInstructions(String organizationId, String domainId) {
        log.debug("Getting verification instructions for domain {} in organization ID: {}", domainId, organizationId);
        
        Domain domain = getDomainInEntity(organizationId, domainId);
        if (domain == null) {
            throw new ValidationException("Domain not found with ID: " + domainId);
        }
        
        return generateVerificationInstructions(domain);
    }

    // ========== UTILITY METHODS ==========

    @Override
    public boolean validateVerificationToken(Domain domain, String providedToken, String verificationMethod) {
        if (!StringUtils.hasText(providedToken) || !StringUtils.hasText(domain.getVerificationToken())) {
            return false;
        }
        
        // Check token match
        if (!domain.getVerificationToken().equals(providedToken)) {
            return false;
        }
        
        // Check verification method match
        if (!domain.getVerificationMethod().equals(verificationMethod)) {
            return false;
        }
        
        // Check token expiry
        if (domain.getCreatedDate() != null) {
            Instant expiryTime = domain.getCreatedDate().plus(java.time.Duration.ofHours(24));
            if (Instant.now().isAfter(expiryTime)) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public Domain getPrimaryDomainForEntity(String organizationId) {
        List<Domain> domains = getDomainsForEntity(organizationId);
        
        return domains.stream()
                .filter(Domain::getIsPrimary)
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean hasVerifiedDomains(String organizationId) {
        List<Domain> domains = getDomainsForEntity(organizationId);
        
        return domains.stream()
                .anyMatch(Domain::getIsVerified);
    }

    // ========== ORGANIZATION DOMAIN VALIDATION METHODS ==========

    @Override
    public boolean hasUsersInDomain(String organizationId, String domainName) {
        log.debug("Checking if organization {} has users in domain: {}", organizationId, domainName);
        
        // TODO: This should check with User service or repository to see if any users

        // in this organization are using the specified domain in their email addresses
        // For now, returning false to avoid blocking domain removal
        log.debug("User domain check not implemented yet - returning false");
        return false;
    }

    @Override
    public int getUserCountByDomain(String organizationId, String domainName) {
        log.debug("Getting user count for organization {} in domain: {}", organizationId, domainName);
        
        // TODO: This should query User service or repository to count users
        
        // in this organization with email addresses in the specified domain
        // For now, returning 0 to avoid blocking domain removal
        log.debug("User domain count not implemented yet - returning 0");
        return 0;
    }

    // ========== PRIVATE HELPER METHODS ==========

    private Organization getOrganizationAndValidate(String organizationId) {
        Organization organization = organizationService.findById(organizationId);
        if (organization == null) {
            throw new ValidationException("Organization not found with ID: " + organizationId);
        }
        return organization;
    }

    private Domain findDomainInOrganization(Organization organization, String domainId) {
        if (organization.getDomains() == null) {
            return null;
        }
        
        return organization.getDomains().stream()
                .filter(domain -> domainId.equals(domain.getId()))
                .findFirst()
                .orElse(null);
    }
}