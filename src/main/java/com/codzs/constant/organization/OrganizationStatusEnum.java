/*
 * Copyright 2020-2025 Nitin Khaitan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codzs.constant.organization;

/**
 * Enum for organization account status values.
 * 
 * Available options: PENDING, ACTIVE, SUSPENDED, DELETED
 * Default value: PENDING
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
public enum OrganizationStatusEnum {
 
    /**
     * Organization is pending activation
     */
    PENDING("PENDING", "Organization is pending activation"),
    
    /**
     * Organization is active and operational
     */
    ACTIVE("ACTIVE", "Organization is active and operational"),
    
    /**
     * Organization is temporarily suspended
     */
    SUSPENDED("SUSPENDED", "Organization is temporarily suspended"),
    
    /**
     * Organization has been deleted (soft delete)
     */
    DELETED("DELETED", "Organization has been deleted");

    private final String value;
    private final String description;

    OrganizationStatusEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * Gets the string value of the enum
     * 
     * @return the string representation
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the description of the status
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the default status (PENDING)
     * 
     * @return the default OrganizationStatusEnum
     */
    public static OrganizationStatusEnum getDefault() {
        return PENDING;
    }

    /**
     * Converts a string value to the corresponding enum
     * 
     * @param value the string value
     * @return the corresponding enum, or null if not found
     */
    public static OrganizationStatusEnum fromValue(String value) {
        if (value == null) {
            return null;
        }
        
        for (OrganizationStatusEnum status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }

    /**
     * Validates if a string value is a valid organization status
     * 
     * @param value the value to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String value) {
        return fromValue(value) != null;
    }

    @Override
    public String toString() {
        return value;
    }
}