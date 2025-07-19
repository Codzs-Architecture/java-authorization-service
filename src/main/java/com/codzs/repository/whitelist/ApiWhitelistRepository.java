/*
 * Copyright 2020-2024 Nitin Khaitan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codzs.repository.whitelist;

import com.codzs.entity.whitelist.ApiWhitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing ApiWhitelist entities.
 * Provides methods for pattern-based IP and endpoint matching.
 *
 * @author Nitin Khaitan
 * @since 1.1
 */
@Repository
public interface ApiWhitelistRepository extends JpaRepository<ApiWhitelist, Long> {

    /**
     * Finds all active whitelist entries ordered by priority (lower number = higher priority).
     *
     * @return list of active whitelist entries ordered by priority
     */
    @Query("""
        SELECT w FROM ApiWhitelist w 
        WHERE w.isActive = true 
        AND (w.expiresAt IS NULL OR w.expiresAt > :currentTime)
        ORDER BY w.priority ASC, w.id ASC
        """)
    List<ApiWhitelist> findActiveWhitelistEntriesOrderedByPriority(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Finds whitelist entries that could match the given IP address.
     * This includes exact IP matches, CIDR ranges, and pattern matches.
     *
     * @param ipAddress the IP address to check
     * @param currentTime current timestamp for expiry check
     * @return list of potentially matching whitelist entries
     */
    @Query("""
        SELECT w FROM ApiWhitelist w 
        WHERE w.isActive = true 
        AND (w.expiresAt IS NULL OR w.expiresAt > :currentTime)
        AND (
            w.ipAddress = :ipAddress
            OR w.ipRange IS NOT NULL
            OR w.ipPattern IS NOT NULL
            OR (w.ipAddress IS NULL AND w.ipRange IS NULL AND w.ipPattern IS NULL)
        )
        ORDER BY w.priority ASC, w.id ASC
        """)
    List<ApiWhitelist> findPotentialMatches(@Param("ipAddress") String ipAddress, 
                                            @Param("currentTime") LocalDateTime currentTime);

    /**
     * Finds whitelist entries for a specific endpoint pattern.
     *
     * @param endpointPattern the endpoint pattern to match
     * @param currentTime current timestamp for expiry check
     * @return list of whitelist entries for the endpoint pattern
     */
    @Query("""
        SELECT w FROM ApiWhitelist w 
        WHERE w.isActive = true 
        AND (w.expiresAt IS NULL OR w.expiresAt > :currentTime)
        AND (w.endpointPattern = :endpointPattern OR w.endpointPattern IS NULL)
        ORDER BY w.priority ASC, w.id ASC
        """)
    List<ApiWhitelist> findByEndpointPattern(@Param("endpointPattern") String endpointPattern, 
                                             @Param("currentTime") LocalDateTime currentTime);

    /**
     * Finds whitelist entries for a specific client ID.
     *
     * @param clientId the client ID to match
     * @param currentTime current timestamp for expiry check
     * @return list of whitelist entries for the client ID
     */
    @Query("""
        SELECT w FROM ApiWhitelist w 
        WHERE w.isActive = true 
        AND (w.expiresAt IS NULL OR w.expiresAt > :currentTime)
        AND (w.clientId = :clientId OR w.clientId IS NULL)
        ORDER BY w.priority ASC, w.id ASC
        """)
    List<ApiWhitelist> findByClientId(@Param("clientId") String clientId, 
                                      @Param("currentTime") LocalDateTime currentTime);

    /**
     * Finds whitelist entries that match the given IP, endpoint, and client criteria.
     *
     * @param ipAddress the IP address to check
     * @param endpoint the endpoint being accessed
     * @param clientId the client ID (can be null)
     * @param currentTime current timestamp for expiry check
     * @return list of matching whitelist entries ordered by priority
     */
    @Query("""
        SELECT w FROM ApiWhitelist w 
        WHERE w.isActive = true 
        AND (w.expiresAt IS NULL OR w.expiresAt > :currentTime)
        AND (
            w.ipAddress = :ipAddress
            OR w.ipRange IS NOT NULL
            OR w.ipPattern IS NOT NULL
        )
        AND (w.endpointPattern IS NULL OR w.endpointPattern = '' OR :endpoint LIKE w.endpointPattern)
        AND (w.clientId IS NULL OR w.clientId = :clientId)
        ORDER BY w.priority ASC, w.id ASC
        """)
    List<ApiWhitelist> findMatchingEntries(@Param("ipAddress") String ipAddress,
                                           @Param("endpoint") String endpoint,
                                           @Param("clientId") String clientId,
                                           @Param("currentTime") LocalDateTime currentTime);

    /**
     * Finds expired whitelist entries.
     *
     * @param currentTime current timestamp
     * @return list of expired whitelist entries
     */
    @Query("""
        SELECT w FROM ApiWhitelist w 
        WHERE w.expiresAt IS NOT NULL 
        AND w.expiresAt <= :currentTime
        """)
    List<ApiWhitelist> findExpiredEntries(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Counts active whitelist entries.
     *
     * @param currentTime current timestamp for expiry check
     * @return count of active entries
     */
    @Query("""
        SELECT COUNT(w) FROM ApiWhitelist w 
        WHERE w.isActive = true 
        AND (w.expiresAt IS NULL OR w.expiresAt > :currentTime)
        """)
    long countActiveEntries(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Finds whitelist entries by IP address (exact match).
     *
     * @param ipAddress the IP address to find
     * @return list of whitelist entries with the exact IP address
     */
    List<ApiWhitelist> findByIpAddress(String ipAddress);

    /**
     * Finds whitelist entries by IP range (exact match).
     *
     * @param ipRange the IP range to find
     * @return list of whitelist entries with the exact IP range
     */
    List<ApiWhitelist> findByIpRange(String ipRange);

    /**
     * Finds all active whitelist entries.
     *
     * @return list of active whitelist entries
     */
    List<ApiWhitelist> findByIsActiveTrue();

    /**
     * Finds whitelist entries created within a time range.
     *
     * @param startTime start of the time range
     * @param endTime end of the time range
     * @return list of whitelist entries created within the time range
     */
    List<ApiWhitelist> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
}