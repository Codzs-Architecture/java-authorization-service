package com.codzs.oauth2.authentication.device.config;

import com.codzs.oauth2.authentication.device.DeviceClientAuthenticationProvider;
import com.codzs.oauth2.authentication.device.error.DeviceAuthenticationErrorHandler;
import com.codzs.web.authentication.DeviceClientAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

/**
 * Configuration class for device authentication.
 * This class handles the configuration of device flow authentication
 * including device code and user code validation.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
// @Configuration  // Commented out to prevent automatic loading - this is an example
public class DeviceAuthenticationConfig {

    /**
     * Creates a DeviceClientAuthenticationProvider bean.
     * This demonstrates how the provider could be configured as a bean
     * with proper dependency injection.
     * 
     * @param registeredClientRepository the registered client repository
     * @return configured DeviceClientAuthenticationProvider
     */
    @Bean
    public DeviceClientAuthenticationProvider deviceClientAuthenticationProvider(
            RegisteredClientRepository registeredClientRepository) {
        return new DeviceClientAuthenticationProvider(registeredClientRepository);
    }

    /**
     * Creates a DeviceClientAuthenticationConverter bean.
     * This demonstrates how the converter could be configured as a bean
     * with proper dependency injection.
     * 
     * @param authorizationServerSettings the authorization server settings
     * @return configured DeviceClientAuthenticationConverter
     */
    @Bean
    public DeviceClientAuthenticationConverter deviceClientAuthenticationConverter(
            AuthorizationServerSettings authorizationServerSettings) {
        return new DeviceClientAuthenticationConverter(
                authorizationServerSettings.getDeviceAuthorizationEndpoint()
        );
    }

    /**
     * Creates a DeviceAuthenticationErrorHandler bean.
     * This demonstrates how the error handler could be configured as a bean
     * for better testability and customization.
     * 
     * @return configured DeviceAuthenticationErrorHandler
     */
    @Bean
    public DeviceAuthenticationErrorHandler deviceAuthenticationErrorHandler() {
        return new DeviceAuthenticationErrorHandler();
    }
} 