package com.codzs.framework.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import com.codzs.framework.validation.annotation.ValidObjectId;

import java.util.regex.Pattern;

/**
 * Validator for ValidObjectId annotation.
 * Validates MongoDB ObjectId format (24-character hexadecimal string).
 * 
 * ObjectId format: 24 characters, each character is 0-9 or a-f (case insensitive)
 * Example: "690974b7183e6714ad3485f2"
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
public class ObjectIdValidator implements ConstraintValidator<ValidObjectId, String> {

    /**
     * Regular expression pattern for MongoDB ObjectId validation.
     * Matches exactly 24 hexadecimal characters (0-9, a-f, A-F).
     */
    private static final Pattern OBJECT_ID_PATTERN = Pattern.compile("^[a-fA-F0-9]{24}$");

    private boolean allowNull;

    @Override
    public void initialize(ValidObjectId constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Handle null values
        if (value == null) {
            return allowNull;
        }

        // Handle empty strings
        if (value.trim().isEmpty()) {
            return false;
        }

        // Validate ObjectId format using regex pattern
        return OBJECT_ID_PATTERN.matcher(value).matches();
    }
}