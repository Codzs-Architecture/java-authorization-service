package com.codzs.temp;

import com.codzs.entity.organization.Organization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Example utility class to demonstrate automatic audit field population.
 * This class shows how audit fields are automatically populated when
 * saving entities to MongoDB.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Slf4j
@Component
public class AuditTestExample {

    /**
     * Demonstrates automatic audit field population during entity creation.
     * 
     * @param mongoTemplate MongoDB template for saving entities
     * @return Created organization with populated audit fields
     */
    public Organization demonstrateAuditCreation(MongoTemplate mongoTemplate) {
        log.info("=== Demonstrating Automatic Audit Field Population ===");
        
        // Simulate authenticated user
        setAuthenticatedUser("user123");
        
        // Create new organization
        Organization org = new Organization();
        org.setName("Test Organization");
        org.setAbbr("TEST");
        org.setDisplayName("Test Organization Inc.");
        org.setDescription("Test organization for audit demonstration");
        org.setOrganizationType("ENTERPRISE");
        org.setBillingEmail("billing@test.com");
        
        log.info("Before save - Created fields: createdDate={}, createdBy={}, lastModifiedDate={}, lastModifiedBy={}", 
                org.getCreatedDate(), org.getCreatedBy(), org.getLastModifiedDate(), org.getLastModifiedBy());
        
        // Save entity - audit fields will be automatically populated
        Organization savedOrg = mongoTemplate.save(org);
        
        log.info("After save - Created fields: createdDate={}, createdBy={}, lastModifiedDate={}, lastModifiedBy={}", 
                savedOrg.getCreatedDate(), savedOrg.getCreatedBy(), savedOrg.getLastModifiedDate(), savedOrg.getLastModifiedBy());
        
        return savedOrg;
    }
    
    /**
     * Demonstrates automatic audit field population during entity update.
     * 
     * @param mongoTemplate MongoDB template for saving entities
     * @param existingOrg Organization to update
     * @return Updated organization with refreshed audit fields
     */
    public Organization demonstrateAuditUpdate(MongoTemplate mongoTemplate, Organization existingOrg) {
        log.info("=== Demonstrating Automatic Update Audit ===");
        
        // Simulate different authenticated user
        setAuthenticatedUser("user456");
        
        // Store original values for comparison
        String originalCreatedBy = existingOrg.getCreatedBy();
        var originalCreatedDate = existingOrg.getCreatedDate();
        
        log.info("Before update - Original: createdDate={}, createdBy={}, lastModifiedDate={}, lastModifiedBy={}", 
                existingOrg.getCreatedDate(), existingOrg.getCreatedBy(), 
                existingOrg.getLastModifiedDate(), existingOrg.getLastModifiedBy());
        
        // Update entity
        existingOrg.setDescription("Updated description");
        
        // Save entity - only update audit fields will be modified
        Organization updatedOrg = mongoTemplate.save(existingOrg);
        
        log.info("After update - Updated: createdDate={}, createdBy={}, lastModifiedDate={}, lastModifiedBy={}", 
                updatedOrg.getCreatedDate(), updatedOrg.getCreatedBy(), 
                updatedOrg.getLastModifiedDate(), updatedOrg.getLastModifiedBy());
        
        // Verify creation fields were NOT modified
        if (originalCreatedBy.equals(updatedOrg.getCreatedBy()) && 
            originalCreatedDate.equals(updatedOrg.getCreatedDate())) {
            log.info("✓ SUCCESS: Creation audit fields were preserved during update");
        } else {
            log.error("✗ ERROR: Creation audit fields were modified during update!");
        }
        
        return updatedOrg;
    }
    
    /**
     * Simulates an authenticated user in the security context.
     * 
     * @param username The username to authenticate
     */
    private void setAuthenticatedUser(String username) {
        Authentication auth = new UsernamePasswordAuthenticationToken(username, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
        log.debug("Set authenticated user: {}", username);
    }
    
    /**
     * Clears the security context.
     */
    public void clearAuthentication() {
        SecurityContextHolder.clearContext();
        log.debug("Cleared security context");
    }
}