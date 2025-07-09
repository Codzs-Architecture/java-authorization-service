package com.codzs.exception.handler;

/**
 * Represents device context details.
 * This class provides specific information about device flow errors
 * including device code, user code, and device flow metadata.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
public class DeviceContextDetails {
    
    private String deviceCode;
    private String userCode;

    // Private constructor for builder pattern
    private DeviceContextDetails() {}

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getDeviceCode() {
        return deviceCode;
    }

    public String getUserCode() {
        return userCode;
    }

    /**
     * Builder class for DeviceContextDetails.
     */
    public static class Builder {
        private final DeviceContextDetails deviceContextDetails;

        public Builder() {
            this.deviceContextDetails = new DeviceContextDetails();
        }

        public Builder deviceCode(String deviceCode) {
            deviceContextDetails.deviceCode = deviceCode;
            return this;
        }

        public Builder userCode(String userCode) {
            deviceContextDetails.userCode = userCode;
            return this;
        }

        public DeviceContextDetails build() {
            return deviceContextDetails;
        }
    }
} 