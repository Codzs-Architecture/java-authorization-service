package com.codzs.validation.organization;

import com.codzs.constant.organization.OrganizationConstants;
import com.codzs.entity.organization.Organization;
import com.codzs.framework.exception.type.ValidationException;
import com.codzs.framework.service.localization.LocalizationCodeService;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

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
    private final LocalizationCodeService localizationCodeService;

    public OrganizationSettingBusinessValidator(LocalizationCodeService localizationCodeService) {
        this.localizationCodeService = localizationCodeService;
    }

    // ========== ENTRY POINT METHODS FOR ORGANIZATION SETTINGS APIs ==========

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

    public boolean isValidSettingKey(String settingKey) {
        if (!StringUtils.hasText(settingKey)) {
            return false;
        }
        
        String[] validKeys = this.getAvailableSettingKeys();
        
        return Arrays.asList(validKeys).contains(settingKey);
    }

    public boolean isValidSettingValue(String settingKey, Object settingValue) {
        if (!isValidSettingKey(settingKey) || settingValue == null) {
            return false;
        }
        
        return switch (settingKey) {
            case OrganizationConstants.SETTING_TIMEZONE -> isValidTimezone(settingValue.toString());
            case OrganizationConstants.SETTING_COUNTRY -> isValidCountryCode(settingValue.toString());
            case OrganizationConstants.SETTING_LANGUAGE -> isValidLanguageCode(settingValue.toString());
            case OrganizationConstants.SETTING_CURRENCY -> isValidCurrencyCode(settingValue.toString());
            default -> false;
        };
    }

    public String[] getAvailableSettingKeys() {
        return new String[]{
            OrganizationConstants.SETTING_TIMEZONE, 
            OrganizationConstants.SETTING_COUNTRY, 
            OrganizationConstants.SETTING_LANGUAGE, 
            OrganizationConstants.SETTING_CURRENCY
        };
    }

    private boolean isValidTimezone(String timezone) {
        if (!StringUtils.hasText(timezone)) {
            return false;
        }

        return localizationCodeService.getAllTimezones().stream().anyMatch(tz -> tz.getCode().equals(timezone));
    }

    private boolean isValidCountryCode(String countryCode) {
        if (!StringUtils.hasText(countryCode)) {
            return false;
        }

        return localizationCodeService.getAllCountryCodes().stream().anyMatch(cc -> cc.getCode().equals(countryCode));
    }

    private boolean isValidLanguageCode(String languageCode) {
        if (!StringUtils.hasText(languageCode)) {
            return false;
        }

        return localizationCodeService.getAllLanguageCodes().stream().anyMatch(lc -> lc.getCode().equals(languageCode));
    }

    private boolean isValidCurrencyCode(String currencyCode) {
        if (!StringUtils.hasText(currencyCode)) {
            return false;
        }

        return localizationCodeService.getAllCurrencyCodes().stream().anyMatch(cc -> cc.getCode().equals(currencyCode));
    }

}