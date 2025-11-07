package com.codzs.framework.logger.listener;

import com.codzs.framework.entity.BaseEntity;
import com.codzs.framework.logger.context.CorrelationIdContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

/**
 * MongoDB event listener that automatically injects correlation IDs into entities
 * before they are converted and saved to the database.
 * 
 * This listener ensures that all entities extending BaseEntity have a correlation ID
 * that matches the current request's correlation ID from the ThreadLocal context.
 * 
 * The correlation ID injection happens automatically for:
 * - New entity creation (insert operations)
 * - Entity updates (when correlationId is null)
 * - All entities extending BaseEntity
 * 
 * This provides automatic correlation between:
 * - HTTP request logs (via MDC)
 * - Database operations (via entity correlationId field)
 * - Business operations (via the same correlation ID)
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
public class CorrelationIdMongoEventListener extends AbstractMongoEventListener<BaseEntity> {
    
    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdMongoEventListener.class);
    
    /**
     * Handles the BeforeConvert event to inject correlation ID into entities
     * before they are converted to MongoDB documents.
     * 
     * This method is called automatically by Spring Data MongoDB before
     * any entity extending BaseEntity is saved to the database.
     * 
     * @param event the before convert event containing the entity
     */
    @Override
    public void onBeforeConvert(BeforeConvertEvent<BaseEntity> event) {
        BaseEntity entity = event.getSource();
        
        if (entity == null) {
            logger.debug("Skipping correlation ID injection for null entity");
            return;
        }
        
        try {
            injectCorrelationId(entity);
        } catch (Exception e) {
            // Don't fail the operation if correlation ID injection fails
            logger.warn("Failed to inject correlation ID for entity: {}", 
                    entity.getClass().getSimpleName(), e);
        }
    }
    
    /**
     * Injects the correlation ID into the entity if it doesn't already have one.
     * 
     * The injection strategy:
     * 1. If entity already has a correlationId, preserve it
     * 2. If no correlationId and one exists in ThreadLocal, use it
     * 3. If no correlationId anywhere, generate a new one
     * 
     * @param entity the entity to inject correlation ID into
     */
    private void injectCorrelationId(BaseEntity entity) {
        // Check if entity already has a correlation ID
        if (hasValidCorrelationId(entity.getCorrelationId())) {
            logger.debug("Entity {} already has correlation ID: {}", 
                    entity.getClass().getSimpleName(), entity.getCorrelationId());
            return;
        }
        
        // Try to get correlation ID from ThreadLocal context
        String contextCorrelationId = CorrelationIdContext.getCorrelationId();
        
        if (hasValidCorrelationId(contextCorrelationId)) {
            // Use correlation ID from current request context
            entity.setCorrelationId(contextCorrelationId);
            logger.debug("Injected correlation ID from context: {} into entity: {}", 
                    contextCorrelationId, entity.getClass().getSimpleName());
        } else {
            // Generate new correlation ID if none available
            // This can happen for background operations or async processes
            String generatedId = CorrelationIdContext.generateCorrelationId();
            entity.setCorrelationId(generatedId);
            logger.debug("Generated and injected new correlation ID: {} into entity: {}", 
                    generatedId, entity.getClass().getSimpleName());
        }
    }
    
    /**
     * Checks if a correlation ID is valid (not null, not empty, not just whitespace).
     * 
     * @param correlationId the correlation ID to validate
     * @return true if valid, false otherwise
     */
    private boolean hasValidCorrelationId(String correlationId) {
        return correlationId != null && !correlationId.trim().isEmpty();
    }
}