package com.codzs.service.organization;

import com.codzs.entity.organization.Organization;
import com.codzs.entity.organization.OrganizationSettings;
import com.codzs.framework.exception.util.ExceptionUtils;
import com.codzs.repository.organization.OrganizationSettingsRepository;
import com.codzs.validation.organization.OrganizationSettingsBusinessValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * Service implementation for OrganizationSettings-related business operations.
 * Manages organization settings including language, timezone, currency, and country
 * with proper business validation and transaction management. Follows entity-first design pattern.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class OrganizationSettingsServiceImpl implements OrganizationSettingsService {

    private final OrganizationSettingsRepository organizationSettingsRepository;
    private final OrganizationService organizationService;
    private final OrganizationSettingsBusinessValidator organizationSettingsBusinessValidator;

    @Autowired
    public OrganizationSettingsServiceImpl(OrganizationSettingsRepository organizationSettingsRepository,
                                         OrganizationService organizationService,
                                         OrganizationSettingsBusinessValidator organizationSettingsBusinessValidator) {
        this.organizationSettingsRepository = organizationSettingsRepository;
        this.organizationService = organizationService;
        this.organizationSettingsBusinessValidator = organizationSettingsBusinessValidator;
    }

    // ========== API FLOW METHODS ==========

    @Override
    @Transactional
    public Organization updateOrganizationSettings(String organizationId, OrganizationSettings settings) {
        log.debug("Updating organization settings for organization ID: {}", organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Business validation for settings update
        organizationSettingsBusinessValidator.validateSettingsUpdate(organization, settings);
        
        // Use MongoDB operation to update settings directly
        organizationSettingsRepository.updateAllSettings(organizationId, settings, 
            java.time.Instant.now(), "system"); // TODO: Get actual user from security context
        
        log.info("Updated organization settings for organization ID: {}", organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    @Override
    public OrganizationSettings getOrganizationSettings(String organizationId) {
        log.debug("Getting organization settings for organization ID: {}", organizationId);
        
        try {
            Organization organization = getOrganizationAndValidate(organizationId);
            return organization.getSettings();
        } catch (ValidationException e) {
            log.warn("Organization not found with ID: {}", organizationId);
            return null;
        }
    }

    @Override
    @Transactional
    public Organization updateSettingValue(String organizationId, String settingKey, Object settingValue) {
        log.debug("Updating setting {} for organization ID: {}", settingKey, organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Business validation for setting update
        organizationSettingsBusinessValidator.validateSettingUpdate(organization, settingKey, settingValue);
        
        // Use MongoDB operation to update specific setting directly
        updateSpecificSetting(organizationId, settingKey, settingValue.toString());
        
        log.info("Updated setting {} for organization ID: {}", settingKey, organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    @Override
    public String getSettingValue(String organizationId, String settingKey) {
        log.debug("Getting setting {} for organization ID: {}", settingKey, organizationId);
        
        try {
            Organization organization = getOrganizationAndValidate(organizationId);
            OrganizationSettings settings = organization.getSettings();
            if (settings == null) {
                log.warn("No settings found for organization ID: {}", organizationId);
                return null;
            }
            return getSettingFromSettings(settings, settingKey);
        } catch (ValidationException e) {
            log.warn("Organization not found with ID: {}", organizationId);
            return null;
        }
    }

    @Override
    @Transactional
    public Organization resetToDefaultSettings(String organizationId) {
        log.debug("Resetting settings to default for organization ID: {}", organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Business validation for settings reset
        organizationSettingsBusinessValidator.validateSettingsReset(organization);
        
        // Create default settings
        OrganizationSettings defaultSettings = createDefaultSettings(
                organization.getOrganizationType(), 
                organization.getSettings() != null ? organization.getSettings().getCountry() : null);
        
        // Use MongoDB operation to update settings to defaults
        organizationSettingsRepository.updateAllSettings(organizationId, defaultSettings, 
            java.time.Instant.now(), "system"); // TODO: Get actual user from security context
        
        log.info("Reset settings to default for organization ID: {}", organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    // ========== UTILITY METHODS ==========

    @Override
    public boolean validateOrganizationSettings(String organizationId, OrganizationSettings settings) {
        if (settings == null) {
            return false;
        }
        
        // Validate timezone
        if (StringUtils.hasText(settings.getTimezone()) && !isValidTimezone(settings.getTimezone())) {
            return false;
        }
        
        // Validate country
        if (StringUtils.hasText(settings.getCountry()) && !isValidCountryCode(settings.getCountry())) {
            return false;
        }
        
        // Validate language
        if (StringUtils.hasText(settings.getLanguage()) && !isValidLanguageCode(settings.getLanguage())) {
            return false;
        }
        
        // Validate currency
        if (StringUtils.hasText(settings.getCurrency()) && !isValidCurrencyCode(settings.getCurrency())) {
            return false;
        }
        
        return true;
    }

    @Override
    public OrganizationSettings createDefaultSettings(String organizationType, String country) {
        OrganizationSettings settings = new OrganizationSettings();
        
        // Set default values based on organization type and country
        settings.setCountry(StringUtils.hasText(country) ? country : "US");
        settings.setTimezone(getDefaultTimezone(settings.getCountry()));
        settings.setLanguage(getDefaultLanguage(settings.getCountry()));
        settings.setCurrency(getDefaultCurrency(settings.getCountry()));
        
        log.debug("Created default settings for organization type: {} and country: {}", organizationType, country);
        
        return settings;
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
            case "timezone" -> isValidTimezone(settingValue.toString());
            case "country" -> isValidCountryCode(settingValue.toString());
            case "language" -> isValidLanguageCode(settingValue.toString());
            case "currency" -> isValidCurrencyCode(settingValue.toString());
            default -> false;
        };
    }

    @Override
    public String[] getAvailableSettingKeys() {
        return new String[]{"timezone", "country", "language", "currency"};
    }

    // ========== PRIVATE HELPER METHODS ==========

    private Organization getOrganizationAndValidate(String organizationId) {
        return organizationService.findById(organizationId);
    }

    private void updateSpecificSetting(String organizationId, String settingKey, String settingValue) {
        java.time.Instant now = java.time.Instant.now();
        String user = "system"; // TODO: Get actual user from security context
        
        switch (settingKey) {
            case "timezone" -> organizationSettingsRepository.updateTimezone(organizationId, settingValue, now, user);
            case "country" -> organizationSettingsRepository.updateCountry(organizationId, settingValue, now, user);
            case "language" -> organizationSettingsRepository.updateLanguage(organizationId, settingValue, now, user);
            case "currency" -> organizationSettingsRepository.updateCurrency(organizationId, settingValue, now, user);
            default -> throw new ValidationException("Unknown setting key: " + settingKey);
        }
    }

    private String getSettingFromSettings(OrganizationSettings settings, String settingKey) {
        return switch (settingKey) {
            case "timezone" -> settings.getTimezone();
            case "country" -> settings.getCountry();
            case "language" -> settings.getLanguage();
            case "currency" -> settings.getCurrency();
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