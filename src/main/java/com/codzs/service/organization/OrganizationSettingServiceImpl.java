package com.codzs.service.organization;

import com.codzs.constant.organization.OrganizationConstants;
import com.codzs.entity.organization.Organization;
import com.codzs.entity.organization.OrganizationSetting;
import com.codzs.repository.organization.OrganizationRepository;
import com.codzs.repository.organization.OrganizationSettingRepository;
import com.codzs.validation.organization.OrganizationSettingBusinessValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.codzs.exception.validation.ValidationException;
import com.codzs.framework.constant.CommonConstants;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

/**
 * Service implementation for OrganizationSetting-related business operations.
 * Manages organization settings including language, timezone, currency, and country
 * with proper business validation and transaction management. Follows entity-first design pattern.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class OrganizationSettingServiceImpl extends BaseOrganizationServiceImpl implements OrganizationSettingService {

    private final OrganizationSettingRepository organizationSettingRepository;
    private final OrganizationSettingBusinessValidator organizationSettingBusinessValidator;

    @Autowired
    public OrganizationSettingServiceImpl(OrganizationSettingRepository organizationSettingRepository,
                                         OrganizationRepository organizationRepository, 
                                         ObjectMapper objectMapper) {
        super(organizationRepository, objectMapper);
        this.organizationSettingRepository = organizationSettingRepository;
        this.organizationSettingBusinessValidator = new OrganizationSettingBusinessValidator();
    }

    // ========== API FLOW METHODS ==========

    @Override
    @Transactional
    public Organization updateOrganizationSetting(String organizationId, OrganizationSetting setting) {
        log.debug("Updating organization settings for organization ID: {}", organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Get validation data for settings
        boolean isValidTimezone = setting.getTimezone() == null || isValidSettingValue(OrganizationConstants.SETTING_TIMEZONE, setting.getTimezone());
        boolean isValidCountry = setting.getCountry() == null || isValidSettingValue(OrganizationConstants.SETTING_COUNTRY, setting.getCountry());
        boolean isValidLanguage = setting.getLanguage() == null || isValidSettingValue(OrganizationConstants.SETTING_LANGUAGE, setting.getLanguage());
        boolean isValidCurrency = setting.getCurrency() == null || isValidSettingValue(OrganizationConstants.SETTING_CURRENCY, setting.getCurrency());
        
        // Business validation for settings update
        organizationSettingBusinessValidator.validateSettingUpdate(organization, setting, isValidTimezone, isValidCountry, isValidLanguage, isValidCurrency);
        
        // Use MongoDB operation to update settings directly
        organizationSettingRepository.updateAllSetting(organizationId, setting, 
            Instant.now(), getCurrentUser());
        
        log.info("Updated organization settings for organization ID: {}", organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    @Override
    public Optional<OrganizationSetting> getOrganizationSetting(String organizationId) {
        log.debug("Getting organization settings for organization ID: {}", organizationId);
        
        return getOrgById(organizationId)
                .map(Organization::getSetting)
                .filter(setting -> setting != null);
    }

    @Override
    @Transactional
    public Organization updateSettingValue(String organizationId, String settingKey, Object settingValue) {
        log.debug("Updating setting {} for organization ID: {}", settingKey, organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Get validation data for setting
        boolean isValidKey = isValidSettingKey(settingKey);
        boolean isValidValue = isValidSettingValue(settingKey, settingValue);
        
        // Business validation for setting update
        organizationSettingBusinessValidator.validateSettingUpdate(organization, settingKey, settingValue, isValidKey, isValidValue);
        
        // Use MongoDB operation to update specific setting directly
        updateSpecificSetting(organizationId, settingKey, settingValue.toString());
        
        log.info("Updated setting {} for organization ID: {}", settingKey, organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    @Override
    public Optional<String> getSettingValue(String organizationId, String settingKey) {
        log.debug("Getting setting {} for organization ID: {}", settingKey, organizationId);
        
        return getOrganizationSetting(organizationId)
                .map(setting -> getSettingFromSetting(setting, settingKey))
                .filter(value -> value != null);
    }

    @Override
    @Transactional
    public Organization resetToDefaultSetting(String organizationId) {
        log.debug("Resetting settings to default for organization ID: {}", organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Business validation for settings reset
        organizationSettingBusinessValidator.validateSettingReset(organization);
        
        // Create default settings
        OrganizationSetting defaultSetting = createDefaultSetting(
                organization.getOrganizationType(), 
                organization.getSetting() != null ? organization.getSetting().getCountry() : null);
        
        // Use MongoDB operation to update settings to defaults
        organizationSettingRepository.updateAllSetting(organizationId, defaultSetting, 
            java.time.Instant.now(), getCurrentUser());
        
        log.info("Reset settings to default for organization ID: {}", organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    // ========== UTILITY METHODS ==========

    @Override
    public boolean validateOrganizationSetting(String organizationId, OrganizationSetting setting) {
        if (setting == null) {
            return false;
        }
        
        // Validate timezone
        if (StringUtils.hasText(setting.getTimezone()) && !isValidTimezone(setting.getTimezone())) {
            return false;
        }
        
        // Validate country
        if (StringUtils.hasText(setting.getCountry()) && !isValidCountryCode(setting.getCountry())) {
            return false;
        }
        
        // Validate language
        if (StringUtils.hasText(setting.getLanguage()) && !isValidLanguageCode(setting.getLanguage())) {
            return false;
        }
        
        // Validate currency
        if (StringUtils.hasText(setting.getCurrency()) && !isValidCurrencyCode(setting.getCurrency())) {
            return false;
        }
        
        return true;
    }

    @Override
    public OrganizationSetting createDefaultSetting(String organizationType, String country) {
        OrganizationSetting setting = new OrganizationSetting();
        
        // Set default values based on organization type and country
        setting.setCountry(StringUtils.hasText(country) ? country : CommonConstants.DEFAULT_COUNTRY);
        setting.setTimezone(getDefaultTimezone(setting.getCountry()));
        setting.setLanguage(getDefaultLanguage(setting.getCountry()));
        setting.setCurrency(getDefaultCurrency(setting.getCountry()));
        
        log.debug("Created default settings for organization type: {} and country: {}", organizationType, country);
        
        return setting;
    }

    @Override
    public boolean isValidSettingKey(String settingKey) {
        if (!StringUtils.hasText(settingKey)) {
            return false;
        }
        
        String[] validKeys = this.getAvailableSettingKeys();
        
        return Arrays.asList(validKeys).contains(settingKey);
    }

    @Override
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

    @Override
    public String[] getAvailableSettingKeys() {
        return new String[]{
            OrganizationConstants.SETTING_TIMEZONE, 
            OrganizationConstants.SETTING_COUNTRY, 
            OrganizationConstants.SETTING_LANGUAGE, 
            OrganizationConstants.SETTING_CURRENCY
        };
    }

    // ========== PRIVATE HELPER METHODS ==========

    private void updateSpecificSetting(String organizationId, String settingKey, String settingValue) {
        java.time.Instant now = java.time.Instant.now();
        String user = getCurrentUser();
        
        switch (settingKey) {
            case OrganizationConstants.SETTING_TIMEZONE -> organizationSettingRepository.updateTimezone(organizationId, settingValue, now, user);
            case OrganizationConstants.SETTING_COUNTRY -> organizationSettingRepository.updateCountry(organizationId, settingValue, now, user);
            case OrganizationConstants.SETTING_LANGUAGE -> organizationSettingRepository.updateLanguage(organizationId, settingValue, now, user);
            case OrganizationConstants.SETTING_CURRENCY -> organizationSettingRepository.updateCurrency(organizationId, settingValue, now, user);
            default -> throw new ValidationException("Unknown setting key: " + settingKey);
        }
    }

    private String getSettingFromSetting(OrganizationSetting setting, String settingKey) {
        return switch (settingKey) {
            case OrganizationConstants.SETTING_TIMEZONE -> setting.getTimezone();
            case OrganizationConstants.SETTING_COUNTRY -> setting.getCountry();
            case OrganizationConstants.SETTING_LANGUAGE -> setting.getLanguage();
            case OrganizationConstants.SETTING_CURRENCY -> setting.getCurrency();
            default -> null;
        };
    }


    // ========== VALIDATION HELPER METHODS ==========

    private boolean isValidTimezone(String timezone) {
        // TODO: Implement timezone validation against valid timezone list
        return StringUtils.hasText(timezone) && timezone.length() <= 50;
    }

    private boolean isValidCountryCode(String countryCode) {
        // TODO: Implement country code validation against ISO 3166 codes
        return StringUtils.hasText(countryCode) && countryCode.length() <= 10;
    }

    private boolean isValidLanguageCode(String languageCode) {
        // TODO: Implement language code validation against ISO 639 codes
        return StringUtils.hasText(languageCode) && languageCode.length() <= 50;
    }

    private boolean isValidCurrencyCode(String currencyCode) {
        // TODO: Implement currency code validation against ISO 4217 codes
        return StringUtils.hasText(currencyCode) && currencyCode.length() <= 10;
    }

    // ========== DEFAULT VALUE HELPER METHODS ==========

    private String getDefaultTimezone(String country) {
        // TODO: Implement country-based timezone mapping
        return switch (country != null ? country : "US") {
            case "US" -> "America/New_York";
            case "GB" -> "Europe/London";
            case "IN" -> "Asia/Kolkata";
            default -> "UTC";
        };
    }

    private String getDefaultLanguage(String country) {
        // TODO: Implement country-based language mapping
        return switch (country != null ? country : "US") {
            case "US", "GB" -> "en";
            case "IN" -> "en";
            case "FR" -> "fr";
            case "DE" -> "de";
            default -> "en";
        };
    }

    private String getDefaultCurrency(String country) {
        // TODO: Implement country-based currency mapping
        return switch (country != null ? country : "US") {
            case "US" -> "USD";
            case "GB" -> "GBP";
            case "IN" -> "INR";
            case "EU", "FR", "DE" -> "EUR";
            default -> "USD";
        };
    }
}