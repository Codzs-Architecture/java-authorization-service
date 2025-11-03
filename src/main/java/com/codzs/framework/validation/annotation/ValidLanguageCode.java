package com.codzs.framework.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import com.codzs.framework.validation.validator.LanguageCodeValidator;

/**
 * Custom validation annotation to validate language codes against ISO 639-1 standard.
 * 
 * Usage:
 * @ValidLanguageCode
 * private String language;
 * 
 * @ValidLanguageCode(allowNull = true, useDefaultOnInvalid = true)
 * private String language;
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = LanguageCodeValidator.class)
public @interface ValidLanguageCode {
    
    String message() default "Invalid language code. Must be a valid ISO 639-1 language code.";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Allow null values (useful for optional fields).
     */
    boolean allowNull() default false;
    
    /**
     * Use default language code if provided value is invalid.
     * When true, validation always passes but invalid values are replaced with default.
     */
    boolean useDefaultOnInvalid() default false;
}