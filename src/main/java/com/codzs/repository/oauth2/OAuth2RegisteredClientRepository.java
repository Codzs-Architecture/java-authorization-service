package com.codzs.repository.oauth2;

import com.codzs.entity.oauth2.OAuth2RegisteredClient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for OAuth2RegisteredClient MongoDB documents.
 * Provides methods for managing OAuth2 client registrations.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Repository
public interface OAuth2RegisteredClientRepository extends MongoRepository<OAuth2RegisteredClient, String> {

    /**
     * Find a registered client by client ID.
     * 
     * @param clientId the client ID
     * @return Optional containing the registered client if found
     */
    Optional<OAuth2RegisteredClient> findByClientId(String clientId);

    /**
     * Check if a client ID exists.
     * 
     * @param clientId the client ID to check
     * @return true if the client ID exists
     */
    boolean existsByClientId(String clientId);

    /**
     * Find all active registered clients (those without expired client secrets).
     * 
     * @param now current timestamp
     * @return list of active registered clients
     */
    @Query("{ $or: [ { 'clientSecretExpiresAt': null }, { 'clientSecretExpiresAt': { $gt: ?0 } } ] }")
    List<OAuth2RegisteredClient> findActiveClients(LocalDateTime now);

    /**
     * Find registered clients by client name (case-insensitive partial match).
     * 
     * @param clientName the client name to search for
     * @return list of matching registered clients
     */
    @Query("{ 'clientName': { $regex: ?0, $options: 'i' } }")
    List<OAuth2RegisteredClient> findByClientNameContainingIgnoreCase(String clientName);

    /**
     * Find expired client secrets for cleanup.
     * 
     * @param now current timestamp
     * @return list of clients with expired secrets
     */
    @Query("{ 'clientSecretExpiresAt': { $ne: null, $lte: ?0 } }")
    List<OAuth2RegisteredClient> findExpiredSecrets(LocalDateTime now);

    /**
     * Find clients by authorization grant type.
     * 
     * @param grantType the grant type to search for
     * @return list of clients supporting the grant type
     */
    @Query("{ 'authorizationGrantTypes': { $regex: ?0 } }")
    List<OAuth2RegisteredClient> findByAuthorizationGrantTypesContaining(String grantType);

    /**
     * Find clients by scope.
     * 
     * @param scope the scope to search for
     * @return list of clients with the scope
     */
    @Query("{ 'scopes': { $regex: ?0 } }")
    List<OAuth2RegisteredClient> findByScopesContaining(String scope);
}