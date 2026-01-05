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
package com.codzs.entity.whitelist;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * MongoDB Document for logging API whitelist access attempts for security monitoring and analysis.
 *
 * @author Nitin Khaitan
 * @since 1.1
 */
@Document(collection = "api_whitelist_access_log")
@CompoundIndexes({
    @CompoundIndex(name = "idx_whitelist_access_log_ip_time", def = "{'ipAddress': 1, 'attemptedAt': 1}"),
    @CompoundIndex(name = "idx_whitelist_access_log_result", def = "{'requestResult': 1, 'attemptedAt': 1}"),
    @CompoundIndex(name = "idx_whitelist_access_log_endpoint", def = "{'endpoint': 1, 'attemptedAt': 1}"),
    @CompoundIndex(name = "idx_whitelist_access_log_rule", def = "{'whitelistRuleId': 1, 'attemptedAt': 1}")
})
public class ApiWhitelistAccessLog {

    @Id
    private String id; // MongoDB ObjectId

    @NotBlank(message = "IP address is required")
    @Size(max = 45, message = "IP address must not exceed 45 characters")
    private String ipAddress;

    @Size(max = 1000, message = "User agent must not exceed 1000 characters")
    private String userAgent;

    @NotBlank(message = "Endpoint is required")
    @Size(max = 200, message = "Endpoint must not exceed 200 characters")
    private String endpoint;

    @NotBlank(message = "HTTP method is required")
    @Size(max = 10, message = "HTTP method must not exceed 10 characters")
    private String httpMethod;

    @Size(max = 100, message = "Client ID must not exceed 100 characters")
    private String clientId;

    private String whitelistRuleId; // Changed to String to reference MongoDB ObjectId

    @NotNull(message = "Request result is required")
    private RequestResult requestResult;

    @Size(max = 200, message = "Matched pattern must not exceed 200 characters")
    private String matchedPattern;

    @Size(max = 500, message = "Block reason must not exceed 500 characters")
    private String blockReason;

    private Map<String, String> requestHeaders; // MongoDB supports nested documents naturally

    @CreatedDate
    private LocalDateTime attemptedAt;

    /**
     * Enumeration of possible request results for whitelist validation.
     */
    public enum RequestResult {
        ALLOWED_WHITELIST("Request allowed by whitelist rule"),
        BLOCKED_NOT_WHITELISTED("Request blocked - not in whitelist"),
        SKIPPED_DISABLED("Whitelist validation skipped - disabled");

        private final String description;

        RequestResult(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Creates a successful whitelist validation log entry.
     *
     * @param ipAddress the client IP address
     * @param endpoint the requested endpoint
     * @param httpMethod the HTTP method used
     * @param whitelistRuleId the ID of the matching whitelist rule
     * @param matchedPattern the pattern that matched
     * @return a new log entry for successful validation
     */
    public static ApiWhitelistAccessLog createAllowedEntry(String ipAddress, String endpoint, 
                                                          String httpMethod, String whitelistRuleId, 
                                                          String matchedPattern) {
        ApiWhitelistAccessLog log = new ApiWhitelistAccessLog();
        log.setIpAddress(ipAddress);
        log.setEndpoint(endpoint);
        log.setHttpMethod(httpMethod);
        log.setWhitelistRuleId(whitelistRuleId);
        log.setMatchedPattern(matchedPattern);
        log.setRequestResult(RequestResult.ALLOWED_WHITELIST);
        return log;
    }

    /**
     * Creates a blocked request log entry.
     *
     * @param ipAddress the client IP address
     * @param endpoint the requested endpoint
     * @param httpMethod the HTTP method used
     * @param blockReason the reason for blocking
     * @return a new log entry for blocked request
     */
    public static ApiWhitelistAccessLog createBlockedEntry(String ipAddress, String endpoint, 
                                                          String httpMethod, String blockReason) {
        ApiWhitelistAccessLog log = new ApiWhitelistAccessLog();
        log.setIpAddress(ipAddress);
        log.setEndpoint(endpoint);
        log.setHttpMethod(httpMethod);
        log.setBlockReason(blockReason);
        log.setRequestResult(RequestResult.BLOCKED_NOT_WHITELISTED);
        return log;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getWhitelistRuleId() {
        return whitelistRuleId;
    }

    public void setWhitelistRuleId(String whitelistRuleId) {
        this.whitelistRuleId = whitelistRuleId;
    }

    public RequestResult getRequestResult() {
        return requestResult;
    }

    public void setRequestResult(RequestResult requestResult) {
        this.requestResult = requestResult;
    }

    public String getMatchedPattern() {
        return matchedPattern;
    }

    public void setMatchedPattern(String matchedPattern) {
        this.matchedPattern = matchedPattern;
    }

    public String getBlockReason() {
        return blockReason;
    }

    public void setBlockReason(String blockReason) {
        this.blockReason = blockReason;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public LocalDateTime getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(LocalDateTime attemptedAt) {
        this.attemptedAt = attemptedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiWhitelistAccessLog that = (ApiWhitelistAccessLog) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ApiWhitelistAccessLog{" +
                "id=" + id +
                ", ipAddress='" + ipAddress + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", requestResult=" + requestResult +
                ", matchedPattern='" + matchedPattern + '\'' +
                ", attemptedAt=" + attemptedAt +
                '}';
    }
}