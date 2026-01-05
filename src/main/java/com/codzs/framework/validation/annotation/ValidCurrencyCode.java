package com.codzs.framework.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import com.codzs.framework.validation.validator.CurrencyCodeValidator;

/**
 * Custom validation annotation to validate currency codes against ISO 4217 standard.
 * 
 * Usage:
 * @ValidCurrencyCode
 * private String currency;
 * 
 * @ValidCurrencyCode(allowNull = true, useDefaultOnInvalid = true)
 * private String currency;
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CurrencyCodeValidator.class)
public @interface ValidCurrencyCode {
    
    String message() default "Invalid currency code. Must be a valid ISO 4217 currency code.";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Allow null values (useful for optional fields).
     */
    boolean allowNull() default false;
    
    /**
     * Use default currency code if provided value is invalid.
     * When true, validation always passes but invalid values are replaced with default.
     */
    boolean useDefaultOnInvalid() default false;
}