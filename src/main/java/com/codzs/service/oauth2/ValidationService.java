package com.codzs.service.oauth2;

import java.util.Map;

import com.codzs.framework.exception.type.ValidationException;

/**
 * Service interface for handling validation operations.
 * This interface defines the contract for validation-related business logic,
 * including parameter validation, OAuth2 validation, and device validation.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
public interface ValidationService {

    /**
     * Validates OAuth2 authorization request parameters.
     * 
     * @param parameters the request parameters to validate
     * @throws ValidationException if validation fails
     */
    void validateAuthorizationRequest(Map<String, String> parameters) throws ValidationException;

    /**
     * Validates OAuth2 token request parameters.
     * 
     * @param parameters the request parameters to validate
     * @throws ValidationException if validation fails
     */
    void validateTokenRequest(Map<String, String> parameters) throws ValidationException;

    /**
     * Validates device authorization request parameters.
     * 
     * @param parameters the request parameters to validate
     * @throws ValidationException if validation fails
     */
    void validateDeviceAuthorizationRequest(Map<String, String> parameters) throws ValidationException;

    /**
     * Validates device token request parameters.
     * 
     * @param parameters the request parameters to validate
     * @throws ValidationException if validation fails
     */
    void validateDeviceTokenRequest(Map<String, String> parameters) throws ValidationException;

    /**
     * Validates client ID format and presence.
     * 
     * @param clientId the client ID to validate
     * @throws ValidationException if validation fails
     */
    void validateClientId(String clientId) throws ValidationException;

    /**
     * Validates scope parameter format and values.
     * 
     * @param scope the scope parameter to validate
     * @throws ValidationException if validation fails
     */
    void validateScope(String scope) throws ValidationException;

    /**
     * Validates redirect URI format and security.
     * 
     * @param redirectUri the redirect URI to validate
     * @throws ValidationException if validation fails
     */
    void validateRedirectUri(String redirectUri) throws ValidationException;

    /**
     * Validates user code format for device flow.
     * 
     * @param userCode the user code to validate
     * @throws ValidationException if validation fails
     */
    void validateUserCode(String userCode) throws ValidationException;

    /**
     * Validates device code format for device flow.
     * 
     * @param deviceCode the device code to validate
     * @throws ValidationException if validation fails
     */
    void validateDeviceCode(String deviceCode) throws ValidationException;

    /**
     * Validates a required string parameter.
     * 
     * @param parameterName the name of the parameter
     * @param value the value to validate
     * @throws ValidationException if validation fails
     */
    void validateRequiredParameter(String parameterName, String value) throws ValidationException;

    /**
     * Validates a string parameter against a pattern.
     * 
     * @param parameterName the name of the parameter
     * @param value the value to validate
     * @param pattern the regex pattern to match against
     * @param patternDescription a human-readable description of the expected format
     * @throws ValidationException if validation fails
     */
    void validateParameterPattern(String parameterName, String value, String pattern, String patternDescription) throws ValidationException;

    /**
     * Validates a string parameter length.
     * 
     * @param parameterName the name of the parameter
     * @param value the value to validate
     * @param minLength the minimum allowed length
     * @param maxLength the maximum allowed length
     * @throws ValidationException if validation fails
     */
    void validateParameterLength(String parameterName, String value, int minLength, int maxLength) throws ValidationException;

    /**
     * Checks if a parameter value is valid without throwing exceptions.
     * 
     * @param parameterName the name of the parameter
     * @param value the value to check
     * @return true if the parameter is valid, false otherwise
     */
    boolean isValidParameter(String parameterName, String value);

    /**
     * Gets the validation result for a parameter without throwing exceptions.
     * 
     * @param parameterName the name of the parameter
     * @param value the value to validate
     * @return ValidationResult containing the validation outcome and any errors
     */
    ValidationResult validateParameter(String parameterName, String value);

    /**
     * Represents the result of a validation operation.
     */
    class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        private final String field;

        public ValidationResult(boolean valid, String field, String errorMessage) {
            this.valid = valid;
            this.field = field;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String getField() {
            return field;
        }

        /**
         * Creates a successful validation result.
         * 
         * @param field the field that was validated
         * @return ValidationResult indicating success
         */
        public static ValidationResult success(String field) {
            return new ValidationResult(true, field, null);
        }

        /**
         * Creates a failed validation result.
         * 
         * @param field the field that failed validation
         * @param errorMessage the error message
         * @return ValidationResult indicating failure
         */
        public static ValidationResult failure(String field, String errorMessage) {
            return new ValidationResult(false, field, errorMessage);
        }
    }
} 