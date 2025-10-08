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
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing ApiWhitelist MongoDB documents.
 * Provides methods for pattern-based IP and endpoint matching.
 *
 * @author Nitin Khaitan
 * @since 1.1
 */
@Repository
public interface ApiWhitelistRepository extends MongoRepository<ApiWhitelist, String> {

    /**
     * Finds all active whitelist entries ordered by priority (lower number = higher priority).
     *
     * @return list of active whitelist entries ordered by priority
     */
    @Query(value = "{ 'isActive': true, $or: [ { 'expiresAt': null }, { 'expiresAt': { $gt: ?0 } } ] }", sort = "{ 'priority': 1, '_id': 1 }")
    List<ApiWhitelist> findActiveWhitelistEntriesOrderedByPriority(LocalDateTime currentTime);

    /**
     * Finds whitelist entries that could match the given IP address.
     * This includes exact IP matches, CIDR ranges, and pattern matches.
     *
     * @param ipAddress the IP address to check
     * @param currentTime current timestamp for expiry check
     * @return list of potentially matching whitelist entries
     */
    @Query(value = "{ 'isActive': true, $or: [ { 'expiresAt': null }, { 'expiresAt': { $gt: ?1 } } ], $or: [ { 'ipAddress': ?0 }, { 'ipRange': { $ne: null } }, { 'ipPattern': { $ne: null } }, { $and: [ { 'ipAddress': null }, { 'ipRange': null }, { 'ipPattern': null } ] } ] }", sort = "{ 'priority': 1, '_id': 1 }")
    List<ApiWhitelist> findPotentialMatches(String ipAddress, LocalDateTime currentTime);

    /**
     * Finds whitelist entries for a specific endpoint pattern.
     *
     * @param endpointPattern the endpoint pattern to match
     * @param currentTime current timestamp for expiry check
     * @return list of whitelist entries for the endpoint pattern
     */
    @Query(value = "{ 'isActive': true, $or: [ { 'expiresAt': null }, { 'expiresAt': { $gt: ?1 } } ], $or: [ { 'endpointPattern': ?0 }, { 'endpointPattern': null } ] }", sort = "{ 'priority': 1, '_id': 1 }")
    List<ApiWhitelist> findByEndpointPattern(String endpointPattern, LocalDateTime currentTime);

    /**
     * Finds whitelist entries for a specific client ID.
     *
     * @param clientId the client ID to match
     * @param currentTime current timestamp for expiry check
     * @return list of whitelist entries for the client ID
     */
    @Query(value = "{ 'isActive': true, $or: [ { 'expiresAt': null }, { 'expiresAt': { $gt: ?1 } } ], $or: [ { 'clientId': ?0 }, { 'clientId': null } ] }", sort = "{ 'priority': 1, '_id': 1 }")
    List<ApiWhitelist> findByClientId(String clientId, LocalDateTime currentTime);

    /**
     * Finds whitelist entries that match the given IP, endpoint, and client criteria.
     *
     * @param ipAddress the IP address to check
     * @param endpoint the endpoint being accessed
     * @param clientId the client ID (can be null)
     * @param currentTime current timestamp for expiry check
     * @return list of matching whitelist entries ordered by priority
     */
    @Query(value = "{ 'isActive': true, $or: [ { 'expiresAt': null }, { 'expiresAt': { $gt: ?3 } } ], $or: [ { 'ipAddress': ?0 }, { 'ipRange': { $ne: null } }, { 'ipPattern': { $ne: null } } ], $or: [ { 'endpointPattern': null }, { 'endpointPattern': '' }, { 'endpointPattern': { $regex: ?1 } } ], $or: [ { 'clientId': null }, { 'clientId': ?2 } ] }", sort = "{ 'priority': 1, '_id': 1 }")
    List<ApiWhitelist> findMatchingEntries(String ipAddress, String endpoint, String clientId, LocalDateTime currentTime);

    /**
     * Finds expired whitelist entries.
     *
     * @param currentTime current timestamp
     * @return list of expired whitelist entries
     */
    @Query("{ 'expiresAt': { $ne: null, $lte: ?0 } }")
    List<ApiWhitelist> findExpiredEntries(LocalDateTime currentTime);

    /**
     * Counts active whitelist entries.
     *
     * @param currentTime current timestamp for expiry check
     * @return count of active entries
     */
    @Query(value = "{ 'isActive': true, $or: [ { 'expiresAt': null }, { 'expiresAt': { $gt: ?0 } } ] }", count = true)
    long countActiveEntries(LocalDateTime currentTime);

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