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

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Entity for logging API whitelist access attempts for security monitoring and analysis.
 *
 * @author Nitin Khaitan
 * @since 1.1
 */
@Entity
@Table(name = "api_whitelist_access_log",
       indexes = {
           @Index(name = "idx_whitelist_access_log_ip_time", columnList = "ip_address, attempted_at"),
           @Index(name = "idx_whitelist_access_log_result", columnList = "request_result, attempted_at"),
           @Index(name = "idx_whitelist_access_log_endpoint", columnList = "endpoint, attempted_at"),
           @Index(name = "idx_whitelist_access_log_rule", columnList = "whitelist_rule_id, attempted_at")
       })
public class ApiWhitelistAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ip_address", length = 45, nullable = false)
    @NotBlank(message = "IP address is required")
    @Size(max = 45, message = "IP address must not exceed 45 characters")
    private String ipAddress;

    @Column(name = "user_agent", length = 1000)
    @Size(max = 1000, message = "User agent must not exceed 1000 characters")
    private String userAgent;

    @Column(name = "endpoint", length = 200, nullable = false)
    @NotBlank(message = "Endpoint is required")
    @Size(max = 200, message = "Endpoint must not exceed 200 characters")
    private String endpoint;

    @Column(name = "http_method", length = 10, nullable = false)
    @NotBlank(message = "HTTP method is required")
    @Size(max = 10, message = "HTTP method must not exceed 10 characters")
    private String httpMethod;

    @Column(name = "client_id", length = 100)
    @Size(max = 100, message = "Client ID must not exceed 100 characters")
    private String clientId;

    @Column(name = "whitelist_rule_id")
    private Long whitelistRuleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_result", nullable = false)
    @NotNull(message = "Request result is required")
    private RequestResult requestResult;

    @Column(name = "matched_pattern", length = 200)
    @Size(max = 200, message = "Matched pattern must not exceed 200 characters")
    private String matchedPattern;

    @Column(name = "block_reason", length = 500)
    @Size(max = 500, message = "Block reason must not exceed 500 characters")
    private String blockReason;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_headers", columnDefinition = "json")
    private Map<String, String> requestHeaders;

    @CreationTimestamp
    @Column(name = "attempted_at", nullable = false, updatable = false)
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
                                                          String httpMethod, Long whitelistRuleId, 
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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getWhitelistRuleId() {
        return whitelistRuleId;
    }

    public void setWhitelistRuleId(Long whitelistRuleId) {
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