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
package com.codzs.service.whitelist;

import com.codzs.entity.whitelist.ApiWhitelist;
import com.codzs.entity.whitelist.ApiWhitelistAccessLog;
import com.codzs.repository.whitelist.ApiWhitelistRepository;
import com.codzs.repository.whitelist.ApiWhitelistAccessLogRepository;
import com.codzs.security.constant.IPConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Service for managing whitelist validation with pattern-based matching.
 * Supports exact IP matches, CIDR ranges, and custom pattern matching.
 *
 * @author Nitin Khaitan
 * @since 1.1
 */
@Service
@Transactional
public class ApiWhitelistService {

    private static final Logger logger = LoggerFactory.getLogger(ApiWhitelistService.class);

    @Autowired
    private ApiWhitelistRepository whitelistRepository;

    @Autowired
    private ApiWhitelistAccessLogRepository accessLogRepository;

    /**
     * Result of whitelist validation containing the decision and metadata.
     */
    public static class WhitelistValidationResult {
        private final boolean allowed;
        private final String reason;
        private final String matchedRuleId;
        private final String matchedPattern;

        public WhitelistValidationResult(boolean allowed, String reason, String matchedRuleId, String matchedPattern) {
            this.allowed = allowed;
            this.reason = reason;
            this.matchedRuleId = matchedRuleId;
            this.matchedPattern = matchedPattern;
        }

        public boolean isAllowed() { return allowed; }
        public String getReason() { return reason; }
        public String getMatchedRuleId() { return matchedRuleId; }
        public String getMatchedPattern() { return matchedPattern; }
    }

    /**
     * Validates if an IP address is whitelisted for accessing a specific endpoint.
     *
     * @param ipAddress the client IP address
     * @param endpoint the endpoint being accessed
     * @param httpMethod the HTTP method
     * @param clientId the OAuth2 client ID (optional)
     * @param userAgent the user agent string (optional)
     * @return validation result indicating if access is allowed
     */
    public WhitelistValidationResult validateIpAccess(String ipAddress, String endpoint, 
                                                     String httpMethod, String clientId, 
                                                     String userAgent) {
        try {
            logger.debug("Validating IP access: ip={}, endpoint={}, method={}, client={}", 
                        ipAddress, endpoint, httpMethod, clientId);

            // Get potential matching whitelist entries
            List<ApiWhitelist> potentialMatches = whitelistRepository.findPotentialMatches(
                ipAddress, LocalDateTime.now());

            // Check each potential match in priority order
            for (ApiWhitelist whitelistEntry : potentialMatches) {
                if (matchesWhitelistEntry(ipAddress, endpoint, clientId, whitelistEntry)) {
                    String matchedPattern = getMatchedPattern(ipAddress, whitelistEntry);
                    
                    // Log successful validation
                    logAccessAttempt(ipAddress, endpoint, httpMethod, clientId, userAgent,
                                   ApiWhitelistAccessLog.RequestResult.ALLOWED_WHITELIST,
                                   whitelistEntry.getId(), matchedPattern, null);
                    
                    logger.debug("IP access allowed by whitelist rule {}: pattern={}", 
                               whitelistEntry.getId(), matchedPattern);
                    
                    return new WhitelistValidationResult(true, 
                        "IP allowed by whitelist rule: " + whitelistEntry.getDescription(),
                        whitelistEntry.getId(), matchedPattern);
                }
            }

            // No matching whitelist entry found
            String blockReason = "IP address not found in whitelist for endpoint: " + endpoint;
            
            // Log blocked attempt
            logAccessAttempt(ipAddress, endpoint, httpMethod, clientId, userAgent,
                           ApiWhitelistAccessLog.RequestResult.BLOCKED_NOT_WHITELISTED,
                           null, null, blockReason);
            
            logger.debug("IP access blocked: {}", blockReason);
            
            return new WhitelistValidationResult(false, blockReason, null, null);

        } catch (Exception e) {
            logger.error("Error validating IP whitelist for {}: {}", ipAddress, e.getMessage(), e);
            
            // Log the error attempt
            String errorReason = "Whitelist validation error: " + e.getMessage();
            logAccessAttempt(ipAddress, endpoint, httpMethod, clientId, userAgent,
                           ApiWhitelistAccessLog.RequestResult.BLOCKED_NOT_WHITELISTED,
                           null, null, errorReason);
            
            return new WhitelistValidationResult(false, errorReason, null, null);
        }
    }

    /**
     * Checks if the given request matches a whitelist entry.
     */
    private boolean matchesWhitelistEntry(String ipAddress, String endpoint, String clientId, 
                                        ApiWhitelist whitelistEntry) {
        // Check if the entry is currently active
        if (!whitelistEntry.isCurrentlyActive()) {
            return false;
        }

        // Check endpoint pattern matching first
        if (!matchesEndpointPattern(endpoint, whitelistEntry.getEndpointPattern())) {
            return false;
        }

        // Check client ID matching
        if (!matchesClientId(clientId, whitelistEntry.getClientId())) {
            return false;
        }

        // NEW LOGIC: If endpoint_pattern is provided and all IP fields are null,
        // then this endpoint is accessible from any IP address
        if (isEndpointOnlyPattern(whitelistEntry)) {
            logger.debug("Endpoint-only pattern matched: {} allows access from any IP", 
                        whitelistEntry.getEndpointPattern());
            return true;
        }

        // Check IP matching for entries that have IP restrictions
        if (!matchesIpCriteria(ipAddress, whitelistEntry)) {
            return false;
        }

        return true;
    }

    /**
     * Checks if this is an endpoint-only pattern (allows any IP address).
     * An endpoint-only pattern has an endpoint_pattern defined but all IP fields are null/empty.
     */
    private boolean isEndpointOnlyPattern(ApiWhitelist whitelistEntry) {
        // Must have an endpoint pattern
        if (whitelistEntry.getEndpointPattern() == null || 
            whitelistEntry.getEndpointPattern().trim().isEmpty()) {
            return false;
        }

        // All IP criteria must be null or empty
        boolean ipAddressEmpty = whitelistEntry.getIpAddress() == null || 
                               whitelistEntry.getIpAddress().trim().isEmpty();
        boolean ipRangeEmpty = whitelistEntry.getIpRange() == null || 
                             whitelistEntry.getIpRange().trim().isEmpty();
        boolean ipPatternEmpty = whitelistEntry.getIpPattern() == null || 
                               whitelistEntry.getIpPattern().trim().isEmpty();

        return ipAddressEmpty && ipRangeEmpty && ipPatternEmpty;
    }

    /**
     * Checks if the IP address matches the whitelist entry's IP criteria.
     */
    private boolean matchesIpCriteria(String ipAddress, ApiWhitelist whitelistEntry) {
        // Exact IP match
        if (whitelistEntry.getIpAddress() != null && 
            whitelistEntry.getIpAddress().equals(ipAddress)) {
            return true;
        }

        // CIDR range match
        if (whitelistEntry.getIpRange() != null && 
            matchesCidrRange(ipAddress, whitelistEntry.getIpRange())) {
            return true;
        }

        // Pattern match
        if (whitelistEntry.getIpPattern() != null && 
            matchesIpPattern(ipAddress, whitelistEntry.getIpPattern())) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the IP address matches a CIDR range.
     */
    private boolean matchesCidrRange(String ipAddress, String cidrRange) {
        try {
            if (!IPConstant.CIDR_PATTERN.matcher(cidrRange).matches()) {
                logger.warn("Invalid CIDR pattern: {}", cidrRange);
                return false;
            }

            String[] parts = cidrRange.split("/");
            String networkAddress = parts[0];
            int prefixLength = Integer.parseInt(parts[1]);

            InetAddress targetAddr = InetAddress.getByName(ipAddress);
            InetAddress networkAddr = InetAddress.getByName(networkAddress);

            byte[] targetBytes = targetAddr.getAddress();
            byte[] networkBytes = networkAddr.getAddress();

            if (targetBytes.length != networkBytes.length) {
                return false; // Different IP versions
            }

            int bytesToCheck = prefixLength / 8;
            int bitsToCheck = prefixLength % 8;

            // Check full bytes
            for (int i = 0; i < bytesToCheck; i++) {
                if (targetBytes[i] != networkBytes[i]) {
                    return false;
                }
            }

            // Check partial byte if needed
            if (bitsToCheck > 0 && bytesToCheck < targetBytes.length) {
                int mask = 0xFF << (8 - bitsToCheck);
                if ((targetBytes[bytesToCheck] & mask) != (networkBytes[bytesToCheck] & mask)) {
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            logger.warn("Error checking CIDR range {} for IP {}: {}", cidrRange, ipAddress, e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the IP address matches a custom pattern.
     * Supports wildcards (*) and ranges (e.g., 192.168.1.1-192.168.1.255).
     */
    private boolean matchesIpPattern(String ipAddress, String ipPattern) {
        try {
            // Handle wildcard patterns (e.g., 192.168.*.*)
            if (ipPattern.contains("*")) {
                String regexPattern = ipPattern
                    .replace(".", "\\.")
                    .replace("*", "\\d{1,3}");
                Pattern pattern = Pattern.compile("^" + regexPattern + "$");
                return pattern.matcher(ipAddress).matches();
            }

            // Handle range patterns (e.g., 192.168.1.1-192.168.1.255)
            if (ipPattern.contains("-")) {
                String[] rangeParts = ipPattern.split("-");
                if (rangeParts.length == 2) {
                    return isIpInRange(ipAddress, rangeParts[0].trim(), rangeParts[1].trim());
                }
            }

            // Direct pattern match
            return ipAddress.equals(ipPattern);

        } catch (Exception e) {
            logger.warn("Error matching IP pattern {} for IP {}: {}", ipPattern, ipAddress, e.getMessage());
            return false;
        }
    }

    /**
     * Checks if an IP address is within a range.
     */
    private boolean isIpInRange(String ipAddress, String startIp, String endIp) {
        try {
            long targetIp = ipToLong(ipAddress);
            long startIpLong = ipToLong(startIp);
            long endIpLong = ipToLong(endIp);
            
            return targetIp >= startIpLong && targetIp <= endIpLong;
        } catch (Exception e) {
            logger.warn("Error checking IP range {}-{} for IP {}: {}", startIp, endIp, ipAddress, e.getMessage());
            return false;
        }
    }

    /**
     * Converts an IPv4 address to a long value for range comparison.
     */
    private long ipToLong(String ipAddress) {
        String[] parts = ipAddress.split("\\.");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid IPv4 address: " + ipAddress);
        }
        
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = result * 256 + Integer.parseInt(parts[i]);
        }
        return result;
    }

    /**
     * Checks if the endpoint matches the whitelist entry's endpoint pattern.
     */
    private boolean matchesEndpointPattern(String endpoint, String endpointPattern) {
        if (endpointPattern == null || endpointPattern.trim().isEmpty()) {
            return true; // No endpoint restriction
        }

        // Handle wildcard patterns
        if (endpointPattern.contains("*")) {
            String regexPattern = endpointPattern
                .replace(".", "\\.")
                .replace("*", ".*");
            Pattern pattern = Pattern.compile("^" + regexPattern + "$");
            return pattern.matcher(endpoint).matches();
        }

        // Exact match
        return endpoint.equals(endpointPattern) || endpoint.startsWith(endpointPattern);
    }

    /**
     * Checks if the client ID matches the whitelist entry's client restriction.
     */
    private boolean matchesClientId(String clientId, String whitelistClientId) {
        if (whitelistClientId == null || whitelistClientId.trim().isEmpty()) {
            return true; // No client restriction
        }
        
        return whitelistClientId.equals(clientId);
    }

    /**
     * Gets the pattern that matched for logging purposes.
     */
    private String getMatchedPattern(String ipAddress, ApiWhitelist whitelistEntry) {
        // Check if this is an endpoint-only pattern (any IP allowed)
        if (isEndpointOnlyPattern(whitelistEntry)) {
            return "endpoint-only:" + whitelistEntry.getEndpointPattern();
        }
        
        if (whitelistEntry.getIpAddress() != null && whitelistEntry.getIpAddress().equals(ipAddress)) {
            return "exact:" + whitelistEntry.getIpAddress();
        }
        if (whitelistEntry.getIpRange() != null) {
            return "cidr:" + whitelistEntry.getIpRange();
        }
        if (whitelistEntry.getIpPattern() != null) {
            return "pattern:" + whitelistEntry.getIpPattern();
        }
        return "unknown";
    }

    /**
     * Logs an access attempt for security monitoring.
     */
    private void logAccessAttempt(String ipAddress, String endpoint, String httpMethod, 
                                String clientId, String userAgent, 
                                ApiWhitelistAccessLog.RequestResult result,
                                String whitelistRuleId, String matchedPattern, String blockReason) {
        try {
            ApiWhitelistAccessLog log = new ApiWhitelistAccessLog();
            log.setIpAddress(ipAddress);
            log.setEndpoint(endpoint);
            log.setHttpMethod(httpMethod);
            log.setClientId(clientId);
            log.setUserAgent(userAgent);
            log.setRequestResult(result);
            log.setWhitelistRuleId(whitelistRuleId);
            log.setMatchedPattern(matchedPattern);
            log.setBlockReason(blockReason);

            // Add relevant headers for analysis
            Map<String, String> headers = new HashMap<>();
            if (userAgent != null) {
                headers.put("User-Agent", userAgent);
            }
            log.setRequestHeaders(headers);

            accessLogRepository.save(log);
            
        } catch (Exception e) {
            logger.error("Failed to log whitelist access attempt for IP {}: {}", ipAddress, e.getMessage(), e);
        }
    }

    /**
     * Gets active whitelist entries ordered by priority.
     */
    @Transactional(readOnly = true)
    public List<ApiWhitelist> getActiveWhitelistEntries() {
        return whitelistRepository.findActiveWhitelistEntriesOrderedByPriority(LocalDateTime.now());
    }

    /**
     * Gets access statistics for a time period.
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getAccessStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        List<Object> stats = accessLogRepository.getAccessStatistics(startTime, endTime);
        Map<String, Long> result = new HashMap<>();
        
        for (Object stat : stats) {
            if (stat instanceof Object[]) {
                Object[] statArray = (Object[]) stat;
                String resultType = statArray[0].toString();
                Long count = (Long) statArray[1];
                result.put(resultType, count);
            }
        }
        
        return result;
    }

    /**
     * Gets recent blocked attempts for security monitoring.
     */
    @Transactional(readOnly = true)
    public List<ApiWhitelistAccessLog> getRecentBlockedAttempts(LocalDateTime since) {
        return accessLogRepository.findBlockedAttempts(since, LocalDateTime.now());
    }
}