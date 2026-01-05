package com.codzs.service.device;

/**
 * Service interface for handling device flow operations.
 * This interface defines the contract for device flow business logic,
 * including device code generation, user code validation, and device flow management.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
public interface DeviceService {

    /**
     * Process device activation request.
     * Handles the device activation flow including user code validation
     * and appropriate redirection logic.
     * 
     * @param userCode the user code from the device activation request (optional)
     * @return DeviceActivationResult containing the appropriate view or redirect
     */
    DeviceActivationResult processDeviceActivation(String userCode);

    /**
     * Process device activation completion.
     * Handles the successful completion of device activation.
     * 
     * @return the view name for the activation success page
     */
    String processDeviceActivationSuccess();

    /**
     * Process device success callback.
     * Handles success callback scenarios for device flow.
     * 
     * @return the view name for the success page
     */
    String processDeviceSuccessCallback();

    /**
     * Result class for device activation processing.
     * Contains information about the activation result including
     * whether it's a redirect or view, and the target destination.
     */
    class DeviceActivationResult {
        private final boolean isRedirect;
        private final String destination;

        public DeviceActivationResult(boolean isRedirect, String destination) {
            this.isRedirect = isRedirect;
            this.destination = destination;
        }

        public boolean isRedirect() {
            return isRedirect;
        }

        public String getDestination() {
            return destination;
        }

        /**
         * Create a redirect result.
         * 
         * @param redirectUrl the URL to redirect to
         * @return DeviceActivationResult for redirect
         */
        public static DeviceActivationResult redirect(String redirectUrl) {
            return new DeviceActivationResult(true, redirectUrl);
        }

        /**
         * Create a view result.
         * 
         * @param viewName the view name to display
         * @return DeviceActivationResult for view
         */
        public static DeviceActivationResult view(String viewName) {
            return new DeviceActivationResult(false, viewName);
        }
    }
} 