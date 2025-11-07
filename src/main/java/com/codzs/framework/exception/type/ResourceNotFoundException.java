package com.codzs.framework.exception.type;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource is not found.
 * Maps to HTTP 404 status code.
 * 
 * @author CodeGeneration Framework
 * @since 1.0
 */
public class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String resourceType, String resourceId) {
        super("RESOURCE_NOT_FOUND", 
              String.format("%s not found with id: %s", resourceType, resourceId), 
              HttpStatus.NOT_FOUND);
    }
    
    public ResourceNotFoundException(String resourceType, String field, Object value) {
        super("RESOURCE_NOT_FOUND", 
              String.format("%s not found with %s: %s", resourceType, field, value), 
              HttpStatus.NOT_FOUND);
    }
    
    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }
}