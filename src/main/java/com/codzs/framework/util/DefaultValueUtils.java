package com.codzs.framework.util;

import com.codzs.framework.annotation.validation.ApplyDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Utility class for applying default values to DTOs.
 * Handles automatic default value application for DTOs annotated with @ApplyDefaults.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public final class DefaultValueUtils {

    private static final Logger logger = LoggerFactory.getLogger(DefaultValueUtils.class);

    private DefaultValueUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Applies default values to the given object if it's annotated with @ApplyDefaults.
     * 
     * @param object the object to apply defaults to
     * @return the object with defaults applied (same instance)
     */
    public static <T> T applyDefaults(T object) {
        if (object == null) {
            return null;
        }

        Class<?> clazz = object.getClass();
        ApplyDefaults annotation = clazz.getAnnotation(ApplyDefaults.class);
        
        if (annotation == null) {
            return object;
        }

        try {
            String methodName = annotation.methodName();
            Method method = clazz.getMethod(methodName);
            method.invoke(object);
            logger.debug("Applied defaults to {} using method {}", clazz.getSimpleName(), methodName);
        } catch (Exception e) {
            logger.warn("Failed to apply defaults to {}: {}", clazz.getSimpleName(), e.getMessage());
        }

        return object;
    }

    /**
     * Recursively applies defaults to an object and its nested objects.
     * 
     * @param object the object to apply defaults to
     * @return the object with defaults applied (same instance)
     */
    public static <T> T applyDefaultsRecursively(T object) {
        if (object == null) {
            return null;
        }

        // Apply defaults to the main object
        applyDefaults(object);

        // TODO: Add logic to recursively apply defaults to nested objects
        // This would require reflection to find all fields and check if they need defaults

        return object;
    }
}