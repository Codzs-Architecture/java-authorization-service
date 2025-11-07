// package com.codzs.framework.config;

// import com.codzs.framework.filter.CorrelationIdFilter;
// import org.springframework.boot.web.servlet.FilterRegistrationBean;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.Ordered;

// /**
//  * Configuration class for registering custom filters in the Spring Boot application.
//  * This class ensures proper ordering and configuration of filters that need to run
//  * at specific points in the filter chain.
//  * 
//  * @author Codzs Team
//  * @since 1.0
//  */
// @Configuration
// public class FilterConfig {
    
//     /**
//      * Registers the CorrelationIdFilter with explicit ordering to ensure it runs
//      * before all other filters in the chain.
//      * 
//      * This filter is crucial for setting up correlation context for the entire
//      * request processing pipeline, including logging and database operations.
//      * 
//      * @param correlationIdFilter the correlation ID filter to register
//      * @return FilterRegistrationBean with highest precedence
//      */
//     @Bean
//     public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilterRegistration(
//             CorrelationIdFilter correlationIdFilter) {
        
//         FilterRegistrationBean<CorrelationIdFilter> registration = 
//             new FilterRegistrationBean<>(correlationIdFilter);
        
//         // Set highest precedence to ensure this filter runs first
//         registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        
//         // Apply to all URL patterns
//         registration.addUrlPatterns("/*");
        
//         // Set filter name for identification
//         registration.setName("correlationIdFilter");
        
//         return registration;
//     }
// }