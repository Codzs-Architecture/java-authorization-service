package com.codzs.framework.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codzs.framework.service.localization.LocalizationCodeService;
import com.codzs.framework.validation.annotation.ValidLanguageCode;

/**
 * Validator for ValidLanguageCode annotation.
 * Validates language codes against ISO 639-1 standard.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
public class LanguageCodeValidator implements ConstraintValidator<ValidLanguageCode, String> {

    @Autowired
    private LocalizationCodeService localizationCodeService;

    private boolean allowNull;
    private boolean useDefaultOnInvalid;

    @Override
    public void initialize(ValidLanguageCode constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
        this.useDefaultOnInvalid = constraintAnnotation.useDefaultOnInvalid();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Handle null values
        if (value == null) {
            return allowNull;
        }

        // Handle empty strings
        if (value.trim().isEmpty()) {
            return allowNull;
        }

        // Validate language code
        boolean isValid = localizationCodeService.isValidLanguageCode(value);

        // If useDefaultOnInvalid is true, always return true (validation passes)
        // The actual normalization/default setting should be handled by the service layer
        if (useDefaultOnInvalid) {
            return true;
        }

        return isValid;
    }
}