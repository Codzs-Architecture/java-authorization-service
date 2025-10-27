package com.codzs.service.organization;

import com.codzs.entity.domain.Domain;
import com.codzs.entity.organization.Organization;
import com.codzs.service.domain.DomainService;

import java.util.List;

/**
 * Service interface for Domain-related business operations within organizations.
 * Manages domain operations as embedded objects within organizations
 * with proper business validation and transaction management.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public interface OrganizationDomainService extends DomainService<Organization> {

    // ========== API FLOW METHODS ==========

    /**
     * Adds a domain to an organization.
     * API: POST /api/v1/organizations/{id}/domains
     *
     * @param organizationId the organization ID
     * @param domain the domain entity to add
     * @return list of all domains in the organization after addition
     */
    List<Domain> addDomainToOrganization(String organizationId, Domain domain);

    /**
     * Updates a domain within an organization.
     * API: PUT /api/v1/organizations/{id}/domains/{domainId}
     *
     * @param organizationId the organization ID
     * @param domain the domain entity with updates
     * @return list of all domains in the organization after update
     */
    List<Domain> updateDomainInOrganization(String organizationId, Domain domain);

    /**
     * Removes a domain from an organization.
     * API: DELETE /api/v1/organizations/{id}/domains/{domainId}
     *
     * @param organizationId the organization ID
     * @param domainId the domain ID to remove
     * @return list of all domains in the organization after removal
     */
    List<Domain> removeDomainFromOrganization(String organizationId, String domainId);

    /**
     * Verifies a domain within an organization.
     * API: PUT /api/v1/organizations/{id}/domains/{domainId}/verify
     *
     * @param organizationId the organization ID
     * @param domainId the domain ID to verify
     * @param verificationMethod the verification method used
     * @param verificationToken the verification token (optional)
     * @return the verified domain entity
     */
    Domain verifyDomainInOrganization(String organizationId, String domainId, 
                                          String verificationMethod, String verificationToken);

    /**
     * Sets a domain as primary within an organization.
     * API: PUT /api/v1/organizations/{id}/domains/{domainId}/set-primary
     *
     * @param organizationId the organization ID
     * @param domainId the domain ID to set as primary
     * @return list of all domains in the organization after setting primary
     */
    List<Domain> setPrimaryDomain(String organizationId, String domainId);

    /**
     * Gets all domains for an organization.
     * API: GET /api/v1/organizations/{id}/domains
     *
     * @param organizationId the organization ID
     * @return list of domain entities
     */
    List<Domain> getDomainsForEntity(String organizationId);

    /**
     * Gets a specific domain within an organization.
     * API: GET /api/v1/organizations/{id}/domains/{domainId}
     *
     * @param organizationId the organization ID
     * @param domainId the domain ID
     * @return the domain entity or null if not found
     */
    Domain getDomainInEntity(String organizationId, String domainId);

    /**
     * Regenerates verification token for a domain.
     * API: POST /api/v1/organizations/{id}/domains/{domainId}/regenerate-token
     *
     * @param organizationId the organization ID
     * @param domainId the domain ID
     * @return the domain entity with new verification token
     */
    Domain regenerateDomainVerificationToken(String organizationId, String domainId);

    /**
     * Gets verification instructions for a domain.
     * API: GET /api/v1/organizations/{id}/domains/{domainId}/verification-instructions
     *
     * @param organizationId the organization ID
     * @param domainId the domain ID
     * @return verification instructions as string
     */
    String getDomainVerificationInstructions(String organizationId, String domainId);

    // ========== ORGANIZATION DOMAIN VALIDATION METHODS ==========

    /**
     * Checks if an organization has users in a specific domain.
     * Used by domain validation layer before allowing domain removal.
     *
     * @param organizationId the organization ID
     * @param domainName the domain name to check
     * @return true if domain has active users
     */
    boolean hasUsersInDomain(String organizationId, String domainName);

    /**
     * Gets user count for a specific domain in an organization.
     * Used by validation layer to check domain usage impact.
     *
     * @param organizationId the organization ID
     * @param domainName the domain name
     * @return number of users in the domain
     */
    int getUserCountByDomain(String organizationId, String domainName);

    // ========== UTILITY METHODS ==========

    /**
     * Gets primary domain for an organization.
     *
     * @param organizationId the organization ID
     * @return the primary domain entity or null if none
     */
    Domain getPrimaryDomainForEntity(String organizationId);

    /**
     * Checks if organization has verified domains.
     *
     * @param organizationId the organization ID
     * @return true if organization has at least one verified domain
     */
    boolean hasVerifiedDomains(String organizationId);

}