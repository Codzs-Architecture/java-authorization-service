package com.codzs.repository.blacklist;

import com.codzs.entity.blacklist.IpBlacklist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for IpBlacklist MongoDB document.
 * Provides methods for managing IP blacklist for device authorization security.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Repository
public interface IpBlacklistRepository extends MongoRepository<IpBlacklist, String> {

    /**
     * Find an active blacklist entry for a specific IP address.
     * 
     * @param ipAddress the IP address to check
     * @return Optional containing the blacklist entry if found and active
     */
    @Query("{ 'ipAddress': ?0, 'isActive': true, $or: [ { 'expiresAt': null }, { 'expiresAt': { $gt: ?1 } } ] }")
    Optional<IpBlacklist> findActiveByIpAddress(String ipAddress, LocalDateTime now);

    /**
     * Find all active blacklist entries that haven't expired.
     * 
     * @param now current timestamp
     * @return list of active blacklist entries
     */
    @Query(value = "{ 'isActive': true, $or: [ { 'expiresAt': null }, { 'expiresAt': { $gt: ?0 } } ] }", sort = "{ 'createdAt': -1 }")
    List<IpBlacklist> findAllActive(LocalDateTime now);

    /**
     * Find all active IP ranges for CIDR matching.
     * 
     * @param now current timestamp
     * @return list of active IP ranges
     */
    @Query(value = "{ 'isActive': true, 'ipRange': { $ne: null }, $or: [ { 'expiresAt': null }, { 'expiresAt': { $gt: ?0 } } ] }", sort = "{ 'createdAt': -1 }")
    List<IpBlacklist> findAllActiveRanges(LocalDateTime now);

    /**
     * Check if an IP address exists in the blacklist (regardless of active status).
     * 
     * @param ipAddress the IP address to check
     * @return true if the IP exists in the blacklist
     */
    boolean existsByIpAddress(String ipAddress);

    /**
     * Find blacklist entries created by a specific user/system.
     * 
     * @param blockedBy who created the blacklist entries
     * @return list of blacklist entries
     */
    List<IpBlacklist> findByBlockedByOrderByCreatedAtDesc(String blockedBy);

    /**
     * Deactivate blacklist entries for a specific IP address.
     * 
     * @param ipAddress the IP address to deactivate
     * @param now current timestamp
     * @return number of entries deactivated
     */
    @Query("{ 'ipAddress': ?0, 'isActive': true }")
    @Update("{ $set: { 'isActive': false, 'updatedAt': ?1 } }")
    long deactivateByIpAddress(String ipAddress, LocalDateTime now);

    /**
     * Clean up expired entries by marking them as inactive.
     * 
     * @param now current timestamp
     * @return number of entries cleaned up
     */
    @Query("{ 'isActive': true, 'expiresAt': { $ne: null, $lte: ?0 } }")
    @Update("{ $set: { 'isActive': false, 'updatedAt': ?0 } }")
    long cleanupExpiredEntries(LocalDateTime now);
}