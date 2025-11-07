package com.codzs.service.organization;

import com.codzs.constant.organization.OrganizationConstants;
import com.codzs.entity.organization.Organization;
import com.codzs.entity.organization.OrganizationSetting;
import com.codzs.exception.type.validation.ValidationException;
import com.codzs.repository.organization.OrganizationRepository;
import com.codzs.repository.organization.OrganizationSettingRepository;
import com.codzs.validation.organization.OrganizationSettingBusinessValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.codzs.framework.constant.CommonConstants;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
                                         OrganizationSettingBusinessValidator organizationSettingBusinessValidator,
                                         ObjectMapper objectMapper) {
        super(organizationRepository, objectMapper);
        this.organizationSettingRepository = organizationSettingRepository;
        this.organizationSettingBusinessValidator = organizationSettingBusinessValidator;
    }

    // ========== API FLOW METHODS ==========

    @Override
    public Optional<OrganizationSetting> getOrganizationSetting(String organizationId) {
        log.debug("Getting organization settings for organization ID: {}", organizationId);
        
        return getOrgById(organizationId)
                .map(Organization::getSetting)
                .filter(setting -> setting != null);
    }

    @Override
    @Transactional
    public OrganizationSetting updateSettingValue(String organizationId, OrganizationSetting setting) {
        log.debug("Updating settings for organization ID: {}", organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        OrganizationSetting currentSetting = organization.getSetting();

        if (setting != null)    {
            if (StringUtils.hasText(setting.getTimezone()) && !setting.getTimezone().equals(currentSetting.getTimezone())) {
                updateSpecificSetting(organization, OrganizationConstants.SETTING_TIMEZONE, setting.getTimezone());
                log.info("Updated setting {} for organization ID: {}", OrganizationConstants.SETTING_TIMEZONE, organizationId);
            }
            if (StringUtils.hasText(setting.getCountry()) && !setting.getCountry().equals(currentSetting.getCountry())) {
                updateSpecificSetting(organization, OrganizationConstants.SETTING_COUNTRY, setting.getCountry());
                log.info("Updated setting {} for organization ID: {}", OrganizationConstants.SETTING_COUNTRY, organizationId);
            }
            if (StringUtils.hasText(setting.getLanguage()) && !setting.getLanguage().equals(currentSetting.getLanguage())) {
                updateSpecificSetting(organization, OrganizationConstants.SETTING_LANGUAGE, setting.getLanguage());
                log.info("Updated setting {} for organization ID: {}", OrganizationConstants.SETTING_LANGUAGE, organizationId);
            }
            if (StringUtils.hasText(setting.getCurrency()) && !setting.getCurrency().equals(currentSetting.getCurrency())) {
                updateSpecificSetting(organization, OrganizationConstants.SETTING_CURRENCY, setting.getCurrency());
                log.info("Updated setting {} for organization ID: {}", OrganizationConstants.SETTING_CURRENCY, organizationId);
            }
        }
        
        // Return updated organization
        return getOrganizationSetting(organizationId)
                .get();
    }

    @Override
    @Transactional
    public OrganizationSetting resetToDefaultSetting(String organizationId) {
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
        return getOrganizationSetting(organizationId)
                .get();
    }

    // ========== UTILITY METHODS ==========

    @Override
    public OrganizationSetting createDefaultSetting(String organizationType, String country) {
        OrganizationSetting setting = new OrganizationSetting();
        
        // Set default values based on organization type and country
        setting.setCountry(StringUtils.hasText(country) ? country : CommonConstants.DEFAULT_COUNTRY);
        setting.setTimezone(CommonConstants.DEFAULT_COUNTRY);
        setting.setLanguage(CommonConstants.DEFAULT_LANGUAGE);
        setting.setCurrency(CommonConstants.DEFAULT_CURRENCY);
        
        log.debug("Created default settings for organization type: {} and country: {}", organizationType, country);
        
        return setting;
    }

    // ========== PRIVATE HELPER METHODS ==========

    private void updateSpecificSetting(Organization organization, String settingKey, String settingValue) {
        java.time.Instant now = java.time.Instant.now();
        String user = getCurrentUser();
        
        // Get validation data for setting
        boolean isValidKey = organizationSettingBusinessValidator.isValidSettingKey(settingKey);
        boolean isValidValue = organizationSettingBusinessValidator.isValidSettingValue(settingKey, settingValue);
        
        // Business validation for setting update
        organizationSettingBusinessValidator.validateSettingUpdate(organization, settingKey, settingValue, isValidKey, isValidValue);

        switch (settingKey) {
            case OrganizationConstants.SETTING_TIMEZONE -> organizationSettingRepository.updateTimezone(organization.getId(), settingValue, now, user);
            case OrganizationConstants.SETTING_COUNTRY -> organizationSettingRepository.updateCountry(organization.getId(), settingValue, now, user);
            case OrganizationConstants.SETTING_LANGUAGE -> organizationSettingRepository.updateLanguage(organization.getId(), settingValue, now, user);
            case OrganizationConstants.SETTING_CURRENCY -> organizationSettingRepository.updateCurrency(organization.getId(), settingValue, now, user);
            default -> throw new ValidationException("Unknown setting key: " + settingKey);
        }
    }
}