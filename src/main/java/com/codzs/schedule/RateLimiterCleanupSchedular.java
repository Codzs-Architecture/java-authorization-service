package com.codzs.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.codzs.filter.DeviceAuthorizationRateLimitingFilter;

/**
 * Configuration class for scheduled tasks.
 * Handles periodic cleanup and maintenance operations for security components.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Configuration
@EnableScheduling
public class RateLimiterCleanupSchedular {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterCleanupSchedular.class);

    @Autowired
    private DeviceAuthorizationRateLimitingFilter rateLimitingFilter;

    /**
     * Scheduled task to clean up expired rate limiters.
     * Runs every 5 minutes to free up memory and maintain performance.
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void cleanupExpiredRateLimiters() {
        try {
            logger.debug("Starting scheduled cleanup of expired rate limiters");
            rateLimitingFilter.cleanupExpiredRateLimiters();
            logger.debug("Completed scheduled cleanup of expired rate limiters");
        } catch (Exception e) {
            logger.error("Error during scheduled cleanup of rate limiters", e);
        }
    }
}