package com.codzs.oauth2.authentication.federation.config;

import com.codzs.federation.FederatedIdentityAuthenticationSuccessHandler;
import com.codzs.federation.FederatedIdentityIdTokenCustomizer;
import com.codzs.federation.UserRepositoryOAuth2UserHandler;
import com.codzs.oauth2.authentication.federation.repository.DefaultFederatedUserHandler;
import com.codzs.oauth2.authentication.federation.repository.FederatedUserHandler;
import com.codzs.oauth2.authentication.federation.token.ClaimExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;

/**
 * Optional configuration class for federation authentication components.
 * This class demonstrates how federation authentication components could be
 * configured as Spring beans for better dependency injection and testability.
 * 
 * This configuration is NOT automatically loaded and is provided as an example
 * of improved Spring configuration practices.
 * 
 * To use this configuration, either:
 * 1. Add @Configuration annotation and @Import it in your main configuration
 * 2. Or manually use @Bean methods where needed
 * 
 * @author Federation Authentication Configuration
 * @since 1.1
 */
// @Configuration  // Commented out to prevent automatic loading - this is an example
public class FederatedAuthenticationConfig {

    /**
     * Creates a FederatedUserHandler bean.
     * This demonstrates how the federated user handler could be configured as a bean
     * with the strategy pattern for better extensibility.
     * 
     * @return configured FederatedUserHandler
     */
    @Bean
    public FederatedUserHandler federatedUserHandler() {
        return new DefaultFederatedUserHandler();
    }

    /**
     * Creates a FederatedIdentityAuthenticationSuccessHandler bean.
     * This demonstrates how the success handler could be configured as a bean
     * with proper dependency injection.
     * 
     * @return configured FederatedIdentityAuthenticationSuccessHandler
     */
    @Bean
    public FederatedIdentityAuthenticationSuccessHandler federatedIdentityAuthenticationSuccessHandler() {
        return new FederatedIdentityAuthenticationSuccessHandler();
    }

    /**
     * Creates a FederatedIdentityIdTokenCustomizer bean.
     * This demonstrates how the token customizer could be configured as a bean
     * with proper dependency injection.
     * 
     * @return configured FederatedIdentityIdTokenCustomizer
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> federatedIdentityIdTokenCustomizer() {
        return new FederatedIdentityIdTokenCustomizer();
    }

    /**
     * Creates a UserRepositoryOAuth2UserHandler bean.
     * This demonstrates how the user handler could be configured as a bean
     * with proper dependency injection.
     * 
     * @return configured UserRepositoryOAuth2UserHandler
     */
    @Bean
    public UserRepositoryOAuth2UserHandler userRepositoryOAuth2UserHandler() {
        return new UserRepositoryOAuth2UserHandler();
    }

    /**
     * Creates a ClaimExtractor bean.
     * This demonstrates how the claim extractor could be configured as a bean
     * for better testability and customization.
     * 
     * @return configured ClaimExtractor
     */
    @Bean
    public ClaimExtractor claimExtractor() {
        return new ClaimExtractor();
    }
} 