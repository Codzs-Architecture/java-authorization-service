/*
 * Copyright 2020-2025 Nitin Khaitan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codzs.migration;

import com.codzs.entity.oauth2.OAuth2RegisteredClient;
import com.codzs.entity.security.User;
import com.codzs.entity.security.Authority;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

/**
 * Initial database setup migration for MongoDB.
 * Creates initial users, authorities, and OAuth2 clients.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@ChangeUnit(id = "initial-database-setup", order = "001", author = "Nitin Khaitan")
public class InitialDatabaseSetup {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Setup initial data and indexes for the authorization service.
     * 
     * @param mongoTemplate MongoDB template for operations
     */
    @Execution
    public void setupInitialData(MongoTemplate mongoTemplate) {
        // Create indexes
        createIndexes(mongoTemplate);
        
        // Create initial users and authorities
        createInitialUsers(mongoTemplate);
        
        // Create initial OAuth2 clients
        createInitialOAuth2Clients(mongoTemplate);
    }

    /**
     * Rollback method to clean up initial data if needed.
     * 
     * @param mongoTemplate MongoDB template for operations
     */
    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        // Clean up collections (only if needed for rollback)
        mongoTemplate.dropCollection("users");
        mongoTemplate.dropCollection("authorities");
        mongoTemplate.dropCollection("oauth2_registered_client");
        mongoTemplate.dropCollection("oauth2_authorization");
        mongoTemplate.dropCollection("oauth2_authorization_consent");
    }

    private void createIndexes(MongoTemplate mongoTemplate) {
        // Note: @Id fields are automatically unique in MongoDB, no need to create explicit indexes for them
        // User.username is @Id, so it doesn't need an explicit index
        
        // Authority collection indexes - for non-@Id fields  
        IndexOperations authorityIndexOps = mongoTemplate.indexOps(Authority.class);
        authorityIndexOps.ensureIndex(new Index().on("username", org.springframework.data.domain.Sort.Direction.ASC));
        authorityIndexOps.ensureIndex(new Index().on("authority", org.springframework.data.domain.Sort.Direction.ASC));
        
        // OAuth2 registered client indexes - clientId is a separate unique field (not @Id)
        IndexOperations clientIndexOps = mongoTemplate.indexOps(OAuth2RegisteredClient.class);
        clientIndexOps.ensureIndex(new Index().on("clientId", org.springframework.data.domain.Sort.Direction.ASC).unique());
    }

    private void createInitialUsers(MongoTemplate mongoTemplate) {
        // Create admin user
        User adminUser = new User("admin", passwordEncoder.encode("admin"), true);
        mongoTemplate.save(adminUser);
        
        Authority adminAuth = new Authority("admin", "ADMIN");
        mongoTemplate.save(adminAuth);
        
        // Create test user
        User testUser = new User("user", passwordEncoder.encode("password"), true);
        mongoTemplate.save(testUser);
        
        Authority userAuth = new Authority("user", "USER");
        mongoTemplate.save(userAuth);
    }

    private void createInitialOAuth2Clients(MongoTemplate mongoTemplate) {
        // Create a default client for testing
        OAuth2RegisteredClient client = new OAuth2RegisteredClient();
        client.setId("default-client-id");
        client.setClientId("default-client");
        client.setClientIdIssuedAt(LocalDateTime.now());
        client.setClientSecret("{bcrypt}" + passwordEncoder.encode("secret")); // Properly encoded secret
        client.setClientName("Default OAuth2 Client");
        client.setClientAuthenticationMethods("client_secret_basic,client_secret_post");
        client.setAuthorizationGrantTypes("authorization_code,refresh_token,client_credentials");
        client.setRedirectUris("http://localhost:4200/authorized,https://local.codzs.com:4200/authorized");
        client.setPostLogoutRedirectUris("http://localhost:4200/logged-out,https://local.codzs.com:4200/logged-out");
        client.setScopes("openid,profile,email,read,write");
        client.setClientSettings("{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}");
        client.setTokenSettings("{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",1800.000000000],\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",3600.000000000]}");
        
        mongoTemplate.save(client);
    }
}