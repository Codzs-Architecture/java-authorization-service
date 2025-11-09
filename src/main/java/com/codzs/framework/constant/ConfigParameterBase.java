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
package com.codzs.framework.constant;

import java.util.Arrays;
import java.util.List;

/**
 * Base class for configuration parameters that come from config server.
 * This class provides common functionality for handling parameter options and descriptions
 * from the param table in the config database.
 * 
 * Concrete classes should inject the specific property values using @Value annotations:
 * - @Value("${prefix.paramname.options}") for optionsString
 * - @Value("${prefix.paramname.description}") for description
 * - @Value("${prefix.paramname.default:}") for defaultValue (optional)
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
public abstract class ConfigParameterBase {

    protected List<String> options;

    /**
     * Abstract method to get the raw options string from the concrete class.
     * This allows the concrete class to inject the value using @Value annotation.
     * 
     * @return the raw options string from config server
     */
    protected abstract String getOptionsString();

    /**
     * Abstract method to get the description from the concrete class.
     * This allows the concrete class to inject the value using @Value annotation.
     * 
     * @return the description from config server
     */
    protected abstract String getDescription();

    /**
     * Parses the options string into a list of options.
     * This method is called lazily when getOptions() is first accessed.
     */
    protected void initializeOptions() {
        if (options == null) {
            String optionsString = getOptionsString();
            if (optionsString != null && !(optionsString = optionsString.trim()).isEmpty()) {
                // The processor converts arrays to comma-separated strings
                this.options = optionsString.contains(",") ? 
                    Arrays.asList(optionsString.split(",")) : 
                    Arrays.asList(optionsString);
            }
        }
    }

    /**
     * Gets the list of options for this parameter.
     * Options are parsed from the optionsString on first access.
     * 
     * @return List of option values, never null
     */
    public List<String> getOptions() {
        initializeOptions();
        return options != null ? options : Arrays.asList();
    }

    /**
     * Gets the default value for this parameter.
     * 
     * @return Default value from config server, may be null if not configured
     */
    public String getDefaultValue() {
        return null;
    }

    /**
     * Checks if this parameter has a default value configured.
     * 
     * @return true if default value is available, false otherwise
     */
    public boolean hasDefaultValue() {
        String defaultValue = getDefaultValue();
        return defaultValue != null && !defaultValue.trim().isEmpty();
    }

    /**
     * Checks if the parameter has valid options configured.
     * 
     * @return true if options are available, false otherwise
     */
    public boolean hasOptions() {
        return getOptions().size() > 0;
    }

    /**
     * Gets the number of available options.
     * 
     * @return Number of options
     */
    public int getOptionsCount() {
        return getOptions().size();
    }

    /**
     * Checks if a specific value is a valid option.
     * 
     * @param value the value to check
     * @return true if the value is a valid option, false otherwise
     */
    public boolean isValidOption(String value) {
        return value != null && getOptions().contains(value);
    }

    /**
     * Gets the effective value to use - returns the provided value if valid, otherwise returns default.
     * 
     * @param value the value to validate
     * @return the value if valid, otherwise the default value, or null if no default is configured
     */
    public String getEffectiveValue(String value) {
        if (isValidOption(value)) {
            return value;
        }
        return hasDefaultValue() ? getDefaultValue() : null;
    }

    /**
     * Validates that the default value (if configured) is also a valid option.
     * 
     * @return true if default value is valid or not configured, false if default is invalid
     */
    public boolean isDefaultValueValid() {
        return hasDefaultValue() || isValidOption(getDefaultValue());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "optionsCount=" + getOptionsCount() +
                ", description='" + getDescription() + '\'' +
                ", defaultValue='" + getDefaultValue() + '\'' +
                ", options=" + getOptions() +
                '}';
    }
}