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
package com.codzs.filter;

import com.codzs.service.whitelist.ApiWhitelistService;
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
 * Global API whitelist filter that validates API access based on configurable whitelist patterns.
 * This filter operates at a high priority to ensure whitelist validation occurs early in the filter chain.
 * Supports pattern-based IP matching, endpoint-specific restrictions, and client-based access control.
 *
 * @author Nitin Khaitan
 * @since 1.1
 */
@Component
@Order(2) // Execute after GlobalIpBlacklistFilter but before other security filters
public class GlobalApiWhitelistFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(GlobalApiWhitelistFilter.class);

    private static final Set<String> EXCLUDED_PATHS = Set.of(
        "/management", "/actuator", "/health", "/favicon.ico", "/error", "/webjars",
        "/css", "/js", "/images", "/static", "/public"
    );

    @Value("${security.ip-whitelist.enabled:false}")
    private boolean whitelistEnabled;

    @Value("${security.ip-whitelist.block-response-delay-ms:1000}")
    private long blockResponseDelayMs;

    @Value("${security.ip-whitelist.enforce-mode:false}")
    private boolean enforceMode;

    @Autowired
    private ApiWhitelistService whitelistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // Skip filtering if whitelist is disabled
        if (!whitelistEnabled) {
            logger.trace("IP whitelist filter disabled, skipping validation");
            filterChain.doFilter(request, response);
            return;
        }

        String requestUri = request.getRequestURI();
        String clientIp = getClientIpAddress(request);
        String httpMethod = request.getMethod();
        String userAgent = request.getHeader("User-Agent");
        String clientId = extractClientId(request);

        // Skip validation for excluded paths
        if (isExcludedPath(requestUri)) {
            logger.trace("Skipping whitelist validation for excluded path: {}", requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        logger.debug("Validating IP whitelist for request: ip={}, uri={}, method={}, client={}", 
                    clientIp, requestUri, httpMethod, clientId);

        try {
            // Validate IP against whitelist
            ApiWhitelistService.WhitelistValidationResult validationResult = 
                whitelistService.validateIpAccess(clientIp, requestUri, httpMethod, clientId, userAgent);

            if (validationResult.isAllowed()) {
                logger.debug("IP whitelist validation passed for {}: {}", clientIp, validationResult.getReason());
                filterChain.doFilter(request, response);
            } else {
                handleBlockedRequest(request, response, clientIp, validationResult.getReason());
            }

        } catch (Exception e) {
            logger.error("Error during IP whitelist validation for {}: {}", clientIp, e.getMessage(), e);
            
            if (enforceMode) {
                // In enforce mode, block on errors
                handleBlockedRequest(request, response, clientIp, "Whitelist validation error: " + e.getMessage());
            } else {
                // In permissive mode, allow on errors but log them
                logger.warn("Allowing request due to whitelist validation error (permissive mode): {}", e.getMessage());
                filterChain.doFilter(request, response);
            }
        }
    }

    /**
     * Handles a blocked request by returning an appropriate error response.
     */
    private void handleBlockedRequest(HttpServletRequest request, HttpServletResponse response, 
                                    String clientIp, String reason) throws IOException {
        
        logger.warn("Blocking request from {}: {} - URI: {}", clientIp, reason, request.getRequestURI());

        // Add delay to slow down potential attackers
        if (blockResponseDelayMs > 0) {
            try {
                Thread.sleep(blockResponseDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.debug("Block response delay interrupted");
            }
        }

        // Set response status and headers
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Add security headers
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");

        // Return a generic error message for security
        String errorResponse = """
            {
                "error": "Forbidden",
                "message": "Access denied",
                "status": 403,
                "timestamp": "%s"
            }
            """.formatted(java.time.Instant.now());

        response.getWriter().write(errorResponse);
        response.getWriter().flush();
    }

    /**
     * Extracts the client IP address from the request, handling various proxy headers.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            // X-Forwarded-For can contain multiple IPs, take the first one
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        String xForwardedProto = request.getHeader("X-Forwarded-Proto");
        if (xForwardedProto != null) {
            String cfConnectingIp = request.getHeader("CF-Connecting-IP");
            if (cfConnectingIp != null && !cfConnectingIp.isEmpty()) {
                return cfConnectingIp; // Cloudflare
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * Extracts the OAuth2 client ID from the request if available.
     */
    private String extractClientId(HttpServletRequest request) {
        // Try to extract from Authorization header (Basic auth)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            try {
                String encoded = authHeader.substring(6);
                String decoded = new String(java.util.Base64.getDecoder().decode(encoded));
                String[] credentials = decoded.split(":", 2);
                if (credentials.length == 2) {
                    return credentials[0]; // client_id is typically the username in Basic auth
                }
            } catch (Exception e) {
                logger.debug("Failed to extract client ID from Basic auth header: {}", e.getMessage());
            }
        }

        // Try to extract from form parameters or query parameters
        String clientId = request.getParameter("client_id");
        if (clientId != null && !clientId.isEmpty()) {
            return clientId;
        }

        // Could add more extraction methods here (JWT bearer tokens, etc.)
        
        return null;
    }

    /**
     * Checks if the request path should be excluded from whitelist validation.
     */
    private boolean isExcludedPath(String requestUri) {
        if (requestUri == null) {
            return false;
        }

        return EXCLUDED_PATHS.stream().anyMatch(requestUri::startsWith);
    }

    /**
     * Determines if this filter should process the request.
     * Can be overridden to add custom logic for when to apply whitelist filtering.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Only apply to specific endpoints if needed
        String requestUri = request.getRequestURI();
        
        // Skip if whitelist is disabled
        if (!whitelistEnabled) {
            return true;
        }
        
        // Skip for excluded paths
        if (isExcludedPath(requestUri)) {
            return true;
        }

        // Could add more conditions here based on requirements
        // For example, only apply to OAuth2 endpoints:
        // return !requestUri.startsWith("/oauth2/") && !requestUri.startsWith("/api/");
        
        return false;
    }

    // Getters for testing and configuration
    public boolean isWhitelistEnabled() {
        return whitelistEnabled;
    }

    public long getBlockResponseDelayMs() {
        return blockResponseDelayMs;
    }

    public boolean isEnforceMode() {
        return enforceMode;
    }
}