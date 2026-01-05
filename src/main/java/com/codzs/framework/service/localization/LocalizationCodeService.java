package com.codzs.framework.service.localization;

import com.codzs.framework.constant.CommonConstants;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Centralized service for managing localization codes (country, currency, timezone, language).
 * Provides validation, normalization, and retrieval operations for all localization-related codes.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Service
public class LocalizationCodeService {

    // ========================= COUNTRY CODE OPERATIONS =========================
    
    /**
     * Validates country code against ISO 3166-1 alpha-2 standard.
     */
    public boolean isValidCountryCode(String countryCode) {
        if (countryCode == null || countryCode.length() != 2) {
            return false;
        }
        
        try {
            String[] availableCountries = Locale.getISOCountries();
            String normalized = countryCode.trim().toUpperCase();
            
            for (String country : availableCountries) {
                if (country.equals(normalized)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Normalizes country code to standard uppercase format.
     * Returns default if invalid.
     */
    public String normalizeCountryCode(String countryCode) {
        if (countryCode == null || countryCode.trim().isEmpty()) {
            return CommonConstants.DEFAULT_COUNTRY;
        }
        
        String normalized = countryCode.trim().toUpperCase();
        
        if (isValidCountryCode(normalized)) {
            return normalized;
        }
        
        return CommonConstants.DEFAULT_COUNTRY;
    }

    /**
     * Gets all available country codes with display names.
     */
    public List<CodeInfo> getAllCountryCodes() {
        String[] countryCodes = Locale.getISOCountries();
        return Arrays.stream(countryCodes)
                .map(code -> {
                    Locale locale = new Locale("", code);
                    return new CodeInfo(code, locale.getDisplayCountry(), 
                                      code.equals(CommonConstants.DEFAULT_COUNTRY));
                })
                .sorted(Comparator.comparing(CodeInfo::getValue))
                .collect(Collectors.toList());
    }

    // ========================= CURRENCY CODE OPERATIONS =========================
    
    /**
     * Validates currency code against ISO 4217 standard.
     */
    public boolean isValidCurrencyCode(String currencyCode) {
        if (currencyCode == null || currencyCode.length() != 3) {
            return false;
        }
        
        try {
            Currency.getInstance(currencyCode.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Normalizes currency code to standard uppercase format.
     * Returns default if invalid.
     */
    public String normalizeCurrencyCode(String currencyCode) {
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            return CommonConstants.DEFAULT_CURRENCY;
        }
        
        String normalized = currencyCode.trim().toUpperCase();
        
        if (isValidCurrencyCode(normalized)) {
            return normalized;
        }
        
        return CommonConstants.DEFAULT_CURRENCY;
    }

    /**
     * Gets all available currency codes with display names.
     */
    public List<CodeInfo> getAllCurrencyCodes() {
        Set<Currency> currencies = Currency.getAvailableCurrencies();
        return currencies.stream()
                .map(currency -> new CodeInfo(currency.getCurrencyCode(), 
                                            currency.getDisplayName(), 
                                            currency.getCurrencyCode().equals(CommonConstants.DEFAULT_CURRENCY)))
                .sorted(Comparator.comparing(CodeInfo::getCode))
                .collect(Collectors.toList());
    }

    // ========================= TIMEZONE OPERATIONS =========================
    
    /**
     * Validates timezone against available timezone IDs.
     */
    public boolean isValidTimezone(String timezoneId) {
        if (timezoneId == null || timezoneId.trim().isEmpty()) {
            return false;
        }
        
        try {
            String normalized = timezoneId.trim();
            String[] availableIds = TimeZone.getAvailableIDs();
            
            for (String id : availableIds) {
                if (id.equals(normalized)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Normalizes timezone to standard format.
     * Returns default if invalid.
     */
    public String normalizeTimezone(String timezone) {
        if (timezone == null || timezone.trim().isEmpty()) {
            return CommonConstants.DEFAULT_TIMEZONE;
        }
        
        String normalized = timezone.trim();
        
        if (isValidTimezone(normalized)) {
            return normalized;
        }
        
        return CommonConstants.DEFAULT_TIMEZONE;
    }

    /**
     * Gets all available timezone IDs with display names.
     */
    public List<CodeInfo> getAllTimezones() {
        String[] timezoneIds = TimeZone.getAvailableIDs();
        return Arrays.stream(timezoneIds)
                .map(id -> {
                    TimeZone tz = TimeZone.getTimeZone(id);
                    return new CodeInfo(id, tz.getDisplayName(), 
                                      id.equals(CommonConstants.DEFAULT_TIMEZONE));
                })
                .sorted(Comparator.comparing(CodeInfo::getCode))
                .collect(Collectors.toList());
    }

    // ========================= LANGUAGE CODE OPERATIONS =========================
    
    /**
     * Validates language code against ISO 639-1 standard.
     */
    public boolean isValidLanguageCode(String languageCode) {
        if (languageCode == null || languageCode.length() != 2) {
            return false;
        }
        
        try {
            String normalized = languageCode.trim().toLowerCase();
            String[] availableLanguages = Locale.getISOLanguages();
            
            for (String lang : availableLanguages) {
                if (lang.equals(normalized)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Normalizes language code to standard lowercase format.
     * Returns default if invalid.
     */
    public String normalizeLanguageCode(String languageCode) {
        if (languageCode == null || languageCode.trim().isEmpty()) {
            return CommonConstants.DEFAULT_LANGUAGE;
        }
        
        String normalized = languageCode.trim().toLowerCase();
        
        if (isValidLanguageCode(normalized)) {
            return normalized;
        }
        
        return CommonConstants.DEFAULT_LANGUAGE;
    }

    /**
     * Gets all available language codes with display names.
     */
    public List<CodeInfo> getAllLanguageCodes() {
        String[] languageCodes = Locale.getISOLanguages();
        return Arrays.stream(languageCodes)
                .map(code -> {
                    Locale locale = new Locale(code);
                    return new CodeInfo(code, locale.getDisplayLanguage(), 
                                      code.equals(CommonConstants.DEFAULT_LANGUAGE));
                })
                .sorted(Comparator.comparing(CodeInfo::getValue))
                .collect(Collectors.toList());
    }

    // ========================= UTILITY CLASS =========================
    
    /**
     * Data class for code information.
     */
    public static class CodeInfo {
        private final String code;
        private final String value;
        private final boolean isDefault;

        public CodeInfo(String code, String value, boolean isDefault) {
            this.code = code;
            this.value = value;
            this.isDefault = isDefault;
        }

        public String getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }

        public boolean isDefault() {
            return isDefault;
        }
    }
}