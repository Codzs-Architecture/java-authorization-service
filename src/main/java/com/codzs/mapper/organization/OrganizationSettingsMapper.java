package com.codzs.mapper.organization;

import com.codzs.dto.organization.request.OrganizationSettingsRequestDto;
import com.codzs.dto.organization.response.OrganizationSettingsResponseDto;
import com.codzs.entity.organization.OrganizationSettings;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;

/**
 * MapStruct mapper for OrganizationSettings entity and DTOs.
 * Handles mapping between OrganizationSettings entity, request DTOs, and response DTOs
 * with proper data transformations and validation.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
@Component
public interface OrganizationSettingsMapper {

    // ========================= CREATE MAPPINGS =========================
    
    /**
     * Maps OrganizationSettingsRequestDto to OrganizationSettings entity.
     * Validates and normalizes settings values.
     */
    @Mapping(source = "language", target = "language", qualifiedByName = "normalizeLanguage")
    @Mapping(source = "timezone", target = "timezone", qualifiedByName = "normalizeTimezone")
    @Mapping(source = "currency", target = "currency", qualifiedByName = "normalizeCurrency")
    @Mapping(source = "country", target = "country", qualifiedByName = "normalizeCountry")
    OrganizationSettings toEntity(OrganizationSettingsRequestDto requestDto);

    /**
     * Updates existing OrganizationSettings entity with request data.
     * Only updates provided fields, preserves existing values for null fields.
     */
    @Mapping(source = "language", target = "language", qualifiedByName = "normalizeLanguage")
    @Mapping(source = "timezone", target = "timezone", qualifiedByName = "normalizeTimezone")
    @Mapping(source = "currency", target = "currency", qualifiedByName = "normalizeCurrency")
    @Mapping(source = "country", target = "country", qualifiedByName = "normalizeCountry")
    void updateEntity(@MappingTarget OrganizationSettings settings, OrganizationSettingsRequestDto requestDto);

    // ========================= RESPONSE MAPPINGS =========================
    
    /**
     * Maps OrganizationSettings entity to OrganizationSettingsResponseDto.
     */
    OrganizationSettingsResponseDto toResponse(OrganizationSettings settings);

    // ========================= UTILITY METHODS =========================
    
    /**
     * Normalizes language code to standard format.
     * Validates against ISO 639-1 language codes.
     */
    @Named("normalizeLanguage")
    default String normalizeLanguage(String language) {
        if (language == null || language.trim().isEmpty()) {
            return "en"; // Default to English
        }
        
        String normalized = language.trim().toLowerCase();
        
        // Validate against common language codes
        if (isValidLanguageCode(normalized)) {
            return normalized;
        }
        
        // Return default if invalid
        return "en";
    }

    /**
     * Normalizes timezone to standard format.
     * Validates against available timezone IDs.
     */
    @Named("normalizeTimezone")
    default String normalizeTimezone(String timezone) {
        if (timezone == null || timezone.trim().isEmpty()) {
            return "UTC"; // Default to UTC
        }
        
        String normalized = timezone.trim();
        
        // Validate timezone
        if (isValidTimezone(normalized)) {
            return normalized;
        }
        
        // Return default if invalid
        return "UTC";
    }

    /**
     * Normalizes currency code to standard format.
     * Validates against ISO 4217 currency codes.
     */
    @Named("normalizeCurrency")
    default String normalizeCurrency(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            return "USD"; // Default to USD
        }
        
        String normalized = currency.trim().toUpperCase();
        
        // Validate currency code
        if (isValidCurrencyCode(normalized)) {
            return normalized;
        }
        
        // Return default if invalid
        return "USD";
    }

    /**
     * Normalizes country code to standard format.
     * Validates against ISO 3166-1 alpha-2 country codes.
     */
    @Named("normalizeCountry")
    default String normalizeCountry(String country) {
        if (country == null || country.trim().isEmpty()) {
            return "US"; // Default to US
        }
        
        String normalized = country.trim().toUpperCase();
        
        // Validate country code
        if (isValidCountryCode(normalized)) {
            return normalized;
        }
        
        // Return default if invalid
        return "US";
    }

    // ========================= VALIDATION METHODS =========================
    
    /**
     * Validates language code against ISO 639-1 standard.
     */
    default boolean isValidLanguageCode(String languageCode) {
        if (languageCode == null || languageCode.length() != 2) {
            return false;
        }
        
        try {
            // Use Locale to validate language code
            Locale locale = new Locale(languageCode);
            String[] availableLanguages = Locale.getISOLanguages();
            
            for (String lang : availableLanguages) {
                if (lang.equals(languageCode)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates timezone against available timezone IDs.
     */
    default boolean isValidTimezone(String timezoneId) {
        if (timezoneId == null || timezoneId.trim().isEmpty()) {
            return false;
        }
        
        try {
            TimeZone.getTimeZone(timezoneId);
            // Check if it's in the list of available IDs
            String[] availableIds = TimeZone.getAvailableIDs();
            for (String id : availableIds) {
                if (id.equals(timezoneId)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates currency code against ISO 4217 standard.
     */
    default boolean isValidCurrencyCode(String currencyCode) {
        if (currencyCode == null || currencyCode.length() != 3) {
            return false;
        }
        
        try {
            Currency.getInstance(currencyCode);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Validates country code against ISO 3166-1 alpha-2 standard.
     */
    default boolean isValidCountryCode(String countryCode) {
        if (countryCode == null || countryCode.length() != 2) {
            return false;
        }
        
        try {
            String[] availableCountries = Locale.getISOCountries();
            for (String country : availableCountries) {
                if (country.equals(countryCode)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}