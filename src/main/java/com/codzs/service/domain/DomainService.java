package com.codzs.service.domain;

import com.codzs.entity.domain.Domain;

/**
 * Service interface for Domain-related business operations within entities.
 * Manages domain operations as embedded objects within entities
 * with proper business validation and transaction management.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public interface DomainService<T> {

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
     * Checks if verification method is available for domain/entity type.
     *
     * @param domainName the domain name
     * @param verificationMethod the verification method
     * @param entityType the entity type
     * @return true if verification method is valid for this context
     */
    boolean isVerificationMethodValid(String domainName, String verificationMethod, String entityType);

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
     * @param entityName the entity name
     * @return true if email was sent successfully
     */
    boolean sendVerificationEmail(String domainName, String token, String entityName);

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
     * Gets available verification methods for an entity type.
     * Different entity types may have different available methods.
     *
     * @param entityType the entity type
     * @return array of available verification methods
     */
    String[] getAvailableVerificationMethods(String entityType);

    /**
     * Estimates verification completion time for a method.
     * Provides time estimates to users for different verification methods.
     *
     * @param verificationMethod the verification method
     * @return estimated time in minutes
     */
    int getEstimatedVerificationTime(String verificationMethod);
}