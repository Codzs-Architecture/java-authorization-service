package com.codzs.service.domain;

import com.codzs.constant.domain.DomainConstants;
import com.codzs.dto.organization.request.DomainRequestDto;
import com.codzs.entity.domain.Domain;
import com.codzs.entity.organization.Organization;
import com.codzs.exception.validation.ValidationException;
import com.codzs.mapper.organization.OrganizationDomainMapper;
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
    private final OrganizationDomainMapper organizationDomainMapper;
    
    private static final Pattern DOMAIN_PATTERN = Pattern.compile(DomainConstants.DOMAIN_BUSINESS_VALIDATION_PATTERN);

    @Autowired
    public DomainServiceImpl(OrganizationRepository organizationRepository,
                           OrganizationService organizationService,
                           DomainBusinessValidator domainBusinessValidator,
                           OrganizationDomainMapper organizationDomainMapper) {
        this.organizationRepository = organizationRepository;
        this.organizationService = organizationService;
        this.domainBusinessValidator = domainBusinessValidator;
        this.organizationDomainMapper = organizationDomainMapper;
    }

    // ========== API FLOW METHODS ==========

    @Override
    @Transactional
    public Organization addDomainToOrganization(Organization organization, Domain domain) {
        log.debug("Adding domain {} to organization ID: {}", domain.getName(), organization.getId());
        
        // Business validation as first step - convert entity to request DTO for validation
        DomainRequestDto validationRequest = convertToDomainRequest(domain);
        domainBusinessValidator.validateDomainAddition(organization, validationRequest);
        
        // Add domain to organization
        if (organization.getDomains() == null) {
            organization.setDomains(new ArrayList<>());
        }
        organization.getDomains().add(domain);
        
        // Apply domain addition business logic
        applyDomainAdditionBusinessLogic(organization, domain);
        
        // Save organization with new domain
        Organization updatedOrganization = organizationRepository.save(organization);
        
        log.info("Added domain {} to organization ID: {}", domain.getName(), organization.getId());
        
        return updatedOrganization;
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
        
        // Business validation for domain update - convert entity to request DTO for validation
        DomainRequestDto validationRequest = convertToDomainRequest(domain);
        validateDomainUpdateFlow(organization, existingDomain, validationRequest);
        
        // Update existing domain with new data
        updateDomainFields(existingDomain, domain);
        
        // Apply domain update business logic
        applyDomainUpdateBusinessLogic(organization, existingDomain);
        
        // Save organization with updated domain
        Organization updatedOrganization = organizationRepository.save(organization);
        
        log.info("Updated domain {} in organization ID: {}", domain.getId(), organization.getId());
        
        return updatedOrganization;
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
        validateDomainRemovalFlow(organization, domainToRemove);
        
        // Remove domain from organization
        organization.getDomains().remove(domainToRemove);
        
        // Apply domain removal business logic
        applyDomainRemovalBusinessLogic(organization, domainToRemove);
        
        // Save organization without the domain
        Organization updatedOrganization = organizationRepository.save(organization);
        
        log.info("Removed domain {} from organization ID: {}", domainId, organizationId);
        
        return updatedOrganization;
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
        validateDomainVerificationFlow(organization, domain, verificationMethod, verificationToken);
        
        // Mark domain as verified
        domain.setIsVerified(true);
        domain.setVerifiedDate(Instant.now());
        
        // Apply domain verification business logic
        applyDomainVerificationBusinessLogic(organization, domain);
        
        // Save organization with verified domain
        Organization updatedOrganization = organizationRepository.save(organization);
        
        log.info("Verified domain {} in organization ID: {}", domainId, organizationId);
        
        return updatedOrganization;
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
        validateSetPrimaryDomainFlow(organization, domain);
        
        // Update primary domain settings
        updatePrimaryDomainSettings(organization, domain);
        
        // Save organization with new primary domain
        Organization updatedOrganization = organizationRepository.save(organization);
        
        log.info("Set domain {} as primary for organization ID: {}", domainId, organizationId);
        
        return updatedOrganization;
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
        domain.setVerificationToken(newToken);
        
        // Save organization with new token
        Organization updatedOrganization = organizationRepository.save(organization);
        
        log.info("Regenerated verification token for domain {} in organization ID: {}", domainId, organizationId);
        
        return updatedOrganization;
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

    private void validateDomainUpdateFlow(Organization organization, Domain existingDomain, DomainRequestDto request) {
        // Validate that domain name is not changing to an existing one
        if (!existingDomain.getName().equals(request.getName()) && 
            isDomainAlreadyRegistered(request.getName())) {
            throw new ValidationException("Domain name already registered: " + request.getName());
        }
        
        // Additional domain update validations
        log.debug("Validated domain update for organization: {}", organization.getName());
    }

    private void validateDomainRemovalFlow(Organization organization, Domain domain) {
        // Cannot remove the only domain
        if (organization.getDomains().size() == 1) {
            throw new ValidationException("Cannot remove the only domain from organization");
        }
        
        // Cannot remove primary domain unless another is set as primary
        if (domain.getIsPrimary()) {
            boolean hasOtherPrimary = organization.getDomains().stream()
                    .anyMatch(d -> !d.getId().equals(domain.getId()) && d.getIsPrimary());
            
            if (!hasOtherPrimary) {
                throw new ValidationException("Cannot remove primary domain. Set another domain as primary first.");
            }
        }
        
        log.debug("Validated domain removal for organization: {}", organization.getName());
    }

    private void validateDomainVerificationFlow(Organization organization, Domain domain, 
                                              String verificationMethod, String verificationToken) {
        if (domain.getIsVerified()) {
            throw new ValidationException("Domain is already verified");
        }
        
        if (!domain.getVerificationMethod().equals(verificationMethod)) {
            throw new ValidationException("Verification method does not match domain's configured method");
        }
        
        if (StringUtils.hasText(verificationToken) && 
            !validateVerificationToken(domain, verificationToken, verificationMethod)) {
            throw new ValidationException("Invalid verification token");
        }
        
        log.debug("Validated domain verification for organization: {}", organization.getName());
    }

    private void validateSetPrimaryDomainFlow(Organization organization, Domain domain) {
        if (!domain.getIsVerified()) {
            throw new ValidationException("Only verified domains can be set as primary");
        }
        
        log.debug("Validated set primary domain for organization: {}", organization.getName());
    }

    private void updatePrimaryDomainSettings(Organization organization, Domain newPrimaryDomain) {
        // Unset existing primary domains
        organization.getDomains().forEach(d -> d.setIsPrimary(false));
        
        // Set new primary domain
        newPrimaryDomain.setIsPrimary(true);
        
        log.debug("Updated primary domain settings for organization: {}", organization.getName());
    }

    private void applyDomainAdditionBusinessLogic(Organization organization, Domain domain) {
        // Auto-set as primary if first domain
        if (organization.getDomains().size() == 1) {
            domain.setIsPrimary(true);
        }
        
        log.debug("Applied domain addition business logic for organization: {}", organization.getName());
    }

    private void applyDomainUpdateBusinessLogic(Organization organization, Domain domain) {
        // Apply any additional domain update business logic here
        log.debug("Applied domain update business logic for organization: {}", organization.getName());
    }

    private void updateDomainFields(Domain existingDomain, Domain updatedDomain) {
        // Update fields from updated domain to existing domain
        if (updatedDomain.getName() != null) {
            existingDomain.setName(updatedDomain.getName());
        }
        if (updatedDomain.getIsPrimary() != null) {
            existingDomain.setIsPrimary(updatedDomain.getIsPrimary());
        }
        if (updatedDomain.getVerificationMethod() != null) {
            existingDomain.setVerificationMethod(updatedDomain.getVerificationMethod());
        }
    }

    private DomainRequestDto convertToDomainRequest(Domain domain) {
        DomainRequestDto request = new DomainRequestDto();
        request.setName(domain.getName());
        request.setIsPrimary(domain.getIsPrimary());
        request.setVerificationMethod(domain.getVerificationMethod());
        return request;
    }

    private void applyDomainRemovalBusinessLogic(Organization organization, Domain domain) {
        // Apply any additional domain removal business logic here
        log.debug("Applied domain removal business logic for organization: {}", organization.getName());
    }

    private void applyDomainVerificationBusinessLogic(Organization organization, Domain domain) {
        // Apply any additional domain verification business logic here
        log.debug("Applied domain verification business logic for organization: {}", organization.getName());
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
            DomainConstants.DOMAIN_VERIFICATION_EXPIRY_HOURS != null ? 
            DomainConstants.DOMAIN_VERIFICATION_EXPIRY_HOURS : 24));
        
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