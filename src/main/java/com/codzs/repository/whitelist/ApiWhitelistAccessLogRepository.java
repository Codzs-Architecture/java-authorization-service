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

import com.codzs.entity.whitelist.ApiWhitelistAccessLog;
import com.codzs.entity.whitelist.ApiWhitelistAccessLog.RequestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing ApiWhitelistAccessLog entities.
 * Provides methods for querying whitelist access attempts and security analysis.
 *
 * @author Nitin Khaitan
 * @since 1.1
 */
@Repository
public interface ApiWhitelistAccessLogRepository extends JpaRepository<ApiWhitelistAccessLog, Long> {

    /**
     * Finds access log entries for a specific IP address within a time range.
     *
     * @param ipAddress the IP address to search for
     * @param startTime start of the time range
     * @param endTime end of the time range
     * @return list of access log entries for the IP address
     */
    List<ApiWhitelistAccessLog> findByIpAddressAndAttemptedAtBetween(String ipAddress, 
                                                                   LocalDateTime startTime, 
                                                                   LocalDateTime endTime);

    /**
     * Finds access log entries by request result within a time range.
     *
     * @param requestResult the request result to filter by
     * @param startTime start of the time range
     * @param endTime end of the time range
     * @return list of access log entries with the specified result
     */
    List<ApiWhitelistAccessLog> findByRequestResultAndAttemptedAtBetween(RequestResult requestResult,
                                                                        LocalDateTime startTime,
                                                                        LocalDateTime endTime);

    /**
     * Finds blocked access attempts within a time range.
     *
     * @param startTime start of the time range
     * @param endTime end of the time range
     * @return list of blocked access attempts
     */
    @Query("""
        SELECT w FROM ApiWhitelistAccessLog w 
        WHERE w.requestResult = 'BLOCKED_NOT_WHITELISTED' 
        AND w.attemptedAt BETWEEN :startTime AND :endTime
        ORDER BY w.attemptedAt DESC
        """)
    List<ApiWhitelistAccessLog> findBlockedAttempts(@Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);

    /**
     * Finds allowed access attempts within a time range.
     *
     * @param startTime start of the time range
     * @param endTime end of the time range
     * @return list of allowed access attempts
     */
    @Query("""
        SELECT w FROM ApiWhitelistAccessLog w 
        WHERE w.requestResult = 'ALLOWED_WHITELIST' 
        AND w.attemptedAt BETWEEN :startTime AND :endTime
        ORDER BY w.attemptedAt DESC
        """)
    List<ApiWhitelistAccessLog> findAllowedAttempts(@Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);

    /**
     * Counts access attempts by IP address within a time range.
     *
     * @param ipAddress the IP address to count attempts for
     * @param startTime start of the time range
     * @param endTime end of the time range
     * @return count of access attempts from the IP address
     */
    @Query("""
        SELECT COUNT(w) FROM ApiWhitelistAccessLog w 
        WHERE w.ipAddress = :ipAddress 
        AND w.attemptedAt BETWEEN :startTime AND :endTime
        """)
    long countAttemptsByIpAddress(@Param("ipAddress") String ipAddress,
                                @Param("startTime") LocalDateTime startTime,
                                @Param("endTime") LocalDateTime endTime);

    /**
     * Counts blocked attempts by IP address within a time range.
     *
     * @param ipAddress the IP address to count blocked attempts for
     * @param startTime start of the time range
     * @param endTime end of the time range
     * @return count of blocked attempts from the IP address
     */
    @Query("""
        SELECT COUNT(w) FROM ApiWhitelistAccessLog w 
        WHERE w.ipAddress = :ipAddress 
        AND w.requestResult = 'BLOCKED_NOT_WHITELISTED'
        AND w.attemptedAt BETWEEN :startTime AND :endTime
        """)
    long countBlockedAttemptsByIpAddress(@Param("ipAddress") String ipAddress,
                                       @Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime);

    /**
     * Finds access log entries for a specific endpoint within a time range.
     *
     * @param endpoint the endpoint to search for
     * @param startTime start of the time range
     * @param endTime end of the time range
     * @return list of access log entries for the endpoint
     */
    List<ApiWhitelistAccessLog> findByEndpointAndAttemptedAtBetween(String endpoint,
                                                                  LocalDateTime startTime,
                                                                  LocalDateTime endTime);

    /**
     * Finds access log entries for a specific whitelist rule within a time range.
     *
     * @param whitelistRuleId the whitelist rule ID
     * @param startTime start of the time range
     * @param endTime end of the time range
     * @return list of access log entries for the whitelist rule
     */
    List<ApiWhitelistAccessLog> findByWhitelistRuleIdAndAttemptedAtBetween(Long whitelistRuleId,
                                                                          LocalDateTime startTime,
                                                                          LocalDateTime endTime);

    /**
     * Finds the most recent access attempts from suspicious IPs (multiple blocked attempts).
     *
     * @param minBlockedAttempts minimum number of blocked attempts to consider suspicious
     * @param timeWindowHours time window in hours to look back
     * @return list of recent access attempts from suspicious IPs
     */
    @Query("""
        SELECT w FROM ApiWhitelistAccessLog w 
        WHERE w.ipAddress IN (
            SELECT w2.ipAddress FROM ApiWhitelistAccessLog w2 
            WHERE w2.requestResult = 'BLOCKED_NOT_WHITELISTED' 
            AND w2.attemptedAt >= :startTime
            GROUP BY w2.ipAddress 
            HAVING COUNT(w2.id) >= :minBlockedAttempts
        )
        AND w.attemptedAt >= :startTime
        ORDER BY w.attemptedAt DESC
        """)
    List<ApiWhitelistAccessLog> findSuspiciousIpActivity(@Param("minBlockedAttempts") long minBlockedAttempts,
                                                        @Param("startTime") LocalDateTime startTime);

    /**
     * Gets statistics for access attempts within a time range.
     *
     * @param startTime start of the time range
     * @param endTime end of the time range
     * @return list of request result counts
     */
    @Query("""
        SELECT w.requestResult, COUNT(w) 
        FROM ApiWhitelistAccessLog w 
        WHERE w.attemptedAt BETWEEN :startTime AND :endTime
        GROUP BY w.requestResult
        """)
    List<Object[]> getAccessStatistics(@Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);

    /**
     * Finds access log entries for a specific client ID within a time range.
     *
     * @param clientId the client ID to search for
     * @param startTime start of the time range
     * @param endTime end of the time range
     * @return list of access log entries for the client ID
     */
    List<ApiWhitelistAccessLog> findByClientIdAndAttemptedAtBetween(String clientId,
                                                                  LocalDateTime startTime,
                                                                  LocalDateTime endTime);

    /**
     * Deletes old log entries before a specified date.
     *
     * @param beforeDate the cutoff date for deletion
     * @return number of deleted entries
     */
    @Query("""
        DELETE FROM ApiWhitelistAccessLog w 
        WHERE w.attemptedAt < :beforeDate
        """)
    int deleteOldEntries(@Param("beforeDate") LocalDateTime beforeDate);

    /**
     * Finds the latest access attempts ordered by time.
     *
     * @param limit maximum number of entries to return
     * @return list of latest access attempts
     */
    @Query("""
        SELECT w FROM ApiWhitelistAccessLog w 
        ORDER BY w.attemptedAt DESC
        LIMIT :limit
        """)
    List<ApiWhitelistAccessLog> findLatestAttempts(@Param("limit") int limit);
}