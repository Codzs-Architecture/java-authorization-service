package com.codzs.framework.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codzs.framework.annotation.validation.ValidCurrencyCode;
import com.codzs.framework.service.localization.LocalizationCodeService;

/**
 * Validator for ValidCurrencyCode annotation.
 * Validates currency codes against ISO 4217 standard.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
public class CurrencyCodeValidator implements ConstraintValidator<ValidCurrencyCode, String> {

    @Autowired
    private LocalizationCodeService localizationCodeService;

    private boolean allowNull;
    private boolean useDefaultOnInvalid;

    @Override
    public void initialize(ValidCurrencyCode constraintAnnotation) {
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

        // Validate currency code
        boolean isValid = localizationCodeService.isValidCurrencyCode(value);

        // If useDefaultOnInvalid is true, always return true (validation passes)
        // The actual normalization/default setting should be handled by the service layer
        if (useDefaultOnInvalid) {
            return true;
        }

        return isValid;
    }
}