package com.codzs.service.organization;

import com.codzs.constant.organization.OrganizationConstants;
import com.codzs.entity.domain.Domain;
import com.codzs.entity.organization.Organization;
import com.codzs.framework.aware.audit.AuditorAwareImpl;
import com.codzs.framework.exception.util.ExceptionUtils;
import com.codzs.repository.organization.OrganizationDomainRepository;
import com.codzs.repository.organization.OrganizationRepository;
import com.codzs.service.domain.DomainServiceImpl;
import com.codzs.validation.organization.OrganizationDomainBusinessValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private final BaseOrganizationServiceImpl baseOrganizationService;
    private final OrganizationDomainBusinessValidator organizationDomainBusinessValidator;
    private final OrganizationDomainRepository organizationDomainRepository;
    private final AuditorAwareImpl auditorAware;

    @Autowired
    public OrganizationDomainServiceImpl(OrganizationDomainRepository organizationDomainRepository,
                           OrganizationRepository organizationRepository, 
                           ObjectMapper objectMapper) {
        super();
        this.organizationDomainRepository = organizationDomainRepository;
        this.baseOrganizationService = new BaseOrganizationServiceImpl(organizationRepository, objectMapper);
        this.organizationDomainBusinessValidator = new OrganizationDomainBusinessValidator();
        this.auditorAware = new AuditorAwareImpl();
    }

    // ========== API FLOW METHODS ==========

    @Override
    @Transactional
    public List<Domain> addDomainToOrganization(String organizationId, Domain domain) {
        log.debug("Adding domain {} to organization ID: {}", domain.getName(), organizationId);
        
        // Get organization and validate it exists
        Organization organization = baseOrganizationService.getOrganizationAndValidate(organizationId);
        
        // Get domain data for validation
        boolean isDomainAlreadyRegistered = isDomainAlreadyRegistered(organization, domain);
        
        // Business validation for domain addition
        organizationDomainBusinessValidator.validateDomainAddition(organization, domain, OrganizationConstants.MAX_DOMAINS_PER_ORGANIZATION, isDomainAlreadyRegistered);
        
        // Ensure domain has ID if not provided
        if (domain.getId() == null) {
            domain.setId(ObjectId.get().toString());
        }
        domain.setVerificationToken(generateVerificationToken(domain.getId(), domain.getVerificationMethod()));
        if (domain.getIsPrimary() == null) {
            domain.setIsPrimary(false);
        }
        if (domain.getIsVerified() == null) {
            domain.setIsVerified(false);
        }
        
        // Use MongoDB array operation to add domain directly
        organizationDomainRepository.addDomainToEntity(organizationId, domain);
        
        log.info("Added domain {} to organization ID: {}", domain.getName(), organizationId);
        
        // Return updated list of domains
        return getDomainsForEntity(organizationId);
    }

    @Override
    @Transactional
    public List<Domain> updateDomainInOrganization(String organizationId, Domain domain) {
        log.debug("Updating domain {} in organization ID: {}", domain.getId(), organizationId);
        
        // Get organization and validate it exists
        Organization organization = baseOrganizationService.getOrganizationAndValidate(organizationId);
        
        // Find existing domain in organization
        Domain existingDomain = findDomainInOrganization(organization, domain.getId());
        if (existingDomain == null) {
            throw ExceptionUtils.domainNotFound(domain.getId());
        }
        
        // Get domain data for validation
        boolean isDomainAlreadyRegistered = !existingDomain.getName().equals(domain.getName()) && 
            isDomainAlreadyRegistered(organization, domain);
        
        // Business validation for domain update
        organizationDomainBusinessValidator.validateDomainUpdate(existingDomain, domain, isDomainAlreadyRegistered);
        
        // Use MongoDB array operation to update entire domain in one go
        organizationDomainRepository.updateDomain(organizationId, domain.getId(), domain);
        
        log.info("Updated domain {} in organization ID: {}", domain.getId(), organizationId);
        
        // Return updated list of domains
        return getDomainsForEntity(organizationId);
    }

    @Override
    @Transactional
    public List<Domain> removeDomainFromOrganization(String organizationId, String domainId) {
        log.debug("Removing domain {} from organization ID: {}", domainId, organizationId);
        
        // Get organization and validate it exists
        Organization organization = baseOrganizationService.getOrganizationAndValidate(organizationId);
        
        // Find domain to remove
        Domain domainToRemove = findDomainInOrganization(organization, domainId);
        if (domainToRemove == null) {
            throw ExceptionUtils.domainNotFound(domainId);
        }
        
        // Get user data for validation 
        boolean hasUsersInDomain = hasUsersInDomain(organization.getId(), domainToRemove.getName());
        int userCountInDomain = getUserCountByDomain(organization.getId(), domainToRemove.getName());
        
        // Business validation for domain removal
        organizationDomainBusinessValidator.validateDomainRemoval(organization, domainId, hasUsersInDomain, userCountInDomain);
        
        // Use MongoDB array operation to remove domain directly
        organizationDomainRepository.removeDomainFromEntity(organizationId, domainId);
        
        log.info("Removed domain {} from organization ID: {}", domainId, organizationId);
        
        // Return updated list of domains
        return getDomainsForEntity(organizationId);
    }

    @Override
    @Transactional
    public Domain verifyDomainInOrganization(String organizationId, String domainId, 
                                                 String verificationMethod, String verificationToken) {
        log.debug("Verifying domain {} in organization ID: {}", domainId, organizationId);
        
        // Get organization and validate it exists
        Organization organization = baseOrganizationService.getOrganizationAndValidate(organizationId);
        
        // Find domain to verify
        Domain domain = findDomainInOrganization(organization, domainId);
        if (domain == null) {
            throw ExceptionUtils.domainNotFound(domainId);
        }
        
        // Business validation for domain verification
        organizationDomainBusinessValidator.validateDomainVerificationRequest(domain, verificationMethod, verificationToken);
        
        // Use MongoDB array operation to update verification status
        organizationDomainRepository.updateDomainVerificationStatus(organizationId, domainId, Instant.now());
        
        log.info("Verified domain {} in organization ID: {}", domainId, organizationId);
        
        // Return the verified domain
        return getDomainInEntity(organizationId, domainId).orElse(null);
    }

    @Override
    @Transactional
    public List<Domain> setPrimaryDomain(String organizationId, String domainId) {
        log.debug("Setting domain {} as primary for organization ID: {}", domainId, organizationId);
        
        // Get organization and validate it exists
        Organization organization = baseOrganizationService.getOrganizationAndValidate(organizationId);
        
        // Find domain to set as primary
        Domain domain = findDomainInOrganization(organization, domainId);
        if (domain == null) {
            throw ExceptionUtils.domainNotFound(domainId);
        }
        
        // Business validation for setting primary domain
        organizationDomainBusinessValidator.validateSetPrimaryDomain(domain);
        
        // Use MongoDB array operations to update primary domain setting
        organizationDomainRepository.unsetAllPrimaryDomains(organizationId);
        organizationDomainRepository.setPrimaryDomain(organizationId, domainId);
        
        log.info("Set domain {} as primary for organization ID: {}", domainId, organizationId);
        
        // Return updated list of domains
        return getDomainsForEntity(organizationId);
    }

    @Override
    public List<Domain> getDomainsForEntity(String organizationId) {
        log.debug("Getting domains for organization ID: {}", organizationId);
        
        return baseOrganizationService.getOrgById(organizationId)
                .map(Organization::getDomains)
                .filter(domains -> domains != null)
                .orElse(new ArrayList<>());
    }

    @Override
    public Optional<Domain> getDomainInEntity(String organizationId, String domainId) {
        log.debug("Getting domain {} for organization ID: {}", domainId, organizationId);
        
        return baseOrganizationService.getOrgById(organizationId)
                .map(organization -> findDomainInOrganization(organization, domainId))
                .filter(domain -> domain != null);
    }

    @Override
    @Transactional
    public Domain regenerateDomainVerificationToken(String organizationId, String domainId) {
        log.debug("Regenerating verification token for domain {} in organization ID: {}", domainId, organizationId);
        
        // Get organization and validate it exists
        Organization organization = baseOrganizationService.getOrganizationAndValidate(organizationId);
        
        // Find domain
        Domain domain = findDomainInOrganization(organization, domainId);
        if (domain == null) {
            throw ExceptionUtils.domainNotFound(domainId);
        }
        
        // Generate new verification token
        String newToken = generateVerificationToken(domain.getName(), domain.getVerificationMethod());
        
        // Use MongoDB array operation to update verification token
        organizationDomainRepository.updateDomainVerificationToken(organizationId, domainId, newToken);
        
        log.info("Regenerated verification token for domain {} in organization ID: {}", domainId, organizationId);
        
        // Return the domain with updated verification token
        return getDomainInEntity(organizationId, domainId).orElse(null);
    }
 
    @Override
    public String getDomainVerificationInstructions(String organizationId, String domainId) {
        log.debug("Getting verification instructions for domain {} in organization ID: {}", domainId, organizationId);
        
        return getDomainInEntity(organizationId, domainId)
                .map(this::generateVerificationInstructions)
                .orElseThrow(() -> ExceptionUtils.domainNotFound(domainId));
    }

    // ========== UTILITY METHODS ==========

    @Override
    public Optional<Domain> getPrimaryDomainForEntity(String organizationId) {
        List<Domain> domains = getDomainsForEntity(organizationId);
        
        return domains.stream()
                .filter(Domain::getIsPrimary)
                .findFirst();
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

    @Override
    public boolean isDomainAlreadyRegistered(Organization organization, Domain domain) {
        if (!StringUtils.hasText(domain.getName())) {
            return false;
        }
        
        // Check globally across all organizations, excluding the current organization
        if (domain.getId() == null) {
            long count = organizationDomainRepository.countByDomainNameGlobally(domain.getName().trim());
            return count > 0;
        } else {
            long count = organizationDomainRepository.countByDomainNameGlobally(organization.getId(), domain.getId(), domain.getName().trim());
            return count > 0;
        }
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

    protected String getCurrentUser() {
        return auditorAware.getCurrentUserId();
    }
}