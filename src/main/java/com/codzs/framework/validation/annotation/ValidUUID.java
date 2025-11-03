package com.codzs.framework.validation.annotation;

import com.codzs.framework.validation.validator.UUIDValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation to ensure a field contains a valid UUID format.
 * Can be used on String or UUID fields that should contain valid UUID values.
 * 
 * Usage examples:
 * - @ValidUUID on UUID parameters (validates non-null by default)
 * - @ValidUUID(allowNull = true) on optional UUID parameters
 * - @ValidUUID(fieldName = "Organization ID") for custom error messages
 * 
 * @author CodeGeneration Framework
 * @since 1.0
 */
@Documented
@Constraint(validatedBy = UUIDValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUUID {
    
    /**
     * Error message when validation fails
     */
    String message() default "Invalid UUID format";
    
    /**
     * Validation groups
     */
    Class<?>[] groups() default {};
    
    /**
     * Payload for metadata
     */
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Whether null values are allowed.
     * For UUID parameters, this defaults to false (UUID should not be null).
     * For String parameters, this defaults to true (String can be null/empty).
     */
    boolean allowNull() default true;
    
    /**
     * Custom field name for error messages (e.g., "Organization ID", "Domain ID")
     */
    String fieldName() default "";
}