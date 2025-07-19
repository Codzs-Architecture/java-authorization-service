package com.codzs.repository.blacklist;

import com.codzs.entity.blacklist.IpBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for IpBlacklist entity.
 * Provides methods for managing IP blacklist for device authorization security.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Repository
public interface IpBlacklistRepository extends JpaRepository<IpBlacklist, Long> {

    /**
     * Find an active blacklist entry for a specific IP address.
     * 
     * @param ipAddress the IP address to check
     * @return Optional containing the blacklist entry if found and active
     */
    @Query("SELECT d FROM IpBlacklist d WHERE d.ipAddress = :ipAddress " +
           "AND d.isActive = true " +
           "AND (d.expiresAt IS NULL OR d.expiresAt > :now)")
    Optional<IpBlacklist> findActiveByIpAddress(@Param("ipAddress") String ipAddress, 
                                                       @Param("now") LocalDateTime now);

    /**
     * Find all active blacklist entries that haven't expired.
     * 
     * @param now current timestamp
     * @return list of active blacklist entries
     */
    @Query("SELECT d FROM IpBlacklist d WHERE d.isActive = true " +
           "AND (d.expiresAt IS NULL OR d.expiresAt > :now) " +
           "ORDER BY d.createdAt DESC")
    List<IpBlacklist> findAllActive(@Param("now") LocalDateTime now);

    /**
     * Find all active IP ranges for CIDR matching.
     * 
     * @param now current timestamp
     * @return list of active IP ranges
     */
    @Query("SELECT d FROM IpBlacklist d WHERE d.isActive = true " +
           "AND d.ipRange IS NOT NULL " +
           "AND (d.expiresAt IS NULL OR d.expiresAt > :now) " +
           "ORDER BY d.createdAt DESC")
    List<IpBlacklist> findAllActiveRanges(@Param("now") LocalDateTime now);

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
    @Query("UPDATE IpBlacklist d SET d.isActive = false, d.updatedAt = :now " +
           "WHERE d.ipAddress = :ipAddress AND d.isActive = true")
    int deactivateByIpAddress(@Param("ipAddress") String ipAddress, @Param("now") LocalDateTime now);

    /**
     * Clean up expired entries by marking them as inactive.
     * 
     * @param now current timestamp
     * @return number of entries cleaned up
     */
    @Query("UPDATE IpBlacklist d SET d.isActive = false, d.updatedAt = :now " +
           "WHERE d.isActive = true AND d.expiresAt IS NOT NULL AND d.expiresAt <= :now")
    int cleanupExpiredEntries(@Param("now") LocalDateTime now);
}