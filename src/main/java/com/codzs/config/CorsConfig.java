package com.codzs.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * General CORS configuration for the application.
 * Note: OAuth2 authorization endpoints use a more restrictive CORS configuration
 * defined in {@link com.codzs.config.security.SecurityHeadersConfigurer}.
 */
@Configuration(proxyBeanMethods = false)
@Order(-1)
public class CorsConfig {

	@Value("${app.cors.allowed-origins:http://localhost:4200,http://127.0.0.1:4200,https://local.codzs.com:4200}")
	private String allowedOrigins;

	@Value("${app.cors.allow-credentials:true}")
	private boolean allowCredentials;

	@Bean
	@Primary
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		// Only allow safe and necessary headers - no wildcard
		config.setAllowedHeaders(Arrays.asList(
			"Content-Type", 
			"Authorization", 
			"X-Requested-With", 
			"X-XSRF-TOKEN",
			"Cache-Control"
		));
		// Only allow necessary HTTP methods for OAuth2 flow
		config.setAllowedMethods(Arrays.asList("GET", "POST", "HEAD", "OPTIONS"));
		
		// Parse comma-separated origins and filter out wildcards if credentials are enabled
		List<String> origins = Arrays.asList(allowedOrigins.split(","));
		if (allowCredentials && origins.contains("*")) {
			// If credentials are required, we cannot use wildcard origins
			// Use specific origins instead
			origins = Arrays.asList("http://localhost:4200", "http://127.0.0.1:4200", "https://local.codzs.com:4200");
		}
		
		config.setAllowedOrigins(origins);
		config.setAllowCredentials(allowCredentials);
		// Only expose necessary headers
		config.setExposedHeaders(Arrays.asList("X-XSRF-TOKEN"));
		// Set max age for preflight cache (reduce OPTIONS requests)
		config.setMaxAge(3600L);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}