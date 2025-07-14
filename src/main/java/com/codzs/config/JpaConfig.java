package com.codzs.config;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for JPA properties.
 * Maps management.jpa properties to the primary JPA configuration.
 */
@Configuration
public class JpaConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "management.jpa")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }
}