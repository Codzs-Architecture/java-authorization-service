package com.codzs.validation.organization;

import com.codzs.constant.organization.OrganizationConstants;
import com.codzs.entity.organization.Organization;
import com.codzs.entity.organization.OrganizationSetting;
import com.codzs.exception.validation.ValidationException;
import com.codzs.framework.constant.CommonConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.ArrayList;

/**
 * Business validator for Organization Settings operations.
 * Focuses on settings business rules and organizational constraints.
 * Works with settings as embedded sub-object within organizations.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
public class OrganizationSettingBusinessValidator {

    public OrganizationSettingBusinessValidator() {
    }

    // ========== ENTRY POINT METHODS FOR ORGANIZATION SETTINGS APIs ==========

    /**
     * Validates settings update for service layer.
     * Entry point for: PUT /api/v1/organizations/{id}/settings
     */
    public void validateSettingUpdate(Organization organization, OrganizationSetting setting, boolean isValidTimezone, boolean isValidCountry, boolean isValidLanguage, boolean isValidCurrency) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        validateSettingBusinessRules(setting, isValidTimezone, isValidCountry, isValidLanguage, isValidCurrency, errors);
        validateSettingConstraints(organization, setting, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Settings update validation failed", errors);
        }
    }

    /**
     * Validates settings update and returns skip flag.
     * Returns true if update should be skipped (no changes), false otherwise.
     */
    public Boolean validateSettingUpdateFlow(Organization organization, OrganizationSetting setting, boolean isValidTimezone, boolean isValidCountry, boolean isValidLanguage, boolean isValidCurrency) {
        // Check if settings are unchanged (idempotent operation)
        OrganizationSetting currentSetting = organization.getSetting();
        if (isSettingUnchanged(currentSetting, setting)) {
            return CommonConstants.SKIP_FURTHER_STEP;
        }
        
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateSettingBusinessRules(setting, isValidTimezone, isValidCountry, isValidLanguage, isValidCurrency, errors);
        validateSettingConstraints(organization, setting, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Settings update validation failed", errors);
        }
        
        return !CommonConstants.SKIP_FURTHER_STEP;
    }

    /**
     * Validates individual setting update for service layer.
     * Entry point for: PUT /api/v1/organizations/{id}/settings/{settingKey}
     */
    public void validateSettingUpdate(Organization organization, String settingKey, Object settingValue, boolean isValidKey, boolean isValidValue) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        validateSettingKeyAndValue(settingKey, settingValue, isValidKey, isValidValue, errors);
        validateSettingConstraints(organization, settingKey, settingValue, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Setting update validation failed", errors);
        }
    }

    /**
     * Validates individual setting update and returns skip flag.
     * Returns true if update should be skipped (no changes), false otherwise.
     */
    public Boolean validateSettingUpdateFlow(Organization organization, String settingKey, Object settingValue, boolean isValidKey, boolean isValidValue) {
        // Check if setting value is unchanged (idempotent operation)
        Object currentValue = getCurrentSettingValue(organization.getSetting(), settingKey);
        if (isSettingValueUnchanged(currentValue, settingValue)) {
            return CommonConstants.SKIP_FURTHER_STEP;
        }
        
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateSettingKeyAndValue(settingKey, settingValue, isValidKey, isValidValue, errors);
        validateSettingConstraints(organization, settingKey, settingValue, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Setting update validation failed", errors);
        }
        
        return !CommonConstants.SKIP_FURTHER_STEP;
    }

    /**
     * Validates settings reset for service layer.
     * Entry point for: POST /api/v1/organizations/{id}/settings/reset
     */
    public void validateSettingReset(Organization organization) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        validateSettingResetConstraints(organization, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Settings reset validation failed", errors);
        }
    }

    // ========== CORE VALIDATION METHODS ==========

    private void validateSettingBusinessRules(OrganizationSetting setting, boolean isValidTimezone, boolean isValidCountry, boolean isValidLanguage, boolean isValidCurrency,
                                             List<ValidationException.ValidationError> errors) {
        if (setting == null) {
            errors.add(new ValidationException.ValidationError("setting", "Setting cannot be null"));
            return;
        }

        // Only business logic validations remain here - DTO validation handles @Size etc.
        validateTimezoneBusinessRules(setting.getTimezone(), isValidTimezone, errors);
        validateCountryBusinessRules(setting.getCountry(), isValidCountry, errors);
        validateLanguageBusinessRules(setting.getLanguage(), isValidLanguage, errors);
        validateCurrencyBusinessRules(setting.getCurrency(), isValidCurrency, errors);
        
        // Cross-field business validation
        validateSettingConsistency(setting, errors);
    }

    private void validateTimezoneBusinessRules(String timezone, boolean isValidTimezone,
                                             List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(timezone)) {
            return; // Allow null/empty - validation handled by DTO annotations
        }

        // Use validation data passed from service layer
        if (!isValidTimezone) {
            errors.add(new ValidationException.ValidationError(OrganizationConstants.SETTING_TIMEZONE, 
                "Invalid timezone: " + timezone + ". Must be a valid IANA timezone identifier."));
        }
    }

    private void validateCountryBusinessRules(String country, boolean isValidCountry,
                                            List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(country)) {
            return; // Allow null/empty - validation handled by DTO annotations
        }

        // Use validation data passed from service layer
        if (!isValidCountry) {
            errors.add(new ValidationException.ValidationError(OrganizationConstants.SETTING_COUNTRY, 
                "Invalid country: " + country + ". Must be a valid ISO 3166 country code."));
        }
    }

    private void validateLanguageBusinessRules(String language, boolean isValidLanguage,
                                             List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(language)) {
            return; // Allow null/empty - validation handled by DTO annotations
        }

        // Use validation data passed from service layer
        if (!isValidLanguage) {
            errors.add(new ValidationException.ValidationError(OrganizationConstants.SETTING_LANGUAGE, 
                "Invalid language: " + language + ". Must be a valid ISO 639 language code."));
        }
    }

    private void validateCurrencyBusinessRules(String currency, boolean isValidCurrency,
                                             List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(currency)) {
            return; // Allow null/empty - validation handled by DTO annotations
        }

        // Use validation data passed from service layer
        if (!isValidCurrency) {
            errors.add(new ValidationException.ValidationError(OrganizationConstants.SETTING_CURRENCY, 
                "Invalid currency: " + currency + ". Must be a valid ISO 4217 currency code."));
        }
    }

    private void validateSettingKeyAndValue(String settingKey, Object settingValue, boolean isValidKey, boolean isValidValue,
                                          List<ValidationException.ValidationError> errors) {
        // Use validation data passed from service layer
        if (!isValidKey) {
            errors.add(new ValidationException.ValidationError("settingKey", 
                "Invalid setting key: " + settingKey));
            return;
        }

        // Use validation data passed from service layer
        if (!isValidValue) {
            errors.add(new ValidationException.ValidationError("settingValue", 
                "Invalid setting value for key: " + settingKey));
        }
    }

    private void validateSettingConsistency(OrganizationSetting setting,
                                           List<ValidationException.ValidationError> errors) {
        // Business rule: If country is set, timezone should be consistent with country
        if (StringUtils.hasText(setting.getCountry()) && StringUtils.hasText(setting.getTimezone())) {
            if (!isTimezoneConsistentWithCountry(setting.getTimezone(), setting.getCountry())) {
                errors.add(new ValidationException.ValidationError(OrganizationConstants.SETTING_TIMEZONE, 
                    "Timezone " + setting.getTimezone() + " is not consistent with country " + setting.getCountry()));
            }
        }

        // Business rule: If country is set, currency should be consistent with country
        if (StringUtils.hasText(setting.getCountry()) && StringUtils.hasText(setting.getCurrency())) {
            if (!isCurrencyConsistentWithCountry(setting.getCurrency(), setting.getCountry())) {
                errors.add(new ValidationException.ValidationError(OrganizationConstants.SETTING_CURRENCY, 
                    "Currency " + setting.getCurrency() + " is not commonly used in country " + setting.getCountry()));
            }
        }
    }

    private void validateSettingConstraints(Organization organization, OrganizationSetting setting,
                                           List<ValidationException.ValidationError> errors) {
        // Business rule: Enterprise organizations must have all settings configured
        if ("ENTERPRISE".equals(organization.getOrganizationType())) {
            if (!StringUtils.hasText(setting.getTimezone()) || 
                !StringUtils.hasText(setting.getCountry()) ||
                !StringUtils.hasText(setting.getLanguage()) ||
                !StringUtils.hasText(setting.getCurrency())) {
                errors.add(new ValidationException.ValidationError("settings", 
                    "Enterprise organizations must have all settings (timezone, country, language, currency) configured"));
            }
        }
    }

    private void validateSettingConstraints(Organization organization, String settingKey, Object settingValue,
                                          List<ValidationException.ValidationError> errors) {
        // Business rule: Cannot clear essential settings for certain organization types
        if ("ENTERPRISE".equals(organization.getOrganizationType()) && 
            (settingValue == null || settingValue.toString().trim().isEmpty())) {
            errors.add(new ValidationException.ValidationError("settingValue", 
                "Enterprise organizations cannot have empty " + settingKey + " setting"));
        }
    }

    private void validateSettingResetConstraints(Organization organization,
                                                List<ValidationException.ValidationError> errors) {
        // Business rule: Cannot reset settings if organization has active international operations
        // This would be determined by checking if organization has users in multiple countries
        // For now, just log that this business rule needs implementation
        
        // Future business rule: Check if organization has multi-country operations
        // if (hasMultiCountryOperations(organization)) {
        //     errors.add(new ValidationException.ValidationError("reset", 
        //         "Cannot reset settings for organizations with multi-country operations"));
        // }
    }

    // ========== BUSINESS LOGIC HELPER METHODS ==========

    private boolean isTimezoneConsistentWithCountry(String timezone, String country) {
        // TODO: Implement timezone-country consistency check
        // This would validate that the timezone is actually used in the specified country
        return true; // Placeholder implementation
    }

    private boolean isCurrencyConsistentWithCountry(String currency, String country) {
        // TODO: Implement currency-country consistency check
        // This would validate that the currency is officially used in the specified country
        return true; // Placeholder implementation
    }

    // ========== HELPER METHODS FOR SKIPFURTHERSTEP LOGIC ==========

    private boolean isSettingUnchanged(OrganizationSetting current, OrganizationSetting updated) {
        if (current == null && updated == null) {
            return true;
        }
        if (current == null || updated == null) {
            return false;
        }
        
        return isStringUnchanged(current.getTimezone(), updated.getTimezone()) &&
               isStringUnchanged(current.getCountry(), updated.getCountry()) &&
               isStringUnchanged(current.getLanguage(), updated.getLanguage()) &&
               isStringUnchanged(current.getCurrency(), updated.getCurrency());
    }

    private Object getCurrentSettingValue(OrganizationSetting settings, String settingKey) {
        if (settings == null) {
            return null;
        }
        
        return switch (settingKey) {
            case OrganizationConstants.SETTING_TIMEZONE -> settings.getTimezone();
            case OrganizationConstants.SETTING_COUNTRY -> settings.getCountry();
            case OrganizationConstants.SETTING_LANGUAGE -> settings.getLanguage();
            case OrganizationConstants.SETTING_CURRENCY -> settings.getCurrency();
            default -> null;
        };
    }

    private boolean isSettingValueUnchanged(Object current, Object updated) {
        if (current == null && updated == null) {
            return true;
        }
        if (current == null || updated == null) {
            return false;
        }
        return current.equals(updated);
    }

    private boolean isStringUnchanged(String current, String updated) {
        if (current == null && updated == null) {
            return true;
        }
        if (current == null || updated == null) {
            return false;
        }
        return current.equals(updated);
    }
}