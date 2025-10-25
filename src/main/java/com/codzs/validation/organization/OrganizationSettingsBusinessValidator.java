package com.codzs.validation.organization;

import com.codzs.entity.organization.Organization;
import com.codzs.entity.organization.OrganizationSettings;
import com.codzs.exception.validation.ValidationException;
import com.codzs.service.organization.OrganizationSettingsService;

import org.springframework.beans.factory.annotation.Autowired;
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
public class OrganizationSettingsBusinessValidator {

    private final OrganizationSettingsService organizationSettingsService;

    @Autowired
    public OrganizationSettingsBusinessValidator(OrganizationSettingsService organizationSettingsService) {
        this.organizationSettingsService = organizationSettingsService;
    }

    // ========== ENTRY POINT METHODS FOR ORGANIZATION SETTINGS APIs ==========

    /**
     * Validates settings update for service layer.
     * Entry point for: PUT /api/v1/organizations/{id}/settings
     */
    public void validateSettingsUpdate(Organization organization, OrganizationSettings settings) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        validateSettingsBusinessRules(settings, errors);
        validateSettingsConstraints(organization, settings, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Settings update validation failed", errors);
        }
    }

    /**
     * Validates individual setting update for service layer.
     * Entry point for: PUT /api/v1/organizations/{id}/settings/{settingKey}
     */
    public void validateSettingUpdate(Organization organization, String settingKey, Object settingValue) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        validateSettingKeyAndValue(settingKey, settingValue, errors);
        validateSettingConstraints(organization, settingKey, settingValue, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Setting update validation failed", errors);
        }
    }

    /**
     * Validates settings reset for service layer.
     * Entry point for: POST /api/v1/organizations/{id}/settings/reset
     */
    public void validateSettingsReset(Organization organization) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        validateSettingsResetConstraints(organization, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Settings reset validation failed", errors);
        }
    }

    // ========== CORE VALIDATION METHODS ==========

    private void validateSettingsBusinessRules(OrganizationSettings settings, 
                                             List<ValidationException.ValidationError> errors) {
        if (settings == null) {
            errors.add(new ValidationException.ValidationError("settings", "Settings cannot be null"));
            return;
        }

        // Only business logic validations remain here - DTO validation handles @Size etc.
        validateTimezoneBusinessRules(settings.getTimezone(), errors);
        validateCountryBusinessRules(settings.getCountry(), errors);
        validateLanguageBusinessRules(settings.getLanguage(), errors);
        validateCurrencyBusinessRules(settings.getCurrency(), errors);
        
        // Cross-field business validation
        validateSettingsConsistency(settings, errors);
    }

    private void validateTimezoneBusinessRules(String timezone, 
                                             List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(timezone)) {
            return; // Allow null/empty - validation handled by DTO annotations
        }

        // Use service layer for business validation
        if (!organizationSettingsService.isValidSettingValue("timezone", timezone)) {
            errors.add(new ValidationException.ValidationError("timezone", 
                "Invalid timezone: " + timezone + ". Must be a valid IANA timezone identifier."));
        }
    }

    private void validateCountryBusinessRules(String country, 
                                            List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(country)) {
            return; // Allow null/empty - validation handled by DTO annotations
        }

        // Use service layer for business validation
        if (!organizationSettingsService.isValidSettingValue("country", country)) {
            errors.add(new ValidationException.ValidationError("country", 
                "Invalid country: " + country + ". Must be a valid ISO 3166 country code."));
        }
    }

    private void validateLanguageBusinessRules(String language, 
                                             List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(language)) {
            return; // Allow null/empty - validation handled by DTO annotations
        }

        // Use service layer for business validation
        if (!organizationSettingsService.isValidSettingValue("language", language)) {
            errors.add(new ValidationException.ValidationError("language", 
                "Invalid language: " + language + ". Must be a valid ISO 639 language code."));
        }
    }

    private void validateCurrencyBusinessRules(String currency, 
                                             List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(currency)) {
            return; // Allow null/empty - validation handled by DTO annotations
        }

        // Use service layer for business validation
        if (!organizationSettingsService.isValidSettingValue("currency", currency)) {
            errors.add(new ValidationException.ValidationError("currency", 
                "Invalid currency: " + currency + ". Must be a valid ISO 4217 currency code."));
        }
    }

    private void validateSettingKeyAndValue(String settingKey, Object settingValue,
                                          List<ValidationException.ValidationError> errors) {
        // Use service layer for key validation
        if (!organizationSettingsService.isValidSettingKey(settingKey)) {
            errors.add(new ValidationException.ValidationError("settingKey", 
                "Invalid setting key: " + settingKey));
            return;
        }

        // Use service layer for value validation
        if (!organizationSettingsService.isValidSettingValue(settingKey, settingValue)) {
            errors.add(new ValidationException.ValidationError("settingValue", 
                "Invalid setting value for key: " + settingKey));
        }
    }

    private void validateSettingsConsistency(OrganizationSettings settings,
                                           List<ValidationException.ValidationError> errors) {
        // Business rule: If country is set, timezone should be consistent with country
        if (StringUtils.hasText(settings.getCountry()) && StringUtils.hasText(settings.getTimezone())) {
            if (!isTimezoneConsistentWithCountry(settings.getTimezone(), settings.getCountry())) {
                errors.add(new ValidationException.ValidationError("timezone", 
                    "Timezone " + settings.getTimezone() + " is not consistent with country " + settings.getCountry()));
            }
        }

        // Business rule: If country is set, currency should be consistent with country
        if (StringUtils.hasText(settings.getCountry()) && StringUtils.hasText(settings.getCurrency())) {
            if (!isCurrencyConsistentWithCountry(settings.getCurrency(), settings.getCountry())) {
                errors.add(new ValidationException.ValidationError("currency", 
                    "Currency " + settings.getCurrency() + " is not commonly used in country " + settings.getCountry()));
            }
        }
    }

    private void validateSettingsConstraints(Organization organization, OrganizationSettings settings,
                                           List<ValidationException.ValidationError> errors) {
        // Business rule: Enterprise organizations must have all settings configured
        if ("ENTERPRISE".equals(organization.getOrganizationType())) {
            if (!StringUtils.hasText(settings.getTimezone()) || 
                !StringUtils.hasText(settings.getCountry()) ||
                !StringUtils.hasText(settings.getLanguage()) ||
                !StringUtils.hasText(settings.getCurrency())) {
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

    private void validateSettingsResetConstraints(Organization organization,
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
}