package com.codzs.repository.oauth2;

import com.codzs.entity.oauth2.OAuth2AuthorizationConsent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for OAuth2AuthorizationConsent MongoDB documents.
 * Provides methods for managing OAuth2 authorization consent.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Repository
public interface OAuth2AuthorizationConsentRepository extends MongoRepository<OAuth2AuthorizationConsent, String> {

    /**
     * Find authorization consent by registered client ID and principal name.
     * 
     * @param registeredClientId the registered client ID
     * @param principalName the principal name
     * @return Optional containing the authorization consent if found
     */
    Optional<OAuth2AuthorizationConsent> findByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);

    /**
     * Find all consents for a specific registered client.
     * 
     * @param registeredClientId the registered client ID
     * @return list of authorization consents for the client
     */
    List<OAuth2AuthorizationConsent> findByRegisteredClientId(String registeredClientId);

    /**
     * Find all consents for a specific principal (user).
     * 
     * @param principalName the principal name
     * @return list of authorization consents for the principal
     */
    List<OAuth2AuthorizationConsent> findByPrincipalName(String principalName);

    /**
     * Check if consent exists for a client and principal.
     * 
     * @param registeredClientId the registered client ID
     * @param principalName the principal name
     * @return true if consent exists
     */
    boolean existsByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);

    /**
     * Delete consent by registered client ID and principal name.
     * 
     * @param registeredClientId the registered client ID
     * @param principalName the principal name
     * @return number of deleted records
     */
    long deleteByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);

    /**
     * Find consents by authority.
     * 
     * @param authority the authority to search for
     * @return list of consents containing the authority
     */
    @Query("{ 'authorities': { $regex: ?0 } }")
    List<OAuth2AuthorizationConsent> findByAuthoritiesContaining(String authority);

    /**
     * Delete all consents for a specific client.
     * 
     * @param registeredClientId the registered client ID
     * @return number of deleted records
     */
    long deleteByRegisteredClientId(String registeredClientId);

    /**
     * Delete all consents for a specific principal.
     * 
     * @param principalName the principal name
     * @return number of deleted records
     */
    long deleteByPrincipalName(String principalName);
}