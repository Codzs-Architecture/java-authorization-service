package com.codzs.framework.util;

import org.springframework.lang.Nullable;

/**
 * Static utility for entity default value initialization.
 * Safe for use in entity constructors and @PostConstruct methods.
 * Does not depend on Spring context injection.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public final class StringUtil {

    private StringUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // =================== SAFE SETTERS ===================

    /**
     * Safely sets a field to default if it's null.
     */
    public static String setDefaultIfNull(@Nullable String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * Safely sets a Boolean field to default if it's null.
     */
    public static Boolean setDefaultIfNull(@Nullable Boolean value, boolean defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * Safely sets an enum field to default if it's null.
     */
    public static <T> T setDefaultIfNull(@Nullable T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}