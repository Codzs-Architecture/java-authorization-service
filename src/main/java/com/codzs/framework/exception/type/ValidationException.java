package com.codzs.framework.exception.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exception thrown when validation fails.
 * This exception is specific to the authorization service validation operations
 * and provides additional context for validation failures.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
public class ValidationException extends ServiceException {

    private final List<ValidationError> validationErrors;

    /**
     * Constructs a new validation exception with the specified message.
     * 
     * @param message the detail message
     */
    public ValidationException(String message) {
        super(message);
        this.validationErrors = new ArrayList<>();
    }

    /**
     * Constructs a new validation exception with validation errors.
     * 
     * @param message the detail message
     * @param validationErrors the list of validation errors
     */
    public ValidationException(String message, List<ValidationError> validationErrors) {
        super(message);
        this.validationErrors = new ArrayList<>(validationErrors);
    }

    /**
     * Constructs a new validation exception with a single validation error.
     * 
     * @param message the detail message
     * @param field the field that failed validation
     * @param errorMessage the validation error message
     */
    public ValidationException(String message, String field, String errorMessage) {
        super(message);
        this.validationErrors = new ArrayList<>();
        this.validationErrors.add(new ValidationError(field, errorMessage));
    }

    /**
     * Gets the validation errors associated with this exception.
     * 
     * @return an unmodifiable list of validation errors
     */
    public List<ValidationError> getValidationErrors() {
        return Collections.unmodifiableList(validationErrors);
    }

    /**
     * Checks if this exception has validation errors.
     * 
     * @return true if validation errors are present, false otherwise
     */
    public boolean hasValidationErrors() {
        return !validationErrors.isEmpty();
    }

    /**
     * Gets the count of validation errors.
     * 
     * @return the number of validation errors
     */
    public int getErrorCount() {
        return validationErrors.size();
    }

    /**
     * Creates a validation exception for a required field.
     * 
     * @param fieldName the name of the required field
     * @return ValidationException for required field
     */
    public static ValidationException requiredField(String fieldName) {
        return new ValidationException("Validation failed: required field missing", 
                                     fieldName, "This field is required");
    }

    /**
     * Creates a validation exception for invalid format.
     * 
     * @param fieldName the name of the field with invalid format
     * @param expectedFormat the expected format description
     * @return ValidationException for invalid format
     */
    public static ValidationException invalidFormat(String fieldName, String expectedFormat) {
        return new ValidationException("Validation failed: invalid format", 
                                     fieldName, "Invalid format. Expected: " + expectedFormat);
    }

    /**
     * Creates a validation exception for value out of range.
     * 
     * @param fieldName the name of the field with value out of range
     * @param minValue the minimum allowed value
     * @param maxValue the maximum allowed value
     * @return ValidationException for value out of range
     */
    public static ValidationException valueOutOfRange(String fieldName, Object minValue, Object maxValue) {
        return new ValidationException("Validation failed: value out of range", 
                                     fieldName, "Value must be between " + minValue + " and " + maxValue);
    }

    /**
     * Represents a single validation error.
     */
    public static class ValidationError {
        private final String field;
        private final String message;
        private final Object rejectedValue;

        /**
         * Constructs a new validation error.
         * 
         * @param field the field name
         * @param message the error message
         */
        public ValidationError(String field, String message) {
            this(field, message, null);
        }

        /**
         * Constructs a new validation error with rejected value.
         * 
         * @param field the field name
         * @param message the error message
         * @param rejectedValue the value that was rejected
         */
        public ValidationError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }

        /**
         * Gets the field name.
         * 
         * @return the field name
         */
        public String getField() {
            return field;
        }

        /**
         * Gets the error message.
         * 
         * @return the error message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Gets the rejected value.
         * 
         * @return the rejected value, or null if not provided
         */
        public Object getRejectedValue() {
            return rejectedValue;
        }

        @Override
        public String toString() {
            return "ValidationError{" +
                   "field='" + field + '\'' +
                   ", message='" + message + '\'' +
                   ", rejectedValue=" + rejectedValue +
                   '}';
        }
    }
} 