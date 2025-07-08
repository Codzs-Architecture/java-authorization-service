package com.codzs.service.error;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Service interface for handling error processing operations.
 * This interface defines the contract for error message processing,
 * formatting, and model preparation for error views.
 * 
 * @author Error Service Interface
 * @since 1.1
 */
public interface ErrorService {

    /**
     * Process error request and create appropriate error model.
     * Analyzes the error from the request and determines the appropriate
     * error title and message for display.
     * 
     * @param request the HTTP request containing error information
     * @return ErrorModel containing processed error information
     */
    ErrorModel processError(HttpServletRequest request);

    /**
     * Extract raw error message from request.
     * Retrieves the error message from the request attributes.
     * 
     * @param request the HTTP request containing error information
     * @return the raw error message string
     */
    String extractErrorMessage(HttpServletRequest request);

    /**
     * Determine if an error message indicates access denied.
     * Analyzes the error message to determine if it represents an access denied scenario.
     * 
     * @param errorMessage the raw error message
     * @return true if the error indicates access denied, false otherwise
     */
    boolean isAccessDeniedError(String errorMessage);

    /**
     * Model class for error information.
     * Contains processed error information including title and message
     * formatted for display to users.
     */
    class ErrorModel {
        private final String errorTitle;
        private final String errorMessage;

        public ErrorModel(String errorTitle, String errorMessage) {
            this.errorTitle = errorTitle;
            this.errorMessage = errorMessage;
        }

        public String getErrorTitle() {
            return errorTitle;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        /**
         * Create an access denied error model.
         * 
         * @return ErrorModel for access denied scenario
         */
        public static ErrorModel accessDenied() {
            return new ErrorModel("Access Denied", "You have denied access.");
        }

        /**
         * Create a generic error model.
         * 
         * @param errorMessage the error message to display
         * @return ErrorModel for generic error scenario
         */
        public static ErrorModel genericError(String errorMessage) {
            return new ErrorModel("Error", errorMessage);
        }
    }
} 