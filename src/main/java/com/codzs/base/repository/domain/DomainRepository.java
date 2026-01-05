package com.codzs.base.repository.domain;

import com.codzs.entity.domain.Domain;

import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.time.Instant;

/**
 * Repository interface for Domain MongoDB operations.
 * Provides methods for managing domains as embedded objects within <entity>s.
 * 
 * @author Codzs Team
 * @since 1.0 
 */
// @Repository
public abstract interface DomainRepository<T> extends MongoRepository<T, String> {

    // ========== DOMAIN OPERATIONS ==========

    /**
     * Adds a new domain to an entity.
     * Uses MongoDB $push operator to add the domain to the domains array.
     */
    @Update("{ '$push': { 'domains': ?1 } }")
    @Query("{ '_id': ?0 }")
    void addDomainToEntity(String entityId, Domain domain);

    /**
     * Removes a domain from an entity.
     * Uses MongoDB $pull operator to remove the domain by ID from the domains array.
     */
    @Update("{ '$pull': { 'domains': { 'id': ?1 } } }")
    @Query("{ '_id': ?0 }")
    void removeDomainFromEntity(String entityId, String domainId);

    /**
     * Updates an entire domain in one operation.
     * Uses MongoDB positional operator ($) to update the specific domain in the array.
     */
    @Update("{ '$set': { 'domains.$': ?2 } }")
    @Query("{ '_id': ?0, 'domains.id': ?1 }")
    void updateDomain(String entityId, String domainId, Domain domain);

    /**
     * Updates domain verification status and timestamp.
     * Marks a domain as verified with the current timestamp.
     */
    @Update("{ '$set': { 'domains.$.isVerified': true, 'domains.$.verifiedDate': ?2 } }")
    @Query("{ '_id': ?0, 'domains.id': ?1 }")
    void updateDomainVerificationStatus(String entityId, String domainId, Instant verifiedDate);

    /**
     * Unsets primary status for all domains in an entity.
     * Used before setting a new primary domain.
     */
    @Update("{ '$set': { 'domains.$[].isPrimary': false } }")
    @Query("{ '_id': ?0 }")
    void unsetAllPrimaryDomains(String entityId);

    /**
     * Sets a domain as primary within an entity.
     */
    @Update("{ '$set': { 'domains.$.isPrimary': true } }")
    @Query("{ '_id': ?0, 'domains._id': ?1 }")
    void setPrimaryDomain(String entityId, String domainId);

    /**
     * Updates the verification token for a domain.
     */
    @Update("{ '$set': { 'domains.$.verificationToken': ?2 } }")
    @Query("{ '_id': ?0, 'domains.id': ?1 }")
    void updateDomainVerificationToken(String entityId, String domainId, String newToken);

    /**
     * Checks if a domain name exists globally across all entities, excluding a specific entity.
     * Uses existence check to return true/false properly.
     */
    @CountQuery("{ '_id': { '$ne': ?0 }, 'domains._id': { '$ne': ?1 }, 'domains.name': ?2 }")
    long countByDomainNameGlobally(String excludeEntityId, String domainId, String domainName);

    /**
     * Checks if a domain name exists globally across all entities.
     * Uses existence check to return true/false properly.
     */
    @CountQuery("{ 'domains.name': ?0 }")
    long countByDomainNameGlobally(String domainName);
}