package com.codzs.framework.annotation.validation;

import com.codzs.framework.base.ConfigParameterBase;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Validator implementation for ValidDynamicEnum annotation.
 * Validates string values against dynamic configuration enums.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
public class DynamicEnumValidator implements ConstraintValidator<ValidDynamicEnum, String> {

    @Autowired
    private ApplicationContext applicationContext;

    private Class<? extends ConfigParameterBase> enumClass;
    private boolean allowNull;
    private boolean useDefaultOnInvalid;

    @Override
    public void initialize(ValidDynamicEnum constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
        this.allowNull = constraintAnnotation.allowNull();
        this.useDefaultOnInvalid = constraintAnnotation.useDefaultOnInvalid();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Handle null values
        if (value == null) {
            return allowNull;
        }
        
        // Handle empty/blank values
        if (value.trim().isEmpty()) {
            return allowNull;
        }

        try {
            // Get the dynamic enum bean from Spring context
            ConfigParameterBase enumBean = applicationContext.getBean(enumClass);
            
            // Check if the value is valid
            boolean isValid = enumBean.isValidOption(value);
            
            // If using default on invalid and value is invalid, check if default exists
            if (!isValid && useDefaultOnInvalid) {
                return enumBean.hasDefaultValue();
            }
            
            return isValid;
            
        } catch (Exception e) {
            // If we can't get the bean or validate, log error and fail validation
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Unable to validate against " + enumClass.getSimpleName() + ": " + e.getMessage()
            ).addConstraintViolation();
            return false;
        }
    }
}