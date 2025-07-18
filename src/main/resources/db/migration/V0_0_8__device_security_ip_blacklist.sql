-- Device Authorization Security - IP Blacklist Schema
-- This migration creates tables for managing IP blacklisting to prevent device authorization abuse

-- IP Blacklist table for blocking suspicious IPs from device authorization
CREATE TABLE device_ip_blacklist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ip_address VARCHAR(45) NOT NULL COMMENT 'IP address (supports both IPv4 and IPv6)',
    ip_range VARCHAR(100) COMMENT 'CIDR notation for IP ranges (e.g., 192.168.1.0/24)',
    reason VARCHAR(500) NOT NULL COMMENT 'Reason for blacklisting',
    blocked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the IP was blacklisted',
    blocked_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM' COMMENT 'Who/what blacklisted the IP',
    expires_at TIMESTAMP NULL COMMENT 'When the blacklist entry expires (NULL for permanent)',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Whether the blacklist entry is active',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_device_ip_blacklist_ip (ip_address),
    INDEX idx_device_ip_blacklist_active (is_active, expires_at),
    INDEX idx_device_ip_blacklist_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='IP blacklist for device authorization security';

-- API Access Attempt Log for security monitoring
CREATE TABLE api_access_attempt_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ip_address VARCHAR(45) NOT NULL,
    user_agent VARCHAR(1000) COMMENT 'User agent string',
    endpoint VARCHAR(200) NOT NULL COMMENT 'Endpoint attempted (e.g., /oauth2/device_authorization)',
    http_method VARCHAR(10) NOT NULL COMMENT 'HTTP method (GET, POST, etc.)',
    client_id VARCHAR(100) COMMENT 'OAuth2 client ID if available',
    user_code VARCHAR(20) COMMENT 'Device user code if available',
    request_result ENUM('ALLOWED', 'BLOCKED_BLACKLIST', 'BLOCKED_RATE_LIMIT', 'BLOCKED_OTHER') NOT NULL,
    block_reason VARCHAR(500) COMMENT 'Reason for blocking if blocked',
    session_id VARCHAR(100) COMMENT 'Session ID for tracking',
    request_headers JSON COMMENT 'Important request headers for analysis',
    attempted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_api_access_log_ip_time (ip_address, attempted_at),
    INDEX idx_api_access_log_result (request_result, attempted_at),
    INDEX idx_api_access_log_client (client_id, attempted_at),
    INDEX idx_api_access_log_endpoint (endpoint, attempted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Log of all API access attempts for security monitoring';

-- Insert some default blacklist entries for known bad IP ranges
INSERT INTO device_ip_blacklist (ip_address, ip_range, reason, blocked_by) VALUES
('0.0.0.0', '0.0.0.0/8', 'Invalid IP range - reserved', 'SYSTEM'),
('127.0.0.1', NULL, 'Example localhost block (remove in production)', 'SYSTEM'),
('10.0.0.0', '10.0.0.0/8', 'Example private network block (configure as needed)', 'SYSTEM');

-- Note: The above entries are examples. In production:
-- 1. Remove the localhost block (127.0.0.1)
-- 2. Configure appropriate IP ranges based on your security requirements
-- 3. The 0.0.0.0/8 block should remain as it's invalid