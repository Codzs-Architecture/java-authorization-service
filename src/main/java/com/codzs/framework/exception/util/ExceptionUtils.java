package com.codzs.framework.exception.util;

import com.codzs.framework.exception.type.DuplicateResourceException;
import com.codzs.framework.exception.type.InvalidOperationException;
import com.codzs.framework.exception.type.ResourceNotFoundException;

/**
 * Utility class for common exception scenarios.
 * Provides factory methods for creating standardized exceptions.
 * 
 * @author CodeGeneration Framework
 * @since 1.0
 */
public final class ExceptionUtils {
    
    private ExceptionUtils() {
        // Utility class
    }
    
    // Resource Not Found utilities
    
    public static ResourceNotFoundException organizationNotFound(String organizationId) {
        return new ResourceNotFoundException("Organization", "id", organizationId);
    }
    
    public static ResourceNotFoundException tenantNotFound(String tenantId) {
        return new ResourceNotFoundException("Tenant", "id", tenantId);
    }
    
    public static ResourceNotFoundException userNotFound(String userId) {
        return new ResourceNotFoundException("User", "id", userId);
    }
    
    public static ResourceNotFoundException domainNotFound(String domainId) {
        return new ResourceNotFoundException("Domain", "id", domainId);
    }
    
    public static ResourceNotFoundException planNotFound(String planId) {
        return new ResourceNotFoundException("Plan", "id", planId);
    }
    
    public static ResourceNotFoundException settingNotFound(String settingKey) {
        return new ResourceNotFoundException("Setting", "key", settingKey);
    }
    
    // Duplicate Resource utilities
    
    public static DuplicateResourceException organizationNameExists(String name) {
        return new DuplicateResourceException("Organization", "name", name);
    }
    
    public static DuplicateResourceException domainNameExists(String domainName) {
        return new DuplicateResourceException("Domain", "name", domainName);
    }
    
    public static DuplicateResourceException userEmailExists(String email) {
        return new DuplicateResourceException("User", "email", email);
    }
    
    // Invalid Operation utilities
    
    public static InvalidOperationException cannotDeletePrimaryDomain() {
        return new InvalidOperationException("delete primary domain", 
                "Cannot delete the primary domain. Set another domain as primary first.");
    }
    
    public static InvalidOperationException cannotVerifyDomain(String reason) {
        return new InvalidOperationException("verify domain", reason);
    }
    
    public static InvalidOperationException cannotChangePlan(String currentPlan, String targetPlan, String reason) {
        return new InvalidOperationException(
                String.format("change plan from %s to %s", currentPlan, targetPlan), 
                reason);
    }
    
    public static InvalidOperationException organizationNotActive(String organizationId) {
        return new InvalidOperationException("perform operation", 
                String.format("Organization %s is not in active status", organizationId));
    }
    
    public static InvalidOperationException domainNotVerified(String domainName) {
        return new InvalidOperationException("use domain", 
                String.format("Domain %s is not verified", domainName));
    }
}