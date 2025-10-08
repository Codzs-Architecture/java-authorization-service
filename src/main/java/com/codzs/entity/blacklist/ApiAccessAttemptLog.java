package com.codzs.entity.blacklist;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * MongoDB Document for logging API access attempts for security monitoring.
 * This document tracks all attempts to access API endpoints,
 * including both allowed and blocked requests, for security analysis.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Document(collection = "api_access_attempt_log")
@CompoundIndexes({
    @CompoundIndex(name = "idx_api_access_log_ip_time", def = "{'ipAddress': 1, 'attemptedAt': 1}"),
    @CompoundIndex(name = "idx_api_access_log_result", def = "{'requestResult': 1, 'attemptedAt': 1}"),
    @CompoundIndex(name = "idx_api_access_log_client", def = "{'clientId': 1, 'attemptedAt': 1}"),
    @CompoundIndex(name = "idx_api_access_log_endpoint", def = "{'endpoint': 1, 'attemptedAt': 1}")
})
public class ApiAccessAttemptLog {

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

    @Size(max = 20, message = "User code must not exceed 20 characters")
    private String userCode;

    @NotNull(message = "Request result is required")
    private RequestResult requestResult;

    @Size(max = 500, message = "Block reason must not exceed 500 characters")
    private String blockReason;

    @Size(max = 100, message = "Session ID must not exceed 100 characters")
    private String sessionId;

    private String requestHeaders; // MongoDB supports nested documents naturally

    @CreatedDate
    @NotNull(message = "Attempted at timestamp is required")
    private LocalDateTime attemptedAt;

    /**
     * Enumeration of possible request results for API access attempts.
     */
    public enum RequestResult {
        ALLOWED("Request was allowed"),
        BLOCKED_BLACKLIST("Request blocked due to IP blacklist"),
        BLOCKED_RATE_LIMIT("Request blocked due to rate limiting"),
        BLOCKED_OTHER("Request blocked for other security reasons");

        private final String description;

        RequestResult(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Constructors
    public ApiAccessAttemptLog() {
        this.attemptedAt = LocalDateTime.now();
    }

    public ApiAccessAttemptLog(String ipAddress, String endpoint, String httpMethod, RequestResult requestResult) {
        this();
        this.ipAddress = ipAddress;
        this.endpoint = endpoint;
        this.httpMethod = httpMethod;
        this.requestResult = requestResult;
    }

    // Builder pattern for easier construction
    public static ApiAccessAttemptLog builder() {
        return new ApiAccessAttemptLog();
    }

    public ApiAccessAttemptLog ipAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public ApiAccessAttemptLog userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public ApiAccessAttemptLog endpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public ApiAccessAttemptLog httpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public ApiAccessAttemptLog clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public ApiAccessAttemptLog userCode(String userCode) {
        this.userCode = userCode;
        return this;
    }

    public ApiAccessAttemptLog requestResult(RequestResult requestResult) {
        this.requestResult = requestResult;
        return this;
    }

    public ApiAccessAttemptLog blockReason(String blockReason) {
        this.blockReason = blockReason;
        return this;
    }

    public ApiAccessAttemptLog sessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public ApiAccessAttemptLog requestHeaders(String requestHeaders) {
        this.requestHeaders = requestHeaders;
        return this;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getUserCode() { return userCode; }
    public void setUserCode(String userCode) { this.userCode = userCode; }

    public RequestResult getRequestResult() { return requestResult; }
    public void setRequestResult(RequestResult requestResult) { this.requestResult = requestResult; }

    public String getBlockReason() { return blockReason; }
    public void setBlockReason(String blockReason) { this.blockReason = blockReason; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getRequestHeaders() { return requestHeaders; }
    public void setRequestHeaders(String requestHeaders) { this.requestHeaders = requestHeaders; }

    public LocalDateTime getAttemptedAt() { return attemptedAt; }
    public void setAttemptedAt(LocalDateTime attemptedAt) { this.attemptedAt = attemptedAt; }

    @Override
    public String toString() {
        return "ApiAccessAttemptLog{" +
                "id=" + id +
                ", ipAddress='" + ipAddress + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", clientId='" + clientId + '\'' +
                ", requestResult=" + requestResult +
                ", blockReason='" + blockReason + '\'' +
                ", attemptedAt=" + attemptedAt +
                '}';
    }
}