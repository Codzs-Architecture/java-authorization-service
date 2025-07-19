package com.codzs.security.blacklist;

import com.codzs.entity.blacklist.ApiAccessAttemptLog;
import com.codzs.service.blacklist.IpBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * Global IP blacklist filter for all API endpoints.
 * Blocks requests from blacklisted IP addresses to provide comprehensive security protection.
 * 
 * Security Features:
 * - Database-driven IP blacklist with CIDR range support
 * - Comprehensive logging of blocked and allowed attempts
 * - Configurable blacklist checking for different environments
 * - Protection against all types of API abuse
 * - Excludes management/actuator endpoints by default
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Component
@Order(1) // Execute early in the filter chain
public class GlobalIpBlacklistFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(GlobalIpBlacklistFilter.class);
    
    // Endpoints to exclude from IP blacklist checking (management/health endpoints)
    private static final Set<String> EXCLUDED_PATHS = Set.of(
        "/management",
        "/actuator",
        "/health",
        "/favicon.ico",
        "/error",
        "/webjars"
    );
    
    @Autowired
    private IpBlacklistService blacklistService;
    
    @Value("${security.ip-blacklist.enabled:true}")
    private boolean blacklistEnabled;
    
    @Value("${security.ip-blacklist.block-response-delay-ms:1000}")
    private int blockResponseDelayMs;
    
    @Value("${security.ip-blacklist.exclude-management-endpoints:true}")
    private boolean excludeManagementEndpoints;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        // Skip blacklist checking for excluded paths if configured
        if (excludeManagementEndpoints && isExcludedPath(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String clientIp = getClientIpAddress(request);
        
        if (blacklistEnabled && blacklistService.isIpBlacklisted(clientIp)) {
            handleBlacklistedIp(request, response, clientIp);
            return;
        } else {
            // Log allowed attempt for API endpoints (not management)
            if (!isManagementEndpoint(requestURI)) {
                logAttempt(request, clientIp, ApiAccessAttemptLog.RequestResult.ALLOWED, null);
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Checks if the request URI should be excluded from IP blacklist checking.
     */
    private boolean isExcludedPath(String requestURI) {
        return EXCLUDED_PATHS.stream().anyMatch(requestURI::startsWith);
    }
    
    /**
     * Checks if the request URI is a management/health endpoint.
     */
    private boolean isManagementEndpoint(String requestURI) {
        return requestURI.startsWith("/management") || 
               requestURI.startsWith("/actuator") || 
               requestURI.startsWith("/health");
    }
    
    /**
     * Handles requests from blacklisted IP addresses.
     */
    private void handleBlacklistedIp(HttpServletRequest request, HttpServletResponse response, String clientIp) 
            throws IOException {
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        // Log the blocked attempt
        logAttempt(request, clientIp, ApiAccessAttemptLog.RequestResult.BLOCKED_BLACKLIST, 
                  "IP address is blacklisted");
        
        // Security event logging
        logger.error("SECURITY ALERT: Blacklisted IP attempted API access. " +
                    "IP: {}, Endpoint: {}, Method: {}, User-Agent: {}, Timestamp: {}", 
                    clientIp, requestURI, method, request.getHeader("User-Agent"), 
                    System.currentTimeMillis());
        
        // Add delay to slow down potential attackers
        if (blockResponseDelayMs > 0) {
            try {
                Thread.sleep(blockResponseDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Block response delay interrupted", e);
            }
        }
        
        // Set security headers
        response.setHeader("X-Blocked-Reason", "IP_BLACKLISTED");
        response.setHeader("X-Block-Timestamp", String.valueOf(System.currentTimeMillis()));
        
        // Return 403 Forbidden
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.getWriter().write(
            "{\"error\":\"access_denied\"," +
            "\"error_description\":\"Access denied from this IP address\"," +
            "\"error_code\":\"ip_blacklisted\"}"
        );
        response.getWriter().flush();
    }
    
    /**
     * Extracts the real client IP address from the request, considering proxy headers.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Get the first IP from the comma-separated list
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * Log the API access attempt.
     */
    private void logAttempt(HttpServletRequest request, String clientIp, 
                           ApiAccessAttemptLog.RequestResult result, String blockReason) {
        try {
            String userAgent = request.getHeader("User-Agent");
            String endpoint = request.getRequestURI();
            String method = request.getMethod();
            String clientId = request.getParameter("client_id");
            String userCode = request.getParameter("user_code");
            String sessionId = request.getSession(false) != null ? request.getSession().getId() : null;
            
            blacklistService.logAttempt(clientIp, userAgent, endpoint, method, clientId, userCode, 
                                       result, blockReason, sessionId);
        } catch (Exception e) {
            logger.error("Error logging API access attempt", e);
        }
    }
}