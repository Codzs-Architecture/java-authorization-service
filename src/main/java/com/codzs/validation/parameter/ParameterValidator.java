package com.codzs.validation.parameter;

import com.codzs.exception.validation.ValidationException;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * General parameter validator utility class.
 * This class provides common validation logic for parameters including
 * required field validation, pattern matching, and length validation.
 * 
 * @author Parameter Validator
 * @since 1.1
 */
public class ParameterValidator {

    // Common validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern URL_PATTERN = Pattern.compile(
        "^https?://[a-zA-Z0-9.-]+(?:\\:[0-9]+)?(?:/[^\\s]*)?$"
    );

    // Constants for validation
    private static final int DEFAULT_MIN_LENGTH = 1;
    private static final int DEFAULT_MAX_LENGTH = 255;

    /**
     * Validates that a parameter is not null or empty.
     * 
     * @param parameterName the name of the parameter
     * @param value the value to validate
     * @throws ValidationException if validation fails
     */
    public void validateRequired(String parameterName, String value) throws ValidationException {
        if (!StringUtils.hasText(value)) {
            throw ValidationException.requiredField(parameterName);
        }
    }

    /**
     * Validates that a parameter matches a specific pattern.
     * 
     * @param parameterName the name of the parameter
     * @param value the value to validate
     * @param pattern the regex pattern to match against
     * @param patternDescription a human-readable description of the expected format
     * @throws ValidationException if validation fails
     */
    public void validatePattern(String parameterName, String value, String pattern, String patternDescription) throws ValidationException {
        if (!StringUtils.hasText(value)) {
            throw ValidationException.requiredField(parameterName);
        }

        try {
            Pattern compiledPattern = Pattern.compile(pattern);
            if (!compiledPattern.matcher(value).matches()) {
                throw ValidationException.invalidFormat(parameterName, patternDescription);
            }
        } catch (PatternSyntaxException e) {
            throw new ValidationException("Pattern validation failed", parameterName, 
                "Invalid validation pattern: " + e.getMessage());
        }
    }

    /**
     * Validates that a parameter length is within acceptable bounds.
     * 
     * @param parameterName the name of the parameter
     * @param value the value to validate
     * @param minLength the minimum allowed length
     * @param maxLength the maximum allowed length
     * @throws ValidationException if validation fails
     */
    public void validateLength(String parameterName, String value, int minLength, int maxLength) throws ValidationException {
        if (value == null) {
            throw ValidationException.requiredField(parameterName);
        }

        if (value.length() < minLength || value.length() > maxLength) {
            throw ValidationException.valueOutOfRange(parameterName, minLength, maxLength);
        }
    }

    /**
     * Validates that a parameter is a valid email address.
     * 
     * @param parameterName the name of the parameter
     * @param email the email address to validate
     * @throws ValidationException if validation fails
     */
    public void validateEmail(String parameterName, String email) throws ValidationException {
        if (!StringUtils.hasText(email)) {
            throw ValidationException.requiredField(parameterName);
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw ValidationException.invalidFormat(parameterName, "valid email address");
        }
    }

    /**
     * Validates that a parameter is a valid URL.
     * 
     * @param parameterName the name of the parameter
     * @param url the URL to validate
     * @throws ValidationException if validation fails
     */
    public void validateUrl(String parameterName, String url) throws ValidationException {
        if (!StringUtils.hasText(url)) {
            throw ValidationException.requiredField(parameterName);
        }

        if (!URL_PATTERN.matcher(url).matches()) {
            throw ValidationException.invalidFormat(parameterName, "valid HTTP or HTTPS URL");
        }
    }

    /**
     * Validates that a parameter is a valid integer within a range.
     * 
     * @param parameterName the name of the parameter
     * @param value the value to validate
     * @param minValue the minimum allowed value
     * @param maxValue the maximum allowed value
     * @throws ValidationException if validation fails
     */
    public void validateIntegerRange(String parameterName, String value, int minValue, int maxValue) throws ValidationException {
        if (!StringUtils.hasText(value)) {
            throw ValidationException.requiredField(parameterName);
        }

        try {
            int intValue = Integer.parseInt(value);
            if (intValue < minValue || intValue > maxValue) {
                throw ValidationException.valueOutOfRange(parameterName, minValue, maxValue);
            }
        } catch (NumberFormatException e) {
            throw ValidationException.invalidFormat(parameterName, "integer value");
        }
    }

    /**
     * Validates that a parameter is a valid positive integer.
     * 
     * @param parameterName the name of the parameter
     * @param value the value to validate
     * @throws ValidationException if validation fails
     */
    public void validatePositiveInteger(String parameterName, String value) throws ValidationException {
        if (!StringUtils.hasText(value)) {
            throw ValidationException.requiredField(parameterName);
        }

        try {
            int intValue = Integer.parseInt(value);
            if (intValue <= 0) {
                throw new ValidationException("Positive integer validation failed", parameterName, 
                    "Value must be a positive integer greater than 0");
            }
        } catch (NumberFormatException e) {
            throw ValidationException.invalidFormat(parameterName, "positive integer");
        }
    }

    /**
     * Validates that a parameter is alphanumeric.
     * 
     * @param parameterName the name of the parameter
     * @param value the value to validate
     * @throws ValidationException if validation fails
     */
    public void validateAlphanumeric(String parameterName, String value) throws ValidationException {
        validatePattern(parameterName, value, "^[a-zA-Z0-9]+$", "alphanumeric characters only");
    }

    /**
     * Validates that a parameter is alphanumeric with allowed special characters.
     * 
     * @param parameterName the name of the parameter
     * @param value the value to validate
     * @param allowedSpecialChars the allowed special characters (e.g., "_-.")
     * @throws ValidationException if validation fails
     */
    public void validateAlphanumericWithSpecialChars(String parameterName, String value, String allowedSpecialChars) throws ValidationException {
        String escapedSpecialChars = escapeSpecialRegexChars(allowedSpecialChars);
        String pattern = "^[a-zA-Z0-9" + escapedSpecialChars + "]+$";
        validatePattern(parameterName, value, pattern, 
            "alphanumeric characters and these special characters: " + allowedSpecialChars);
    }

    /**
     * Validates that a parameter contains only allowed characters.
     * 
     * @param parameterName the name of the parameter
     * @param value the value to validate
     * @param allowedChars the allowed characters as a character class (e.g., "a-zA-Z0-9_-")
     * @param description a human-readable description of allowed characters
     * @throws ValidationException if validation fails
     */
    public void validateAllowedCharacters(String parameterName, String value, String allowedChars, String description) throws ValidationException {
        String pattern = "^[" + allowedChars + "]+$";
        validatePattern(parameterName, value, pattern, description);
    }

    /**
     * Validates that a parameter is not in a list of forbidden values.
     * 
     * @param parameterName the name of the parameter
     * @param value the value to validate
     * @param forbiddenValues the forbidden values
     * @throws ValidationException if validation fails
     */
    public void validateNotForbidden(String parameterName, String value, String... forbiddenValues) throws ValidationException {
        if (!StringUtils.hasText(value)) {
            throw ValidationException.requiredField(parameterName);
        }

        for (String forbidden : forbiddenValues) {
            if (value.equals(forbidden)) {
                throw new ValidationException("Forbidden value validation failed", parameterName, 
                    "Value '" + value + "' is not allowed");
            }
        }
    }

    /**
     * Escapes special regex characters in a string.
     * 
     * @param input the input string containing special characters
     * @return the escaped string safe for use in regex
     */
    private String escapeSpecialRegexChars(String input) {
        if (input == null) {
            return "";
        }
        
        // Escape special regex characters
        return input.replaceAll("([\\\\\\[\\]{}()*+?.^$|])", "\\\\$1");
    }
} 