package com.codzs.framework.logger.context;

import com.codzs.framework.constant.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * ThreadLocal context for managing correlation IDs throughout the request lifecycle.
 * This class provides a way to store and retrieve correlation IDs that are used
 * for both logging and database auditing purposes.
 * 
 * The correlation ID is automatically:
 * - Extracted from HTTP headers or generated for new requests
 * - Added to MDC for logging correlation
 * - Injected into database entities during save operations
 * - Cleaned up after request completion
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
public class CorrelationIdContext {
    
    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdContext.class);
    
    /**
     * ThreadLocal storage for correlation ID to ensure thread safety
     * and proper isolation between concurrent requests.
     */
    private static final ThreadLocal<String> correlationIdHolder = new ThreadLocal<>();
    
    /**
     * Sets the correlation ID for the current thread.
     * This method is typically called by the CorrelationIdFilter
     * at the beginning of request processing.
     * 
     * @param correlationId the correlation ID to set for the current thread
     */
    public static void setCorrelationId(String correlationId) {
        if (correlationId != null && !correlationId.trim().isEmpty()) {
            correlationIdHolder.set(correlationId.trim());
            logger.debug("Set correlation ID: {}", correlationId);
        } else {
            logger.warn("Attempted to set null or empty correlation ID");
        }
    }
    
    /**
     * Retrieves the correlation ID for the current thread.
     * 
     * @return the correlation ID for the current thread, or null if not set
     */
    public static String getCorrelationId() {
        return correlationIdHolder.get();
    }
    
    /**
     * Checks if a correlation ID is currently set for the current thread.
     * 
     * @return true if correlation ID is set and not empty, false otherwise
     */
    public static boolean hasCorrelationId() {
        String correlationId = correlationIdHolder.get();
        return correlationId != null && !correlationId.trim().isEmpty();
    }
    
    /**
     * Generates a new correlation ID if one is not already set.
     * This method is useful for background processes or async operations
     * that may not have a correlation ID from an HTTP request.
     * 
     * @return the existing correlation ID if present, or a newly generated one
     */
    public static String getOrGenerateCorrelationId() {
        String existingId = getCorrelationId();
        if (existingId != null && !existingId.trim().isEmpty()) {
            return existingId;
        }
        
        String newId = generateCorrelationId();
        setCorrelationId(newId);
        logger.debug("Generated new correlation ID: {}", newId);
        return newId;
    }
    
    /**
     * Generates a new unique correlation ID using UUID and timestamp.
     * The format follows the pattern: req_<timestamp>_<uuid_suffix>
     * 
     * @return a newly generated correlation ID
     */
    public static String generateCorrelationId() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        long timestamp = System.currentTimeMillis();
        return CommonConstants.CORRELATION_ID_PREFIX + timestamp + "_" + uuid.substring(0, 8);
    }
    
    /**
     * Clears the correlation ID for the current thread.
     * This method MUST be called to prevent memory leaks and
     * ensure that correlation IDs don't leak between requests.
     * 
     * It's typically called by the CorrelationIdFilter in a finally block.
     */
    public static void clear() {
        String correlationId = correlationIdHolder.get();
        if (correlationId != null) {
            logger.debug("Clearing correlation ID: {}", correlationId);
        }
        correlationIdHolder.remove();
    }
    
    /**
     * Executes a runnable with a specific correlation ID.
     * This method is useful for async operations or background tasks
     * that need to maintain correlation context.
     * 
     * @param correlationId the correlation ID to use during execution
     * @param runnable the code to execute with the correlation ID
     */
    public static void executeWithCorrelationId(String correlationId, Runnable runnable) {
        String previousId = getCorrelationId();
        try {
            setCorrelationId(correlationId);
            runnable.run();
        } finally {
            if (previousId != null) {
                setCorrelationId(previousId);
            } else {
                clear();
            }
        }
    }
    
    /**
     * Creates a new correlation ID context for child operations.
     * This method generates a child correlation ID that includes
     * the parent correlation ID for tracing purposes.
     * 
     * @param operation the operation name to include in the child ID
     * @return a new correlation ID that includes the parent context
     */
    public static String createChildCorrelationId(String operation) {
        String parentId = getCorrelationId();
        String childSuffix = operation + "_" + System.currentTimeMillis();
        
        if (parentId != null) {
            return parentId + "_" + childSuffix;
        } else {
            return CommonConstants.CORRELATION_ID_PREFIX + childSuffix;
        }
    }
}