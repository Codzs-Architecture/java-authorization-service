package com.codzs.framework.validation.annotation;

import com.codzs.framework.constant.ConfigParameterBase;
import com.codzs.framework.validation.validator.DynamicEnumValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation to validate string values against
 * dynamic configuration enums that extend ConfigParameterBase.
 * 
 * Usage:
 * @ValidDynamicEnum(enumClass = OrganizationTypeEnum.class)
 * private String organizationType;
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DynamicEnumValidator.class)
public @interface ValidDynamicEnum {
    
    String message() default "Invalid value. Must be one of the configured options for {enumClass}.";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * The dynamic enum class that extends ConfigParameterBase
     */
    Class<? extends ConfigParameterBase> enumClass();
    
    /**
     * Allow null values (useful for optional fields)
     */
    boolean allowNull() default false;
    
    /**
     * Use default value if provided value is invalid
     */
    boolean useDefaultOnInvalid() default false;
}