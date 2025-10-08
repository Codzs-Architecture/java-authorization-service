package com.codzs.repository.blacklist;

import com.codzs.entity.blacklist.ApiAccessAttemptLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for ApiAccessAttemptLog MongoDB document.
 * Provides methods for logging and analyzing API access attempts.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Repository
public interface ApiAccessAttemptLogRepository extends MongoRepository<ApiAccessAttemptLog, String> {

    /**
     * Find recent attempts from a specific IP address.
     * 
     * @param ipAddress the IP address to search for
     * @param since timestamp to search from
     * @return list of recent attempts from the IP
     */
    @Query(value = "{ 'ipAddress': ?0, 'attemptedAt': { $gte: ?1 } }", sort = "{ 'attemptedAt': -1 }")
    List<ApiAccessAttemptLog> findRecentAttemptsByIp(String ipAddress, LocalDateTime since);

    /**
     * Count blocked attempts from a specific IP within a timeframe.
     * 
     * @param ipAddress the IP address
     * @param since timestamp to count from
     * @return count of blocked attempts
     */
    @Query(value = "{ 'ipAddress': ?0, 'attemptedAt': { $gte: ?1 }, 'requestResult': { $ne: 'ALLOWED' } }", count = true)
    long countBlockedAttemptsByIp(String ipAddress, LocalDateTime since);

    /**
     * Find all blocked attempts within a timeframe for security analysis.
     * 
     * @param since timestamp to search from
     * @return list of blocked attempts
     */
    @Query(value = "{ 'attemptedAt': { $gte: ?0 }, 'requestResult': { $ne: 'ALLOWED' } }", sort = "{ 'attemptedAt': -1 }")
    List<ApiAccessAttemptLog> findRecentBlockedAttempts(LocalDateTime since);

    /**
     * Find most active IPs by attempt count within a timeframe.
     * 
     * @param since timestamp to search from
     * @param limit maximum number of results
     * @return list of IP addresses with attempt counts
     */
    @Aggregation(pipeline = {
        "{ $match: { 'attemptedAt': { $gte: ?0 } } }",
        "{ $group: { _id: '$ipAddress', attemptCount: { $sum: 1 } } }",
        "{ $sort: { attemptCount: -1 } }",
        "{ $limit: ?1 }"
    })
    List<Object> findMostActiveIps(LocalDateTime since, int limit);

    /**
     * Clean up old log entries to prevent database bloat.
     * 
     * @param olderThan timestamp - entries older than this will be deleted
     * @return number of entries deleted
     */
    long deleteByAttemptedAtBefore(LocalDateTime olderThan);
}