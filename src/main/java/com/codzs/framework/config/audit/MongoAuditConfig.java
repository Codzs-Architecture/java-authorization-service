package com.codzs.framework.config.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * MongoDB auditing configuration for automatic population of audit fields.
 * 
 * This configuration enables Spring Data MongoDB's auditing capabilities,
 * which automatically populate fields annotated with:
 * - @CreatedDate: automatically set on entity creation
 * - @LastModifiedDate: automatically updated on entity modification  
 * - @CreatedBy: automatically set with current user on creation
 * - @LastModifiedBy: automatically updated with current user on modification
 * 
 * The auditing infrastructure integrates with Spring Security to capture
 * the current authenticated user through the AuditorAware implementation.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Slf4j
@Configuration
@EnableMongoAuditing(auditorAwareRef = "auditorProvider")
public class MongoAuditConfig {

    /**
     * Configures the AuditorAware bean that provides the current user
     * for automatic audit field population.
     * 
     * @return AuditorAware implementation for capturing current user
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        log.info("Configuring MongoDB auditing with AuditorAware implementation");
        return new AuditorAwareImpl();
    }
}