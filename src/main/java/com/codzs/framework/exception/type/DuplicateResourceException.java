package com.codzs.framework.exception.type;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to create a resource that already exists.
 * Maps to HTTP 409 status code.
 * 
 * @author CodeGeneration Framework
 * @since 1.0
 */
public class DuplicateResourceException extends BusinessException {
    
    public DuplicateResourceException(String resourceType, String field, Object value) {
        super("DUPLICATE_RESOURCE", 
              String.format("%s already exists with %s: %s", resourceType, field, value), 
              HttpStatus.CONFLICT);
    }
    
    public DuplicateResourceException(String message) {
        super("DUPLICATE_RESOURCE", message, HttpStatus.CONFLICT);
    }
}