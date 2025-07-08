// package com.codzs.acl.context;

// import javax.sql.DataSource;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
// import org.springframework.cache.Cache;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
// import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
// import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
// import org.springframework.security.acls.AclPermissionCacheOptimizer;
// import org.springframework.security.acls.AclPermissionEvaluator;
// import org.springframework.security.acls.domain.AclAuthorizationStrategy;
// import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
// import org.springframework.security.acls.domain.ConsoleAuditLogger;
// import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
// import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
// import org.springframework.security.acls.jdbc.BasicLookupStrategy;
// import org.springframework.security.acls.jdbc.JdbcMutableAclService;
// import org.springframework.security.acls.jdbc.LookupStrategy;
// import org.springframework.security.acls.model.PermissionGrantingStrategy;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;

// import com.codzs.utility.RedisUtils;

// @Configuration
// @EnableAutoConfiguration
// public class ACLContext {
//     @Value("${acl.redis.cache.key}")
//     private String aclCacheKey;

//     @Autowired
//     DataSource dataSource;

//     @Autowired
//     LettuceConnectionFactory redisConnectionFactory;

//     @Bean
//     public SpringCacheBasedAclCache aclCache() {
//         return new SpringCacheBasedAclCache(getRedisCache(), permissionGrantingStrategy(), aclAuthorizationStrategy());
//     }

//     @Bean
//     public Cache getRedisCache()  {
//         return RedisUtils.getRedisCache(redisConnectionFactory, aclCacheKey);
//     }

//     @Bean
//     public PermissionGrantingStrategy permissionGrantingStrategy() {
//         return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
//     }

//     @Bean
//     public AclAuthorizationStrategy aclAuthorizationStrategy() {
//         return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
//     }

//     @Bean
//     public MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler() {
//         DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
//         AclPermissionEvaluator permissionEvaluator = new AclPermissionEvaluator(aclService());
//         expressionHandler.setPermissionEvaluator(permissionEvaluator);
//         expressionHandler.setPermissionCacheOptimizer(new AclPermissionCacheOptimizer(aclService()));
//         return expressionHandler;
//     }

//     @Bean
//     public LookupStrategy lookupStrategy() {
//         return new BasicLookupStrategy(dataSource, aclCache(), aclAuthorizationStrategy(), new ConsoleAuditLogger());
//     }

//     @Bean
//     public JdbcMutableAclService aclService() {
//         return new JdbcMutableAclService(dataSource, lookupStrategy(), aclCache());
//     }

// }