package com.codzs.framework.constant;

import com.codzs.framework.base.ConfigParameterBase;

/**
 * Enumeration of service types for database schema configuration.
 * Defines the types of services that can have dedicated database schemas.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public class ServiceTypeEnum extends ConfigParameterBase {

    public static final String AUTH = "auth";
    public static final String BILLING = "billing";
    public static final String ANALYTICS = "analytics";
    public static final String AUDIT = "audit";
    public static final String RESOURCE = "resource";
    public static final String BFF = "bff";

    @Override
    protected String getOptionsString() {
        return AUTH + "," + BILLING + "," + ANALYTICS + "," + AUDIT + "," + RESOURCE + "," + BFF;
    }

    @Override
    protected String getDescription() {
        return "Types of services that can have dedicated database schemas";
    }

    /**
     * Gets the default service type for new organizations
     * 
     * @return the default service type
     */
    public static String getDefault() {
        return AUTH;
    }

    /**
     * Validates if the given service type is valid
     * 
     * @param serviceType the service type to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String serviceType) {
        return AUTH.equals(serviceType) ||
               BILLING.equals(serviceType) ||
               ANALYTICS.equals(serviceType) ||
               AUDIT.equals(serviceType) ||
               RESOURCE.equals(serviceType) ||
               BFF.equals(serviceType);
    }

    /**
     * Gets all available service types
     * 
     * @return array of all service types
     */
    public static String[] getAllValues() {
        return new String[]{AUTH, BILLING, ANALYTICS, AUDIT, RESOURCE, BFF};
    }
}