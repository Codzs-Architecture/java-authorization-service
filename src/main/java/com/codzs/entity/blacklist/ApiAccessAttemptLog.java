package com.codzs.entity.blacklist;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for logging API access attempts for security monitoring.
 * This entity tracks all attempts to access API endpoints,
 * including both allowed and blocked requests, for security analysis.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Entity
@Table(name = "api_access_attempt_log",
       indexes = {
           @Index(name = "idx_api_access_log_ip_time", columnList = "ip_address, attempted_at"),
           @Index(name = "idx_api_access_log_result", columnList = "request_result, attempted_at"),
           @Index(name = "idx_api_access_log_client", columnList = "client_id, attempted_at"),
           @Index(name = "idx_api_access_log_endpoint", columnList = "endpoint, attempted_at")
       })
public class ApiAccessAttemptLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 1000)
    private String userAgent;

    @Column(name = "endpoint", nullable = false, length = 200)
    private String endpoint;

    @Column(name = "http_method", nullable = false, length = 10)
    private String httpMethod;

    @Column(name = "client_id", length = 100)
    private String clientId;

    @Column(name = "user_code", length = 20)
    private String userCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_result", nullable = false)
    private RequestResult requestResult;

    @Column(name = "block_reason", length = 500)
    private String blockReason;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "request_headers", columnDefinition = "JSON")
    private String requestHeaders;

    @Column(name = "attempted_at", nullable = false)
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
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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