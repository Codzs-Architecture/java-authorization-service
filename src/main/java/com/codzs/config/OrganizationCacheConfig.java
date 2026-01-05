// package com.codzs.config;

// import com.codzs.constant.organization.OrganizationConstant;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.cache.CacheManager;
// import org.springframework.cache.annotation.EnableCaching;
// import org.springframework.cache.caffeine.CaffeineCacheManager;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Primary;
// import com.github.benmanes.caffeine.cache.Caffeine;

// import java.util.concurrent.TimeUnit;

// /**
//  * Cache configuration for Organization module.
//  * Configures L1 (local) and L2 (distributed) caching strategies.
//  * Uses Caffeine for L1 cache and Redis for L2 cache.
//  * 
//  * @author Codzs Team
//  * @since 1.0
//  */
// @Configuration
// @EnableCaching
// public class OrganizationCacheConfig {

//     @Autowired
//     private OrganizationConfig organizationConfig;

//     /**
//      * Primary cache manager for L1 (local) caching using Caffeine.
//      * Used for frequently accessed, small-sized data.
//      */
//     @Bean
//     @Primary
//     public CacheManager organizationCacheManager() {
//         CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
//         // Configure default cache settings
//         cacheManager.setCaffeine(caffeineCacheBuilder());
        
//         // Set cache names
//         cacheManager.setCacheNames(
//             OrganizationConstant.CACHE_ORGANIZATION,
//             OrganizationConstant.CACHE_ORGANIZATION_LIST,
//             OrganizationConstant.CACHE_ORGANIZATION_PLANS,
//             OrganizationConstant.CACHE_ORGANIZATION_DOMAINS,
//             OrganizationConstant.CACHE_ORGANIZATION_DATABASES,
//             OrganizationConstant.CACHE_ORGANIZATION_HIERARCHY
//         );
        
//         return cacheManager;
//     }

//     /**
//      * Caffeine cache builder with organization-specific configurations.
//      */
//     @Bean
//     public Caffeine<Object, Object> caffeineCacheBuilder() {
//         return Caffeine.newBuilder()
//                 .maximumSize(1000) // Maximum number of entries
//                 .expireAfterWrite(organizationConfig.getCacheTtlOrganization(), TimeUnit.SECONDS)
//                 .expireAfterAccess(30, TimeUnit.MINUTES) // Expire after 30 minutes of no access
//                 .recordStats() // Enable cache statistics
//                 .removalListener((key, value, cause) -> {
//                     // Log cache evictions for monitoring
//                     // logger.debug("Cache entry removed: key={}, cause={}", key, cause);
//                 });
//     }

//     /**
//      * Cache manager for organization-specific data with custom TTL.
//      * Used for organization core data that changes infrequently.
//      */
//     @Bean("organizationDataCacheManager")
//     public CacheManager organizationDataCacheManager() {
//         CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
//         cacheManager.setCaffeine(
//             Caffeine.newBuilder()
//                 .maximumSize(500)
//                 .expireAfterWrite(organizationConfig.getCacheTtlOrganization(), TimeUnit.SECONDS)
//                 .recordStats()
//         );
        
//         cacheManager.setCacheNames(OrganizationConstant.CACHE_ORGANIZATION);
//         return cacheManager;
//     }

//     /**
//      * Cache manager for organization list data with shorter TTL.
//      * Used for list/search operations that should refresh more frequently.
//      */
//     @Bean("organizationListCacheManager")
//     public CacheManager organizationListCacheManager() {
//         CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
//         cacheManager.setCaffeine(
//             Caffeine.newBuilder()
//                 .maximumSize(100)
//                 .expireAfterWrite(organizationConfig.getCacheTtlOrganizationList(), TimeUnit.SECONDS)
//                 .recordStats()
//         );
        
//         cacheManager.setCacheNames(OrganizationConstant.CACHE_ORGANIZATION_LIST);
//         return cacheManager;
//     }

//     /**
//      * Cache manager for organization plans with longer TTL.
//      * Used for plan data that changes less frequently.
//      */
//     @Bean("organizationPlanCacheManager")
//     public CacheManager organizationPlanCacheManager() {
//         CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
//         cacheManager.setCaffeine(
//             Caffeine.newBuilder()
//                 .maximumSize(200)
//                 .expireAfterWrite(organizationConfig.getCacheTtlOrganizationPlans(), TimeUnit.SECONDS)
//                 .recordStats()
//         );
        
//         cacheManager.setCacheNames(OrganizationConstant.CACHE_ORGANIZATION_PLANS);
//         return cacheManager;
//     }

//     /**
//      * Cache manager for organization hierarchy data.
//      * Used for organizational structure that rarely changes.
//      */
//     @Bean("organizationHierarchyCacheManager")
//     public CacheManager organizationHierarchyCacheManager() {
//         CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
//         cacheManager.setCaffeine(
//             Caffeine.newBuilder()
//                 .maximumSize(50)
//                 .expireAfterWrite(2, TimeUnit.HOURS) // Longer TTL for hierarchy
//                 .recordStats()
//         );
        
//         cacheManager.setCacheNames(OrganizationConstant.CACHE_ORGANIZATION_HIERARCHY);
//         return cacheManager;
//     }

//     /**
//      * Cache manager for organization domains and databases.
//      * Used for configuration data with moderate refresh frequency.
//      */
//     @Bean("organizationConfigCacheManager")
//     public CacheManager organizationConfigCacheManager() {
//         CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
//         cacheManager.setCaffeine(
//             Caffeine.newBuilder()
//                 .maximumSize(300)
//                 .expireAfterWrite(1, TimeUnit.HOURS)
//                 .recordStats()
//         );
        
//         cacheManager.setCacheNames(
//             OrganizationConstant.CACHE_ORGANIZATION_DOMAINS,
//             OrganizationConstant.CACHE_ORGANIZATION_DATABASES
//         );
//         return cacheManager;
//     }
// }