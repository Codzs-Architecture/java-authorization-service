package com.codzs.framework.config.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of Spring Data's AuditorAware interface to automatically 
 * populate audit fields with the current authenticated user.
 * 
 * This class integrates with Spring Security to extract the current user
 * from various authentication sources including JWT tokens, OAuth2, and
 * standard user details.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Slf4j
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    /**
     * Default system user ID used when no authenticated user is available.
     * This is used for system-initiated operations like batch jobs or migrations.
     */
    private static final String SYSTEM_USER = "SYSTEM";
    
    /**
     * Default anonymous user ID used when operations are performed without authentication.
     */
    private static final String ANONYMOUS_USER = "ANONYMOUS";

    /**
     * Returns the current auditor (user ID) from the security context.
     * 
     * Priority order for user identification:
     * 1. JWT token 'sub' claim (most common in OAuth2 scenarios)
     * 2. JWT token 'user_id' claim (custom claim)
     * 3. UserDetails username (traditional Spring Security)
     * 4. Authentication principal name (fallback)
     * 5. SYSTEM (for system operations)
     * 6. ANONYMOUS (when no authentication available)
     * 
     * @return Optional containing the current user ID, never empty
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                log.debug("No authentication found, using ANONYMOUS user for audit");
                return Optional.of(ANONYMOUS_USER);
            }

            // Handle JWT Authentication Token (OAuth2 Resource Server)
            if (authentication instanceof JwtAuthenticationToken jwtAuthToken) {
                String userId = extractUserIdFromJwt(jwtAuthToken.getToken());
                if (userId != null) {
                    log.debug("Extracted user ID from JWT: {}", userId);
                    return Optional.of(userId);
                }
            }

            // Handle UserDetails (traditional Spring Security)
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                String username = userDetails.getUsername();
                log.debug("Extracted user ID from UserDetails: {}", username);
                return Optional.of(username);
            }

            // Handle custom principal objects
            if (principal instanceof String principalName) {
                log.debug("Extracted user ID from principal name: {}", principalName);
                return Optional.of(principalName);
            }

            // Handle authentication name as fallback
            String authName = authentication.getName();
            if (authName != null && !authName.trim().isEmpty() && !"anonymousUser".equals(authName)) {
                log.debug("Extracted user ID from authentication name: {}", authName);
                return Optional.of(authName);
            }

            log.debug("Could not extract user ID from authentication, using SYSTEM user");
            return Optional.of(SYSTEM_USER);

        } catch (Exception e) {
            log.warn("Error extracting current auditor, falling back to SYSTEM user", e);
            return Optional.of(SYSTEM_USER);
        }
    }

    /**
     * Extracts user ID from JWT token claims.
     * 
     * @param jwt The JWT token
     * @return User ID extracted from token claims, or null if not found
     */
    private String extractUserIdFromJwt(Jwt jwt) {
        try {
            // Try 'sub' claim first (standard OAuth2 subject claim)
            String subject = jwt.getSubject();
            if (subject != null && !subject.trim().isEmpty()) {
                return subject;
            }

            // Try custom 'user_id' claim
            String userId = jwt.getClaimAsString("user_id");
            if (userId != null && !userId.trim().isEmpty()) {
                return userId;
            }

            // Try 'preferred_username' claim (common in Keycloak and other providers)
            String preferredUsername = jwt.getClaimAsString("preferred_username");
            if (preferredUsername != null && !preferredUsername.trim().isEmpty()) {
                return preferredUsername;
            }

            // Try 'email' claim as fallback
            String email = jwt.getClaimAsString("email");
            if (email != null && !email.trim().isEmpty()) {
                return email;
            }

            log.debug("No suitable user identifier found in JWT claims");
            return null;

        } catch (Exception e) {
            log.warn("Error extracting user ID from JWT token", e);
            return null;
        }
    }

    /**
     * Utility method to get the current user ID synchronously.
     * Useful for manual audit operations or testing.
     * 
     * @return Current user ID or SYSTEM if not available
     */
    public String getCurrentUserId() {
        return getCurrentAuditor().orElse(SYSTEM_USER);
    }

    /**
     * Checks if the current context has an authenticated user.
     * 
     * @return true if there's an authenticated user, false otherwise
     */
    public boolean hasAuthenticatedUser() {
        String currentUser = getCurrentUserId();
        return !SYSTEM_USER.equals(currentUser) && !ANONYMOUS_USER.equals(currentUser);
    }
}