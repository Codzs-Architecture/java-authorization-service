package com.codzs.framework.mapper;

import java.time.Instant;

import org.bson.types.ObjectId;

import com.codzs.framework.aware.audit.AuditorAwareImpl;
import com.codzs.framework.context.spring.SpringContextHelper;

public interface BaseMapper {
    default String generateId() {
        return ObjectId.get().toString();
    }    
    
    /**
     * Generates current date time for audit fields.
     * 
     * @return Current timestamp as Instant
     */
    default Instant generateCurrentDateTime() {
        return Instant.now();
    }
    
    /**
     * Gets the current user ID from security context for audit fields.
     * Reuses existing AuditorAwareImpl with qualified bean name to avoid ambiguity.
     * 
     * @return Current user ID from AuditorAwareImpl
     */
    default String getCurrentUserId() {
        try {
            // Use qualified bean name to avoid ambiguity between auditorAwareImpl and auditorProvider
            AuditorAwareImpl auditorAware = SpringContextHelper.getBean("auditorAwareImpl", AuditorAwareImpl.class);
            return auditorAware.getCurrentUserId();
        } catch (Exception e) {
            // Fallback to SYSTEM if spring context is not available or bean not found
            return "SYSTEM";
        }
    }
}
