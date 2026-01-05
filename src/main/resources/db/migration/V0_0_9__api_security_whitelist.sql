-- API Security - Whitelist Schema
-- This migration creates tables for managing API whitelisting with pattern-based validation

-- Whitelist table for allowing specific IPs/patterns to access API endpoints
CREATE TABLE api_whitelist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ip_address VARCHAR(45) COMMENT 'Specific IP address (supports both IPv4 and IPv6)',
    ip_range VARCHAR(100) COMMENT 'CIDR notation for IP ranges (e.g., 192.168.1.0/24)',
    ip_pattern VARCHAR(200) COMMENT 'Pattern for IP matching (e.g., 192.168.*.*, 10.0.0.1-10.0.0.255)',
    endpoint_pattern VARCHAR(500) COMMENT 'API endpoint pattern (e.g., /api/*, /oauth2/*, specific paths)',
    client_id VARCHAR(100) COMMENT 'OAuth2 client ID restriction (NULL for all clients)',
    description VARCHAR(500) NOT NULL COMMENT 'Description/reason for whitelisting',
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the whitelist entry was added',
    added_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM' COMMENT 'Who/what added the whitelist entry',
    expires_at TIMESTAMP NULL COMMENT 'When the whitelist entry expires (NULL for permanent)',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Whether the whitelist entry is active',
    priority INTEGER NOT NULL DEFAULT 100 COMMENT 'Priority for pattern matching (lower = higher priority)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_api_whitelist_active (is_active, expires_at),
    INDEX idx_api_whitelist_priority (priority, is_active),
    INDEX idx_api_whitelist_endpoint (endpoint_pattern, is_active),
    INDEX idx_api_whitelist_client (client_id, is_active),
    INDEX idx_api_whitelist_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Whitelist for API endpoint access with pattern-based validation';

-- Whitelist Access Log for security monitoring
CREATE TABLE api_whitelist_access_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ip_address VARCHAR(45) NOT NULL,
    user_agent VARCHAR(1000) COMMENT 'User agent string',
    endpoint VARCHAR(200) NOT NULL COMMENT 'Endpoint attempted',
    http_method VARCHAR(10) NOT NULL COMMENT 'HTTP method (GET, POST, etc.)',
    client_id VARCHAR(100) COMMENT 'OAuth2 client ID if available',
    whitelist_rule_id BIGINT COMMENT 'ID of the whitelist rule that matched (if any)',
    request_result ENUM('ALLOWED_WHITELIST', 'BLOCKED_NOT_WHITELISTED', 'SKIPPED_DISABLED') NOT NULL,
    matched_pattern VARCHAR(200) COMMENT 'The pattern that matched (if any)',
    block_reason VARCHAR(500) COMMENT 'Reason for blocking if blocked',
    request_headers JSON COMMENT 'Important request headers for analysis',
    attempted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_whitelist_access_log_ip_time (ip_address, attempted_at),
    INDEX idx_whitelist_access_log_result (request_result, attempted_at),
    INDEX idx_whitelist_access_log_endpoint (endpoint, attempted_at),
    INDEX idx_whitelist_access_log_rule (whitelist_rule_id, attempted_at),
    FOREIGN KEY (whitelist_rule_id) REFERENCES api_whitelist(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Log of API whitelist access attempts for security monitoring';

-- Insert default whitelist entries
INSERT INTO api_whitelist (ip_address, ip_range, endpoint_pattern, description, added_by, priority) VALUES
-- Localhost access to management endpoints
('127.0.0.1', NULL, '/management/*', 'Localhost access to management endpoints', 'SYSTEM', 10),
('127.0.0.1', NULL, '/actuator/*', 'Localhost access to actuator endpoints', 'SYSTEM', 10),
('::1', NULL, '/management/*', 'IPv6 localhost access to management endpoints', 'SYSTEM', 10),
('::1', NULL, '/actuator/*', 'IPv6 localhost access to actuator endpoints', 'SYSTEM', 10),

-- Network range access to specific endpoints
(NULL, '192.168.0.0/16', '/api/*', 'Local network access to API endpoints', 'SYSTEM', 50),

-- Endpoint-only patterns (accessible from any IP address)
(NULL, NULL, '/health', 'Health check endpoint - accessible from any IP', 'SYSTEM', 5),
(NULL, NULL, '/actuator/health', 'Actuator health endpoint - accessible from any IP', 'SYSTEM', 5),
(NULL, NULL, '/public/*', 'Public API endpoints - accessible from any IP', 'SYSTEM', 20),
(NULL, NULL, '/oauth2/*', 'OAuth2 endpoints - accessible from any IP', 'SYSTEM', 15),
(NULL, NULL, '/login*', 'Login pages - accessible from any IP', 'SYSTEM', 15),
(NULL, NULL, '/logout*', 'Logout pages - accessible from any IP', 'SYSTEM', 15),
(NULL, NULL, '/', 'Root endpoint - accessible from any IP', 'SYSTEM', 25),
(NULL, NULL, '/index*', 'Index pages - accessible from any IP', 'SYSTEM', 25),
(NULL, NULL, '/home*', 'Home pages - accessible from any IP', 'SYSTEM', 25),
(NULL, NULL, '/articles*', 'Articles pages - accessible from any IP', 'SYSTEM', 25),
(NULL, NULL, '/messages*', 'Messages pages - accessible from any IP', 'SYSTEM', 25),
(NULL, NULL, '/webjars/*', 'Static web resources - accessible from any IP', 'SYSTEM', 30),
(NULL, NULL, '/css/*', 'CSS resources - accessible from any IP', 'SYSTEM', 30),
(NULL, NULL, '/js/*', 'JavaScript resources - accessible from any IP', 'SYSTEM', 30),
(NULL, NULL, '/images/*', 'Image resources - accessible from any IP', 'SYSTEM', 30),
(NULL, NULL, '/assets/*', 'Asset resources - accessible from any IP', 'SYSTEM', 30),
(NULL, NULL, '/favicon.ico', 'Favicon - accessible from any IP', 'SYSTEM', 30),
(NULL, NULL, '/authorize/*', 'Authorize endoint', 'SYSTEM', 31);

-- Note: The above entries are examples for development. In production:
-- 1. Remove or modify the private network ranges based on your infrastructure
-- 2. Add specific IP addresses or ranges for your API clients
-- 3. Configure endpoint patterns based on your API structure
-- 4. Set appropriate priorities for pattern matching order