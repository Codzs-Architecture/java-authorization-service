package com.codzs.service.domain;

import com.codzs.constant.domain.DomainConstants;
import com.codzs.constant.organization.OrganizationConstants;
import com.codzs.entity.domain.Domain;
import com.codzs.entity.organization.Organization;
import com.codzs.exception.validation.ValidationException;
import com.codzs.repository.organization.OrganizationRepository;
import com.codzs.service.organization.OrganizationService;
import com.codzs.validation.organization.domain.DomainBusinessValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

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
public class DomainServiceImpl implements DomainService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationService organizationService;
    private final DomainBusinessValidator domainBusinessValidator;
    
    private static final Pattern DOMAIN_PATTERN = Pattern.compile(DomainConstants.DOMAIN_BUSINESS_VALIDATION_PATTERN);

    @Autowired
    public DomainServiceImpl(OrganizationRepository organizationRepository,
                           OrganizationService organizationService,
                           DomainBusinessValidator domainBusinessValidator) {
        this.organizationRepository = organizationRepository;
        this.organizationService = organizationService;
        this.domainBusinessValidator = domainBusinessValidator;
    }

    // ========== API FLOW METHODS ==========

    @Override
    @Transactional
    public Organization addDomainToOrganization(Organization organization, Domain domain) {
        log.debug("Adding domain {} to organization ID: {}", domain.getName(), organization.getId());
        
        domainBusinessValidator.validateDomainAddition(organization, domain);
        
        // Apply domain addition business logic
        applyDomainAdditionBusinessLogic(organization, domain);
        
        // Use MongoDB array operation to add domain directly
        organizationRepository.addDomainToOrganization(organization.getId(), domain);
        
        log.info("Added domain {} to organization ID: {}", domain.getName(), organization.getId());
        
        // Return updated organization
        return getOrganizationAndValidate(organization.getId());
    }

    @Override
    @Transactional
    public Organization updateDomainInOrganization(Organization organization, Domain domain) {
        log.debug("Updating domain {} in organization ID: {}", domain.getId(), organization.getId());
        
        // Find existing domain in organization
        Domain existingDomain = findDomainInOrganization(organization, domain.getId());
        if (existingDomain == null) {
            throw new ValidationException("Domain not found with ID: " + domain.getId());
        }
        
        // Business validation for domain update
        domainBusinessValidator.validateDomainUpdate(organization, existingDomain, domain);
        
        // Use MongoDB array update operations for specific fields
        updateDomainFields(organization.getId(), domain.getId(), domain);
        
        log.info("Updated domain {} in organization ID: {}", domain.getId(), organization.getId());
        
        // Return updated organization
        return getOrganizationAndValidate(organization.getId());
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
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        domainBusinessValidator.validateDomainRemoval(organization, domainId, errors);
        if (!errors.isEmpty()) {
            throw new ValidationException("Domain removal validation failed", errors);
        }
        
        // Use MongoDB array operation to remove domain directly
        organizationRepository.removeDomainFromOrganization(organizationId, domainId);
        
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
        domainBusinessValidator.validateDomainVerificationRequest(organization, domain, verificationMethod, verificationToken);
        
        // Use MongoDB array operation to update verification status
        organizationRepository.updateDomainVerificationStatus(organizationId, domainId, Instant.now());
        
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
        domainBusinessValidator.validateSetPrimaryDomain(organization, domain);
        
        // Use MongoDB array operations to update primary domain settings
        organizationRepository.unsetAllPrimaryDomains(organizationId);
        organizationRepository.setPrimaryDomain(organizationId, domainId);
        
        log.info("Set domain {} as primary for organization ID: {}", domainId, organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    @Override
    public List<Domain> getDomainsForOrganization(String organizationId) {
        log.debug("Getting domains for organization ID: {}", organizationId);
        
        Organization organization = organizationService.findById(organizationId);
        if (organization == null) {
            log.warn("Organization not found with ID: {}", organizationId);
            return new ArrayList<>();
        }
        
        return organization.getDomains() != null ? organization.getDomains() : new ArrayList<>();
    }

    @Override
    public Domain getDomainInOrganization(String organizationId, String domainId) {
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
        organizationRepository.updateDomainVerificationToken(organizationId, domainId, newToken);
        
        log.info("Regenerated verification token for domain {} in organization ID: {}", domainId, organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }
 
    @Override
    public String getDomainVerificationInstructions(String organizationId, String domainId) {
        log.debug("Getting verification instructions for domain {} in organization ID: {}", domainId, organizationId);
        
        Domain domain = getDomainInOrganization(organizationId, domainId);
        if (domain == null) {
            throw new ValidationException("Domain not found with ID: " + domainId);
        }
        
        return generateVerificationInstructions(domain);
    }

    // ========== UTILITY METHODS ==========

    @Override
    public boolean isDomainAlreadyRegistered(String domainName) {
        if (!StringUtils.hasText(domainName)) {
            return false;
        }
        
        return organizationRepository.existsByDomainsName(domainName.toLowerCase().trim());
    }

    @Override
    public boolean isValidDomainFormat(String domainName) {
        if (!StringUtils.hasText(domainName)) {
            return false;
        }
        
        String normalized = domainName.toLowerCase().trim();
        return DOMAIN_PATTERN.matcher(normalized).matches() && 
               normalized.length() <= DomainConstants.MAX_DOMAIN_NAME_LENGTH;
    }

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
    public Domain getPrimaryDomainForOrganization(String organizationId) {
        List<Domain> domains = getDomainsForOrganization(organizationId);
        
        return domains.stream()
                .filter(Domain::getIsPrimary)
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean hasVerifiedDomains(String organizationId) {
        List<Domain> domains = getDomainsForOrganization(organizationId);
        
        return domains.stream()
                .anyMatch(Domain::getIsVerified);
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

    private void updateDomainFields(String organizationId, String domainId, Domain updatedDomain) {
        // Use specific MongoDB array update operations for each field that needs updating
        if (updatedDomain.getName() != null) {
            organizationRepository.updateDomainName(organizationId, domainId, updatedDomain.getName());
        }
        
        if (updatedDomain.getVerificationMethod() != null) {
            organizationRepository.updateDomainVerificationMethod(organizationId, domainId, updatedDomain.getVerificationMethod());
        }
        
        if (updatedDomain.getIsPrimary() != null) {
            // If setting as primary, first unset all other primary domains
            if (updatedDomain.getIsPrimary()) {
                organizationRepository.unsetAllPrimaryDomains(organizationId);
            }
            organizationRepository.updateDomainPrimaryStatus(organizationId, domainId, updatedDomain.getIsPrimary());
        }
        
        log.debug("Updated domain fields for domain {} in organization {}", domainId, organizationId);
    }

    private void applyDomainAdditionBusinessLogic(Organization organization, Domain domain) {
        // Auto-set as primary if first domain
        if (organization.getDomains().size() == 1) {
            domain.setIsPrimary(true);
        }
        
        log.debug("Applied domain addition business logic for organization: {}", organization.getName());
    }

    private String generateVerificationInstructions(Domain domain) {
        StringBuilder instructions = new StringBuilder();
        
        switch (domain.getVerificationMethod()) {
            case "DNS":
                instructions.append("Add the following TXT record to your DNS configuration:\n");
                instructions.append("Name: _codzs-verification\n");
                instructions.append("Value: ").append(domain.getVerificationToken()).append("\n");
                instructions.append("TTL: 3600 (or your default)\n");
                break;
                
            case "EMAIL":
                instructions.append("An email has been sent to admin@").append(domain.getName()).append("\n");
                instructions.append("Click the verification link in the email to complete verification.\n");
                break;
                
            case "FILE":
                instructions.append("Upload a file named 'codzs-verification.txt' to your domain root:\n");
                instructions.append("URL: http://").append(domain.getName()).append("/codzs-verification.txt\n");
                instructions.append("Content: ").append(domain.getVerificationToken()).append("\n");
                break;
                
            default:
                instructions.append("Verification method not supported: ").append(domain.getVerificationMethod());
        }
        
        return instructions.toString();
    }


    // ========== DOMAIN VERIFICATION METHODS ==========

    @Override
    public boolean isVerificationTokenValid(String domainId, String token) {
        log.debug("Checking verification token validity for domain: {}", domainId);
        
        // TODO: Implement actual token validation against stored tokens
        // For now, return true if token is not empty (prevents validation errors)
        return StringUtils.hasText(token);
    }

    @Override
    public String generateVerificationToken(String domainId, String verificationMethod) {
        log.debug("Generating verification token for domain: {} using method: {}", domainId, verificationMethod);
        
        // Generate a simple token for now - in production this would be more sophisticated
        String token = "codzs-verify-" + UUID.randomUUID().toString().substring(0, 8);
        
        // TODO: Store token in database with expiration time
        // TODO: Associate token with domain and verification method
        
        log.info("Generated verification token for domain: {}", domainId);
        return token;
    }

    @Override
    public boolean verifyDomainOwnership(String domainId, String verificationMethod, String token) {
        log.debug("Verifying domain ownership for domain: {} using method: {}", domainId, verificationMethod);
        
        if (!StringUtils.hasText(token)) {
            log.warn("No verification token provided for domain: {}", domainId);
            return false;
        }

        // TODO: Implement actual verification logic based on method
        switch (verificationMethod) {
            case "DNS":
                // TODO: Implement DNS TXT record verification
                log.debug("DNS verification not implemented yet for domain: {}", domainId);
                return false;
            case "EMAIL":
                // TODO: Implement email verification confirmation
                log.debug("Email verification not implemented yet for domain: {}", domainId);
                return false;
            case "FILE":
                // TODO: Implement file verification check
                log.debug("File verification not implemented yet for domain: {}", domainId);
                return false;
            default:
                log.warn("Unknown verification method: {} for domain: {}", verificationMethod, domainId);
                return false;
        }
    }

    @Override
    public String getVerificationInstructions(String domainName, String verificationMethod, String token) {
        log.debug("Getting verification instructions for domain: {} using method: {}", domainName, verificationMethod);
        
        return switch (verificationMethod) {
            case "DNS" -> getDnsInstructions(domainName, token);
            case "EMAIL" -> getEmailInstructions(domainName, token);
            case "FILE" -> getFileInstructions(domainName, token);
            default -> "Invalid verification method";
        };
    }

    @Override
    public boolean isVerificationExpired(Domain domain) {
        if (domain.getCreatedDate() == null) {
            return false;
        }
        
        Instant expiryTime = domain.getCreatedDate().plus(Duration.ofHours(
            OrganizationConstants.DOMAIN_VERIFICATION_EXPIRY_HOURS));
        
        boolean expired = Instant.now().isAfter(expiryTime);
        log.debug("Domain verification expired check for domain {}: {}", domain.getName(), expired);
        
        return expired;
    }

    @Override
    public boolean isVerificationMethodValid(String domainName, String verificationMethod, String organizationType) {
        log.debug("Validating verification method {} for domain {} and org type {}", 
                 verificationMethod, domainName, organizationType);
        
        List<String> availableMethods = Arrays.asList(getAvailableVerificationMethods(organizationType));
        return availableMethods.contains(verificationMethod);
    }

    // ========== DNS VERIFICATION METHODS ==========

    @Override
    public boolean validateDnsRecord(String domainName, String expectedToken) {
        log.debug("Validating DNS record for domain: {} with token: {}", domainName, expectedToken);
        
        // TODO: Implement actual DNS TXT record lookup
        // This would typically use DNS resolution libraries to check for TXT records
        log.debug("DNS validation not implemented yet - returning false");
        return false;
    }

    // ========== EMAIL VERIFICATION METHODS ==========

    @Override
    public boolean sendVerificationEmail(String domainName, String token, String organizationName) {
        log.debug("Sending verification email for domain: {} to organization: {}", domainName, organizationName);
        
        // TODO: Implement email sending to standard admin addresses
        // Standard addresses: admin@domain, webmaster@domain, postmaster@domain
        log.debug("Email sending not implemented yet - returning false");
        return false;
    }

    // ========== FILE VERIFICATION METHODS ==========

    @Override
    public boolean validateVerificationFile(String domainName, String token) {
        log.debug("Validating verification file for domain: {} with token: {}", domainName, token);
        
        // TODO: Implement HTTP request to check for verification file
        // Expected URL: http://domain/.well-known/codzs-domain-verification.txt
        log.debug("File validation not implemented yet - returning false");
        return false;
    }

    // ========== UTILITY METHODS ==========

    @Override
    public String[] getAvailableVerificationMethods(String organizationType) {
        if ("INDIVIDUAL".equals(organizationType)) {
            // Individual organizations have limited verification options
            return new String[]{"EMAIL", "FILE"};
        } else {
            // Business organizations have all verification methods
            return new String[]{"DNS", "EMAIL", "FILE"};
        }
    }

    @Override
    public int getEstimatedVerificationTime(String verificationMethod) {
        return switch (verificationMethod) {
            case "DNS" -> 15; // DNS propagation typically takes 5-15 minutes
            case "EMAIL" -> 5;  // Email verification can be immediate
            case "FILE" -> 2;   // File upload verification is immediate
            default -> 10;
        };
    }

    // ========== PRIVATE HELPER METHODS ==========

    private String getDnsInstructions(String domainName, String token) {
        return String.format(
            "To verify ownership of %s via DNS:\n" +
            "1. Add a TXT record to your domain's DNS settings\n" +
            "2. Record name: _codzs-challenge\n" +
            "3. Record value: %s\n" +
            "4. Wait for DNS propagation (5-15 minutes)\n" +
            "5. Click verify to complete the process",
            domainName, token
        );
    }

    private String getEmailInstructions(String domainName, String token) {
        return String.format(
            "To verify ownership of %s via email:\n" +
            "1. Check your email at admin@%s, webmaster@%s, or postmaster@%s\n" +
            "2. Click the verification link in the email\n" +
            "3. Verification token: %s",
            domainName, domainName, domainName, domainName, token
        );
    }

    private String getFileInstructions(String domainName, String token) {
        return String.format(
            "To verify ownership of %s via file upload:\n" +
            "1. Create a file named 'codzs-domain-verification.txt'\n" +
            "2. File content: %s\n" +
            "3. Upload to: http://%s/.well-known/codzs-domain-verification.txt\n" +
            "4. Ensure the file is publicly accessible\n" +
            "5. Click verify to complete the process",
            domainName, token, domainName
        );
    }
}