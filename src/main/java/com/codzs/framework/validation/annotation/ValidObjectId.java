package com.codzs.framework.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import com.codzs.framework.validation.validator.ObjectIdValidator;

/**
 * Custom validation annotation to validate MongoDB ObjectId format.
 * Validates that a string is a valid 24-character hexadecimal ObjectId.
 * 
 * Usage:
 * @ValidObjectId
 * private String organizationId;
 * 
 * @ValidObjectId(message = "Custom error message")
 * private String entityId;
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ObjectIdValidator.class)
public @interface ValidObjectId {
    
    String message() default "Invalid ObjectId format. Must be a 24-character hexadecimal string.";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Allow null values (useful for optional fields).
     */
    boolean allowNull() default false;
}