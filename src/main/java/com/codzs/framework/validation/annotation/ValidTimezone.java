package com.codzs.framework.annotation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import com.codzs.framework.validation.validator.TimezoneValidator;

/**
 * Custom validation annotation to validate timezone IDs against available timezone IDs.
 * 
 * Usage:
 * @ValidTimezone
 * private String timezone;
 * 
 * @ValidTimezone(allowNull = true, useDefaultOnInvalid = true)
 * private String timezone;
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = TimezoneValidator.class)
public @interface ValidTimezone {
    
    String message() default "Invalid timezone. Must be a valid timezone ID.";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Allow null values (useful for optional fields).
     */
    boolean allowNull() default false;
    
    /**
     * Use default timezone if provided value is invalid.
     * When true, validation always passes but invalid values are replaced with default.
     */
    boolean useDefaultOnInvalid() default false;
}