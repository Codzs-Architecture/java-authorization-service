package com.codzs.framework.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codzs.framework.service.localization.LocalizationCodeService;
import com.codzs.framework.validation.annotation.ValidCountryCode;

/**
 * Validator for ValidCountryCode annotation.
 * Validates country codes against ISO 3166-1 alpha-2 standard.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
public class CountryCodeValidator implements ConstraintValidator<ValidCountryCode, String> {

    @Autowired
    private LocalizationCodeService localizationCodeService;

    private boolean allowNull;
    private boolean useDefaultOnInvalid;

    @Override
    public void initialize(ValidCountryCode constraintAnnotation) {
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

        // Validate country code
        boolean isValid = localizationCodeService.isValidCountryCode(value);

        // If useDefaultOnInvalid is true, always return true (validation passes)
        // The actual normalization/default setting should be handled by the service layer
        if (useDefaultOnInvalid) {
            return true;
        }

        return isValid;
    }
}