package com.codzs.repository.blacklist;

import com.codzs.entity.blacklist.ApiAccessAttemptLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for ApiAccessAttemptLog entity.
 * Provides methods for logging and analyzing API access attempts.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Repository
public interface ApiAccessAttemptLogRepository extends JpaRepository<ApiAccessAttemptLog, Long> {

    /**
     * Find recent attempts from a specific IP address.
     * 
     * @param ipAddress the IP address to search for
     * @param since timestamp to search from
     * @return list of recent attempts from the IP
     */
    @Query("SELECT d FROM ApiAccessAttemptLog d WHERE d.ipAddress = :ipAddress " +
           "AND d.attemptedAt >= :since ORDER BY d.attemptedAt DESC")
    List<ApiAccessAttemptLog> findRecentAttemptsByIp(@Param("ipAddress") String ipAddress, 
                                                                @Param("since") LocalDateTime since);

    /**
     * Count blocked attempts from a specific IP within a timeframe.
     * 
     * @param ipAddress the IP address
     * @param since timestamp to count from
     * @return count of blocked attempts
     */
    @Query("SELECT COUNT(d) FROM ApiAccessAttemptLog d WHERE d.ipAddress = :ipAddress " +
           "AND d.attemptedAt >= :since " +
           "AND d.requestResult != com.codzs.entity.blacklist.ApiAccessAttemptLog.RequestResult.ALLOWED")
    long countBlockedAttemptsByIp(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);

    /**
     * Find all blocked attempts within a timeframe for security analysis.
     * 
     * @param since timestamp to search from
     * @return list of blocked attempts
     */
    @Query("SELECT d FROM ApiAccessAttemptLog d WHERE d.attemptedAt >= :since " +
           "AND d.requestResult != com.codzs.entity.blacklist.ApiAccessAttemptLog.RequestResult.ALLOWED " +
           "ORDER BY d.attemptedAt DESC")
    List<ApiAccessAttemptLog> findRecentBlockedAttempts(@Param("since") LocalDateTime since);

    /**
     * Find most active IPs by attempt count within a timeframe.
     * 
     * @param since timestamp to search from
     * @param limit maximum number of results
     * @return list of IP addresses with attempt counts
     */
    @Query("SELECT d.ipAddress, COUNT(d) as attemptCount FROM ApiAccessAttemptLog d " +
           "WHERE d.attemptedAt >= :since " +
           "GROUP BY d.ipAddress " +
           "ORDER BY attemptCount DESC")
    List<Object[]> findMostActiveIps(@Param("since") LocalDateTime since, @Param("limit") int limit);

    /**
     * Clean up old log entries to prevent database bloat.
     * 
     * @param olderThan timestamp - entries older than this will be deleted
     * @return number of entries deleted
     */
    @Query("DELETE FROM ApiAccessAttemptLog d WHERE d.attemptedAt < :olderThan")
    int cleanupOldEntries(@Param("olderThan") LocalDateTime olderThan);
}