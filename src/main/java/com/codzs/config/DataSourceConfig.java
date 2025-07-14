package com.codzs.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for DataSource.
 * Maps management.datasource properties to the primary datasource.
 */
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "management.datasource")
    @ConditionalOnProperty(name = "management.datasource.url")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
}