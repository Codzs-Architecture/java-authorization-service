package com.codzs.service.blacklist;

import com.codzs.entity.blacklist.ApiAccessAttemptLog;
import com.codzs.entity.blacklist.IpBlacklist;
import com.codzs.repository.blacklist.ApiAccessAttemptLogRepository;
import com.codzs.repository.blacklist.IpBlacklistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.codzs.security.constant.IPConstant;

/**
 * Service class for managing device IP blacklist functionality.
 * Provides methods for checking, adding, and managing IP blacklist entries.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Service
@Transactional
public class IpBlacklistService {

    private static final Logger logger = LoggerFactory.getLogger(IpBlacklistService.class);
    

    @Autowired
    private IpBlacklistRepository blacklistRepository;

    @Autowired
    private ApiAccessAttemptLogRepository attemptLogRepository;

    /**
     * Check if an IP address is blacklisted.
     * 
     * @param ipAddress the IP address to check
     * @return true if the IP is blacklisted and the entry is active
     */
    @Transactional(readOnly = true)
    public boolean isIpBlacklisted(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        
        // Check direct IP match
        Optional<IpBlacklist> directMatch = blacklistRepository.findActiveByIpAddress(ipAddress, now);
        if (directMatch.isPresent()) {
            logger.debug("IP {} is directly blacklisted: {}", ipAddress, directMatch.get().getReason());
            return true;
        }

        // Check IP range matches
        List<IpBlacklist> activeRanges = blacklistRepository.findAllActiveRanges(now);
        for (IpBlacklist rangeEntry : activeRanges) {
            if (isIpInRange(ipAddress, rangeEntry.getIpRange())) {
                logger.debug("IP {} matches blacklisted range {}: {}", 
                           ipAddress, rangeEntry.getIpRange(), rangeEntry.getReason());
                return true;
            }
        }

        return false;
    }

    /**
     * Add an IP address to the blacklist.
     * 
     * @param ipAddress the IP address to blacklist
     * @param reason the reason for blacklisting
     * @param blockedBy who is adding the blacklist entry
     * @return the created blacklist entry
     */
    public IpBlacklist addToBlacklist(String ipAddress, String reason, String blockedBy) {
        return addToBlacklist(ipAddress, null, reason, blockedBy, null);
    }

    /**
     * Add an IP address or range to the blacklist.
     * 
     * @param ipAddress the IP address
     * @param ipRange the IP range in CIDR notation (optional)
     * @param reason the reason for blacklisting
     * @param blockedBy who is adding the blacklist entry
     * @param expiresAt when the blacklist entry expires (null for permanent)
     * @return the created blacklist entry
     */
    public IpBlacklist addToBlacklist(String ipAddress, String ipRange, String reason, 
                                           String blockedBy, LocalDateTime expiresAt) {
        
        if (!isValidIpAddress(ipAddress)) {
            throw new IllegalArgumentException("Invalid IP address: " + ipAddress);
        }

        if (ipRange != null && !isValidCidrRange(ipRange)) {
            throw new IllegalArgumentException("Invalid CIDR range: " + ipRange);
        }

        // Check if IP already exists
        if (blacklistRepository.existsByIpAddress(ipAddress)) {
            logger.warn("IP {} is already in blacklist", ipAddress);
            throw new IllegalStateException("IP address is already blacklisted: " + ipAddress);
        }

        IpBlacklist blacklistEntry = new IpBlacklist(ipAddress, ipRange, reason, blockedBy);
        blacklistEntry.setExpiresAt(expiresAt);

        IpBlacklist saved = blacklistRepository.save(blacklistEntry);
        
        logger.info("Added IP {} to blacklist. Reason: {}. Blocked by: {}. Expires: {}", 
                   ipAddress, reason, blockedBy, expiresAt);

        return saved;
    }

    /**
     * Remove an IP address from the blacklist by marking it as inactive.
     * 
     * @param ipAddress the IP address to remove
     * @return true if the IP was successfully removed
     */
    public boolean removeFromBlacklist(String ipAddress) {
        long updated = blacklistRepository.deactivateByIpAddress(ipAddress, LocalDateTime.now());
        
        if (updated > 0) {
            logger.info("Removed IP {} from blacklist", ipAddress);
            return true;
        }
        
        logger.warn("Attempted to remove IP {} from blacklist but no active entry found", ipAddress);
        return false;
    }

    /**
     * Log a device authorization attempt.
     * 
     * @param ipAddress the client IP address
     * @param userAgent the user agent string
     * @param endpoint the endpoint being accessed
     * @param httpMethod the HTTP method
     * @param clientId the OAuth2 client ID (if available)
     * @param userCode the device user code (if available)
     * @param result the result of the attempt
     * @param blockReason the reason for blocking (if blocked)
     * @param sessionId the session ID (if available)
     */
    public void logAttempt(String ipAddress, String userAgent, String endpoint, String httpMethod,
                          String clientId, String userCode, ApiAccessAttemptLog.RequestResult result,
                          String blockReason, String sessionId) {
        
        ApiAccessAttemptLog logEntry = ApiAccessAttemptLog.builder()
            .ipAddress(ipAddress)
            .userAgent(userAgent)
            .endpoint(endpoint)
            .httpMethod(httpMethod)
            .clientId(clientId)
            .userCode(userCode)
            .requestResult(result)
            .blockReason(blockReason)
            .sessionId(sessionId);

        attemptLogRepository.save(logEntry);
        
        if (result != ApiAccessAttemptLog.RequestResult.ALLOWED) {
            logger.warn("SECURITY: Blocked device authorization attempt from IP {}. " +
                       "Endpoint: {}, Method: {}, Reason: {}", 
                       ipAddress, endpoint, httpMethod, blockReason);
        } else {
            logger.debug("Allowed device authorization attempt from IP {}. " +
                        "Endpoint: {}, Method: {}", 
                        ipAddress, endpoint, httpMethod);
        }
    }

    /**
     * Get recent blocked attempts for analysis.
     * 
     * @param hours number of hours to look back
     * @return list of recent blocked attempts
     */
    @Transactional(readOnly = true)
    public List<ApiAccessAttemptLog> getRecentBlockedAttempts(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return attemptLogRepository.findRecentBlockedAttempts(since);
    }

    /**
     * Clean up expired blacklist entries.
     * 
     * @return number of entries cleaned up
     */
    public long cleanupExpiredEntries() {
        long cleaned = blacklistRepository.cleanupExpiredEntries(LocalDateTime.now());
        if (cleaned > 0) {
            logger.info("Cleaned up {} expired blacklist entries", cleaned);
        }
        return cleaned;
    }

    /**
     * Clean up old attempt logs to prevent database bloat.
     * 
     * @param daysToKeep number of days of logs to keep
     * @return number of log entries deleted
     */
    public long cleanupOldLogs(int daysToKeep) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysToKeep);
        long deleted = attemptLogRepository.deleteByAttemptedAtBefore(cutoff);
        if (deleted > 0) {
            logger.info("Cleaned up {} old attempt log entries", deleted);
        }
        return deleted;
    }

    /**
     * Validate if a string is a valid IPv4 address.
     */
    private boolean isValidIpAddress(String ipAddress) {
        return ipAddress != null && IPConstant.IPV4_PATTERN.matcher(ipAddress.trim()).matches();
    }

    /**
     * Validate if a string is a valid CIDR range.
     */
    private boolean isValidCidrRange(String cidrRange) {
        return cidrRange != null && IPConstant.CIDR_PATTERN.matcher(cidrRange.trim()).matches();
    }

    /**
     * Check if an IP address falls within a CIDR range.
     * Basic implementation for IPv4 CIDR matching.
     */
    private boolean isIpInRange(String ipAddress, String cidrRange) {
        if (cidrRange == null || !isValidCidrRange(cidrRange)) {
            return false;
        }

        try {
            String[] parts = cidrRange.split("/");
            String networkAddress = parts[0];
            int prefixLength = Integer.parseInt(parts[1]);

            long ipLong = ipToLong(ipAddress);
            long networkLong = ipToLong(networkAddress);
            long mask = (0xFFFFFFFFL << (32 - prefixLength)) & 0xFFFFFFFFL;

            return (ipLong & mask) == (networkLong & mask);
        } catch (Exception e) {
            logger.warn("Error checking IP {} against range {}: {}", ipAddress, cidrRange, e.getMessage());
            return false;
        }
    }

    /**
     * Convert IP address string to long value for CIDR calculations.
     */
    private long ipToLong(String ipAddress) {
        String[] parts = ipAddress.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) + Integer.parseInt(parts[i]);
        }
        return result;
    }
}