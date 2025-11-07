package com.codzs.framework.annotation.validation;

import java.lang.annotation.*;

/**
 * Annotation to mark DTOs that should have default values applied.
 * When a DTO is annotated with this, the validation framework will
 * automatically call the applyDefaults() method on the DTO instance.
 * 
 * Usage:
 * @ApplyDefaults
 * public class OrganizationSettingsRequestDto {
 *     // ... fields
 *     
 *     public void applyDefaults() {
 *         if (this.language == null) this.language = "en";
 *         // ... other defaults
 *     }
 * }
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApplyDefaults {
    
    /**
     * Whether to apply defaults before validation
     */
    boolean beforeValidation() default true;
    
    /**
     * Method name to call for applying defaults
     */
    String methodName() default "applyDefaults";
}