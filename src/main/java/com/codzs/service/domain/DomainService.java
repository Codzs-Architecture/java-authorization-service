package com.codzs.service.domain;

import com.codzs.dto.organization.request.DomainRequestDto;
import com.codzs.entity.domain.Domain;
import com.codzs.entity.organization.Organization;

import java.util.List;

/**
 * Service interface for Domain-related business operations within organizations.
 * Manages domain operations as embedded objects within organizations
 * with proper business validation and transaction management.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public interface DomainService {

    // ========== API FLOW METHODS ==========

    /**
     * Adds a domain to an organization.
     * API: POST /api/v1/organizations/{id}/domains
     *
     * @param organization the organization entity
     * @param domain the domain entity to add
     * @return the updated organization entity with new domain
     */
    Organization addDomainToOrganization(Organization organization, Domain domain);

    /**
     * Updates a domain within an organization.
     * API: PUT /api/v1/organizations/{id}/domains/{domainId}
     *
     * @param organization the organization entity
     * @param domain the domain entity with updates
     * @return the updated organization entity
     */
    Organization updateDomainInOrganization(Organization organization, Domain domain);

    /**
     * Removes a domain from an organization.
     * API: DELETE /api/v1/organizations/{id}/domains/{domainId}
     *
     * @param organizationId the organization ID
     * @param domainId the domain ID to remove
     * @return the updated organization entity without the domain
     */
    Organization removeDomainFromOrganization(String organizationId, String domainId);

    /**
     * Verifies a domain within an organization.
     * API: PUT /api/v1/organizations/{id}/domains/{domainId}/verify
     *
     * @param organizationId the organization ID
     * @param domainId the domain ID to verify
     * @param verificationMethod the verification method used
     * @param verificationToken the verification token (optional)
     * @return the updated organization entity with verified domain
     */
    Organization verifyDomainInOrganization(String organizationId, String domainId, 
                                          String verificationMethod, String verificationToken);

    /**
     * Sets a domain as primary within an organization.
     * API: PUT /api/v1/organizations/{id}/domains/{domainId}/set-primary
     *
     * @param organizationId the organization ID
     * @param domainId the domain ID to set as primary
     * @return the updated organization entity with new primary domain
     */
    Organization setPrimaryDomain(String organizationId, String domainId);

    /**
     * Gets all domains for an organization.
     * API: GET /api/v1/organizations/{id}/domains
     *
     * @param organizationId the organization ID
     * @return list of domain entities
     */
    List<Domain> getDomainsForOrganization(String organizationId);

    /**
     * Gets a specific domain within an organization.
     * API: GET /api/v1/organizations/{id}/domains/{domainId}
     *
     * @param organizationId the organization ID
     * @param domainId the domain ID
     * @return the domain entity or null if not found
     */
    Domain getDomainInOrganization(String organizationId, String domainId);

    /**
     * Regenerates verification token for a domain.
     * API: POST /api/v1/organizations/{id}/domains/{domainId}/regenerate-token
     *
     * @param organizationId the organization ID
     * @param domainId the domain ID
     * @return the updated organization entity with new verification token
     */
    Organization regenerateDomainVerificationToken(String organizationId, String domainId);

    /**
     * Gets verification instructions for a domain.
     * API: GET /api/v1/organizations/{id}/domains/{domainId}/verification-instructions
     *
     * @param organizationId the organization ID
     * @param domainId the domain ID
     * @return verification instructions as string
     */
    String getDomainVerificationInstructions(String organizationId, String domainId);

    // ========== UTILITY METHODS ==========

    /**
     * Checks if domain name is already registered globally.
     *
     * @param domainName the domain name to check
     * @return true if domain is already registered
     */
    boolean isDomainAlreadyRegistered(String domainName);

    /**
     * Validates domain name format.
     *
     * @param domainName the domain name to validate
     * @return true if domain name format is valid
     */
    boolean isValidDomainFormat(String domainName);

    /**
     * Generates verification token for domain.
     *
     * @param domainName the domain name
     * @param verificationMethod the verification method
     * @return generated verification token
     */
    String generateVerificationToken(String domainName, String verificationMethod);

    /**
     * Validates domain verification token.
     *
     * @param domain the domain entity
     * @param providedToken the provided verification token
     * @param verificationMethod the verification method
     * @return true if token is valid
     */
    boolean validateVerificationToken(Domain domain, String providedToken, String verificationMethod);

    /**
     * Gets primary domain for an organization.
     *
     * @param organizationId the organization ID
     * @return the primary domain entity or null if none
     */
    Domain getPrimaryDomainForOrganization(String organizationId);

    /**
     * Checks if organization has verified domains.
     *
     * @param organizationId the organization ID
     * @return true if organization has at least one verified domain
     */
    boolean hasVerifiedDomains(String organizationId);


    // ========== DOMAIN VERIFICATION METHODS ==========

    /**
     * Checks if a verification token is valid for a domain.
     * Used by validation layer to validate domain verification requests.
     *
     * @param domainId the domain ID
     * @param token the verification token to validate
     * @return true if token is valid and not expired
     */
    boolean isVerificationTokenValid(String domainId, String token);

    /**
     * Verifies domain ownership using the specified method.
     * Performs actual verification check against external systems.
     *
     * @param domainId the domain ID
     * @param verificationMethod the verification method
     * @param token the verification token
     * @return true if domain ownership is verified
     */
    boolean verifyDomainOwnership(String domainId, String verificationMethod, String token);

    /**
     * Gets verification instructions for a domain and method.
     * Provides user-readable instructions for domain verification setup.
     *
     * @param domainName the domain name
     * @param verificationMethod the verification method
     * @param token the verification token
     * @return verification instructions
     */
    String getVerificationInstructions(String domainName, String verificationMethod, String token);

    /**
     * Checks if domain verification has expired.
     * Used by validation layer to validate verification timing.
     *
     * @param domain the domain entity
     * @return true if verification window has expired
     */
    boolean isVerificationExpired(Domain domain);

    /**
     * Validates verification method against domain constraints.
     * Checks if verification method is available for domain/organization type.
     *
     * @param domainName the domain name
     * @param verificationMethod the verification method
     * @param organizationType the organization type
     * @return true if verification method is valid for this context
     */
    boolean isVerificationMethodValid(String domainName, String verificationMethod, String organizationType);

    // ========== DNS VERIFICATION METHODS ==========

    /**
     * Validates DNS TXT record for domain verification.
     * Performs DNS lookup to check if verification record exists.
     *
     * @param domainName the domain name
     * @param expectedToken the expected verification token
     * @return true if DNS record contains the expected token
     */
    boolean validateDnsRecord(String domainName, String expectedToken);

    // ========== EMAIL VERIFICATION METHODS ==========

    /**
     * Sends verification email for domain verification.
     * Sends email to standard admin addresses for the domain.
     *
     * @param domainName the domain name
     * @param token the verification token
     * @param organizationName the organization name
     * @return true if email was sent successfully
     */
    boolean sendVerificationEmail(String domainName, String token, String organizationName);

    // ========== FILE VERIFICATION METHODS ==========

    /**
     * Validates file-based domain verification.
     * Checks if verification file exists at expected URL.
     *
     * @param domainName the domain name
     * @param token the verification token
     * @return true if verification file is accessible and contains correct token
     */
    boolean validateVerificationFile(String domainName, String token);

    // ========== UTILITY METHODS ==========

    /**
     * Gets available verification methods for an organization type.
     * Different organization types may have different available methods.
     *
     * @param organizationType the organization type
     * @return array of available verification methods
     */
    String[] getAvailableVerificationMethods(String organizationType);

    /**
     * Estimates verification completion time for a method.
     * Provides time estimates to users for different verification methods.
     *
     * @param verificationMethod the verification method
     * @return estimated time in minutes
     */
    int getEstimatedVerificationTime(String verificationMethod);
}