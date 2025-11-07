package com.codzs.framework.exception.type;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an operation is not valid in the current context.
 * Maps to HTTP 422 status code.
 * 
 * @author CodeGeneration Framework
 * @since 1.0
 */
public class InvalidOperationException extends BusinessException {
    
    public InvalidOperationException(String operation, String reason) {
        super("INVALID_OPERATION", 
              String.format("Operation '%s' is not valid: %s", operation, reason), 
              HttpStatus.UNPROCESSABLE_ENTITY);
    }
    
    public InvalidOperationException(String message) {
        super("INVALID_OPERATION", message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}