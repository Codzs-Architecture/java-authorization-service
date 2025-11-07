package com.codzs.exception.bean;

/**
 * Represents a field-specific error.
 * This class provides detailed information about validation errors
 * for specific fields in request data.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
public class FieldError {
    
    private String field;
    private String message;
    private Object rejectedValue;

    // Private constructor for builder pattern
    private FieldError() {}

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    /**
     * Builder class for FieldError.
     */
    public static class Builder {
        private final FieldError fieldError;

        public Builder() {
            this.fieldError = new FieldError();
        }

        public Builder field(String field) {
            fieldError.field = field;
            return this;
        }

        public Builder message(String message) {
            fieldError.message = message;
            return this;
        }

        public Builder rejectedValue(Object rejectedValue) {
            fieldError.rejectedValue = rejectedValue;
            return this;
        }

        public FieldError build() {
            return fieldError;
        }
    }
} 