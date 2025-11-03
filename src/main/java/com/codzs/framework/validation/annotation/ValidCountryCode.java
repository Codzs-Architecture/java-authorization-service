package com.codzs.framework.annotation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import com.codzs.framework.validation.validator.CountryCodeValidator;

/**
 * Custom validation annotation to validate country codes against ISO 3166-1 alpha-2 standard.
 * 
 * Usage:
 * @ValidCountryCode
 * private String country;
 * 
 * @ValidCountryCode(allowNull = true, useDefaultOnInvalid = true)
 * private String country;
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CountryCodeValidator.class)
public @interface ValidCountryCode {
    
    String message() default "Invalid country code. Must be a valid ISO 3166-1 alpha-2 country code.";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Allow null values (useful for optional fields).
     */
    boolean allowNull() default false;
    
    /**
     * Use default country code if provided value is invalid.
     * When true, validation always passes but invalid values are replaced with default.
     */
    boolean useDefaultOnInvalid() default false;
}