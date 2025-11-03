package com.codzs.framework.validation.validator;

import com.codzs.framework.validation.annotation.ValidUUID;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Validator implementation for UUID format validation.
 * Validates that a string contains a valid UUID format.
 * 
 * @author CodeGeneration Framework
 * @since 1.0
 */
public class UUIDValidator implements ConstraintValidator<ValidUUID, Object> {
    
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );
    
    private boolean allowNull;
    private String fieldName;
    
    @Override
    public void initialize(ValidUUID constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
        this.fieldName = constraintAnnotation.fieldName();
    }
    
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // Handle null values - for UUID types, we're more strict by default
        if (value == null) {
            // For UUID types in path variables, null should generally not be allowed
            // unless explicitly specified
            return allowNull;
        }
        
        // Handle UUID type - already valid by type system, just check for null
        if (value instanceof UUID) {
            return true; // UUID type guarantees valid format
        }
        
        // Handle String type
        if (value instanceof String) {
            String stringValue = (String) value;
            
            // Empty strings
            if (!StringUtils.hasText(stringValue)) {
                return allowNull;
            }
            
            String trimmedValue = stringValue.trim();
            
            // Validate UUID format using regex first (faster)
            if (!UUID_PATTERN.matcher(trimmedValue).matches()) {
                setCustomErrorMessage(context, trimmedValue);
                return false;
            }
            
            // Additional validation - try to parse as UUID
            try {
                UUID.fromString(trimmedValue);
                return true;
            } catch (IllegalArgumentException e) {
                setCustomErrorMessage(context, trimmedValue);
                return false;
            }
        }
        
        // Unsupported type
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
            "UUID validation can only be applied to String or UUID fields"
        ).addConstraintViolation();
        
        return false;
    }
    
    private void setCustomErrorMessage(ConstraintValidatorContext context, String value) {
        context.disableDefaultConstraintViolation();
        
        String message;
        if (StringUtils.hasText(fieldName)) {
            message = String.format("Invalid %s format: '%s'. Expected valid UUID format (e.g., 123e4567-e89b-12d3-a456-426614174000)", 
                    fieldName, value);
        } else {
            message = String.format("Invalid UUID format: '%s'. Expected format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx", 
                    value);
        }
        
        context.buildConstraintViolationWithTemplate(message)
               .addConstraintViolation();
    }
}