package com.codzs.service.organization;

import com.codzs.entity.organization.Organization;
import com.codzs.entity.organization.OrganizationSetting;

import java.util.Optional;

/**
 * Service interface for OrganizationSetting-related business operations.
 * Manages organization setting including language, timezone, currency, and country
 * with proper business validation and transaction management. Follows entity-first design pattern.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public interface OrganizationSettingService {

    // ========== API FLOW METHODS ==========

    /**
     * Updates organization setting for an organization.
     * API: PUT /api/v1/organizations/{id}/settings
     *
     * @param organizationId the organization ID
     * @param setting the organization setting entity
     * @return updated organization with new setting
     */
    Organization updateOrganizationSetting(String organizationId, OrganizationSetting setting);

    /**
     * Gets organization settings for an organization.
     * API: GET /api/v1/organizations/{id}/settings
     *
     * @param organizationId the organization ID
     * @return Optional containing organization settings, or empty if not found
     */
    Optional<OrganizationSetting> getOrganizationSetting(String organizationId);

    /**
     * Updates specific setting value for an organization.
     * API: PUT /api/v1/organizations/{id}/settings/{settingKey}
     *
     * @param organizationId the organization ID
     * @param settingKey the setting key to update
     * @param settingValue the new setting value
     * @return updated organization with modified settings
     */
    Organization updateSettingValue(String organizationId, String settingKey, Object settingValue);

    /**
     * Gets specific setting value for an organization.
     * API: GET /api/v1/organizations/{id}/settings/{settingKey}
     *
     * @param organizationId the organization ID
     * @param settingKey the setting key to retrieve
     * @return Optional containing setting value, or empty if not found
     */
    Optional<String> getSettingValue(String organizationId, String settingKey);

    /**
     * Resets organization settings to default values.
     * API: POST /api/v1/organizations/{id}/settings/reset
     *
     * @param organizationId the organization ID
     * @return updated organization with default settings
     */
    Organization resetToDefaultSetting(String organizationId);

    // ========== UTILITY METHODS ==========

    /**
     * Validates organization settings.
     *
     * @param organizationId the organization ID
     * @param settings the organization settings to validate
     * @return true if settings are valid, false otherwise
     */
    boolean validateOrganizationSetting(String organizationId, OrganizationSetting setting);

    /**
     * Creates default settings for an organization.
     *
     * @param organizationType the organization type
     * @param country the country code
     * @return default organization settings
     */
    OrganizationSetting createDefaultSetting(String organizationType, String country);

    /**
     * Checks if a setting key is valid and allowed.
     *
     * @param settingKey the setting key to validate
     * @return true if setting key is valid, false otherwise
     */
    boolean isValidSettingKey(String settingKey);

    /**
     * Checks if a setting value is valid for the given key.
     *
     * @param settingKey the setting key
     * @param settingValue the setting value to validate
     * @return true if setting value is valid, false otherwise
     */
    boolean isValidSettingValue(String settingKey, Object settingValue);

    /**
     * Gets available setting keys for an organization type.
     *
     * @return array of available setting keys
     */
    String[] getAvailableSettingKeys();
}