package com.codzs.security;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Rate limiting filter for OAuth2 device authorization endpoints.
 * Implements IP-based rate limiting to prevent abuse of device authorization flow.
 * 
 * Security Features:
 * - Per-IP rate limiting for device authorization endpoints
 * - Configurable rate limits and time windows
 * - Comprehensive security logging
 * - Protection against device code enumeration attacks
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Component
public class DeviceAuthorizationRateLimitingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(DeviceAuthorizationRateLimitingFilter.class);
    
    // Device authorization endpoints that require rate limiting
    private static final String DEVICE_AUTHORIZATION_ENDPOINT = "/oauth2/device_authorization";
    private static final String DEVICE_VERIFICATION_ENDPOINT = "/oauth2/device_verification";
    
    // Rate limiting configuration
    @Value("${device.security.rate-limiting.requests-per-minute:10}")
    private int requestsPerMinute;
    
    @Value("${device.security.rate-limiting.enabled:true}")
    private boolean rateLimitingEnabled;
    
    @Value("${device.security.rate-limiting.time-window-minutes:1}")
    private int timeWindowMinutes;
    
    private final ConcurrentMap<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();
    private final RateLimiterRegistry rateLimiterRegistry;
    
    public DeviceAuthorizationRateLimitingFilter() {
        this.rateLimiterRegistry = RateLimiterRegistry.ofDefaults();
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        // Check if this is a device authorization endpoint that needs rate limiting
        if (isDeviceAuthorizationEndpoint(requestURI)) {
            // Only rate limit GET requests to device_authorization (initial device code requests)
            // Do NOT rate limit POST requests to device_verification (consent form submissions)
            if ("GET".equals(method) && requestURI.contains(DEVICE_AUTHORIZATION_ENDPOINT)) {
                if (rateLimitingEnabled && !isRateLimitPassed(request)) {
                    handleRateLimitExceeded(request, response);
                    return;
                }
            }
            // POST requests to device_verification should pass through without rate limiting
            // as they are legitimate user consent submissions
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Checks if the request URI is a device authorization endpoint that requires rate limiting.
     * 
     * Note: We only rate-limit backend OAuth2 endpoints, not user-facing activation endpoints.
     * The /activate and /activated endpoints are for legitimate user interactions and should not be rate-limited.
     */
    private boolean isDeviceAuthorizationEndpoint(String requestURI) {
        // Only apply rate limiting to backend OAuth2 endpoints that can be abused for enumeration
        return requestURI.contains(DEVICE_AUTHORIZATION_ENDPOINT) || 
               requestURI.contains(DEVICE_VERIFICATION_ENDPOINT);
        // Explicitly exclude user-facing endpoints: /activate, /activated
    }
    
    /**
     * Checks if the request passes rate limiting based on client IP.
     */
    private boolean isRateLimitPassed(HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        String rateLimiterKey = "device-auth-" + clientIp;
        
        RateLimiter rateLimiter = rateLimiters.computeIfAbsent(rateLimiterKey, this::createRateLimiter);
        
        try {
            // Try to acquire permission from rate limiter
            rateLimiter.acquirePermission(1);
            
            logger.debug("Rate limit check passed for IP: {} on endpoint: {}", 
                        clientIp, request.getRequestURI());
            return true;
            
        } catch (RequestNotPermitted e) {
            logger.warn("Rate limit exceeded for IP: {} on endpoint: {}. " +
                       "Request rejected to prevent device authorization abuse", 
                       clientIp, request.getRequestURI());
            return false;
        }
    }
    
    /**
     * Creates a rate limiter for the given key with configured limits.
     */
    private RateLimiter createRateLimiter(String key) {
        RateLimiterConfig config = RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofMinutes(timeWindowMinutes))
            .limitForPeriod(requestsPerMinute)
            .timeoutDuration(Duration.ofMillis(100))
            .build();
            
        return rateLimiterRegistry.rateLimiter(key, config);
    }
    
    /**
     * Handles rate limit exceeded scenario by returning 429 Too Many Requests.
     */
    private void handleRateLimitExceeded(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String clientIp = getClientIpAddress(request);
        
        // Log security event
        logger.error("SECURITY ALERT: Rate limit exceeded for device authorization. " +
                    "IP: {}, Endpoint: {}, User-Agent: {}, Timestamp: {}", 
                    clientIp, request.getRequestURI(), request.getHeader("User-Agent"), 
                    System.currentTimeMillis());
        
        // Set security headers
        response.setHeader("X-RateLimit-Limit", String.valueOf(requestsPerMinute));
        response.setHeader("X-RateLimit-Remaining", "0");
        response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() + (timeWindowMinutes * 60 * 1000)));
        response.setHeader("Retry-After", String.valueOf(timeWindowMinutes * 60));
        
        // Return 429 Too Many Requests
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.getWriter().write(
            "{\"error\":\"too_many_requests\"," +
            "\"error_description\":\"Rate limit exceeded for device authorization\"," +
            "\"retry_after\":" + (timeWindowMinutes * 60) + "}"
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
     * Cleanup method to remove expired rate limiters and free memory.
     * This method should be called periodically by a scheduled task.
     */
    public void cleanupExpiredRateLimiters() {
        // Remove rate limiters that haven't been used recently
        int initialSize = rateLimiters.size();
        rateLimiters.entrySet().removeIf(entry -> {
            RateLimiter rateLimiter = entry.getValue();
            // Check if rate limiter has available permissions (indicating it's not being used)
            try {
                // Try to acquire permission without waiting, if it fails, the rate limiter is still active
                return rateLimiter.acquirePermission(1);
            } catch (RequestNotPermitted e) {
                // If acquisition fails, keep the rate limiter as it's still being used
                return false;
            }
        });
        
        int removedCount = initialSize - rateLimiters.size();
        
        logger.debug("Cleaned up {} expired rate limiters. Current count: {}", removedCount, rateLimiters.size());
    }
}