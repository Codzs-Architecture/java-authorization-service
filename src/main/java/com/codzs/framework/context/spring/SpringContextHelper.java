package com.codzs.framework.context.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring context helper to access Spring beans from non-Spring managed classes.
 * This utility allows entities and other non-Spring classes to access Spring beans
 * when needed for dynamic configuration retrieval.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
public class SpringContextHelper implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * Sets the application context. Called automatically by Spring.
     * 
     * @param context the ApplicationContext object to be used by this object
     * @throws BeansException in case of context initialization errors
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringContextHelper.applicationContext = context;
    }

    /**
     * Gets a Spring bean by class type.
     * 
     * @param <T> the type of the bean
     * @param beanClass the class of the bean to retrieve
     * @return the bean instance
     * @throws IllegalStateException if Spring context is not available
     */
    public static <T> T getBean(Class<T> beanClass) {
        if (applicationContext == null) {
            throw new IllegalStateException("Spring context is not available. " +
                "Ensure SpringContextHelper is properly initialized.");
        }
        return applicationContext.getBean(beanClass);
    }

    /**
     * Gets a Spring bean by name.
     * 
     * @param beanName the name of the bean to retrieve
     * @return the bean instance
     * @throws IllegalStateException if Spring context is not available
     */
    public static Object getBean(String beanName) {
        if (applicationContext == null) {
            throw new IllegalStateException("Spring context is not available. " +
                "Ensure SpringContextHelper is properly initialized.");
        }
        return applicationContext.getBean(beanName);
    }

    /**
     * Gets a Spring bean by name and class type.
     * 
     * @param <T> the type of the bean
     * @param beanName the name of the bean to retrieve
     * @param beanClass the class of the bean to retrieve
     * @return the bean instance
     * @throws IllegalStateException if Spring context is not available
     */
    public static <T> T getBean(String beanName, Class<T> beanClass) {
        if (applicationContext == null) {
            throw new IllegalStateException("Spring context is not available. " +
                "Ensure SpringContextHelper is properly initialized.");
        }
        return applicationContext.getBean(beanName, beanClass);
    }

    /**
     * Checks if the Spring context is available.
     * 
     * @return true if context is available, false otherwise
     */
    public static boolean isContextAvailable() {
        return applicationContext != null;
    }

    /**
     * Safely gets a bean with null checking and exception handling.
     * 
     * @param <T> the type of the bean
     * @param beanClass the class of the bean to retrieve
     * @return the bean instance or null if not available
     */
    public static <T> T getBeanSafely(Class<T> beanClass) {
        try {
            if (applicationContext != null) {
                return applicationContext.getBean(beanClass);
            }
        } catch (Exception e) {
            // Log if needed, but don't throw - return null for graceful degradation
        }
        return null;
    }
}