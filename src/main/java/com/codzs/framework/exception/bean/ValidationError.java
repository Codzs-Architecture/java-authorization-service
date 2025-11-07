package com.codzs.framework.exception.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * Represents a single validation error.
 * 
 * @author CodeGeneration Framework
 * @since 1.0
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationError {
    
    /**
     * The field that failed validation
     */
    private String field;
    
    /**
     * The value that was rejected
     */
    private Object rejectedValue;
    
    /**
     * The validation error message
     */
    private String message;
    
    /**
     * The validation constraint that was violated
     */
    private String constraint;
}