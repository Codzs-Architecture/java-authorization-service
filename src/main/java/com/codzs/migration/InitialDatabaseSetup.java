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
import org.bson.Document;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Initial database setup migration for MongoDB.
 * Creates initial users, authorities, OAuth2 clients, IP blacklist, and API whitelist.
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

        // Create initial IP blacklist entries
        createInitialIpBlacklistEntries(mongoTemplate);

        // Create initial API whitelist entries
        createInitialApiWhitelistEntries(mongoTemplate);
    }

    /**
     * Rollback method to clean up initial data if needed.
     * 
     * @param mongoTemplate MongoDB template for operations
     */
    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        // Clean up collections (only if needed for rollback)
        mongoTemplate.dropCollection("user");
        mongoTemplate.dropCollection("authority");
        mongoTemplate.dropCollection("oauth2_registered_client");
        mongoTemplate.dropCollection("oauth2_authorization");
        mongoTemplate.dropCollection("oauth2_authorization_consent");
        mongoTemplate.dropCollection("ip_blacklist");
        mongoTemplate.dropCollection("api_whitelist");
    }

    private void createIndexes(MongoTemplate mongoTemplate) {
        // User.username is @Id, so doesn't need explicit index
        
        // Authority collection indexes
        IndexOperations authorityIndexOps = mongoTemplate.indexOps(Authority.class);
        authorityIndexOps.ensureIndex(new Index().on("username", org.springframework.data.domain.Sort.Direction.ASC));
        authorityIndexOps.ensureIndex(new Index().on("authority", org.springframework.data.domain.Sort.Direction.ASC));
        
        // OAuth2 registered client indexes
        IndexOperations clientIndexOps = mongoTemplate.indexOps(OAuth2RegisteredClient.class);
        clientIndexOps.ensureIndex(new Index().on("clientId", org.springframework.data.domain.Sort.Direction.ASC).unique());
        
        // IP blacklist index
        IndexOperations ipBlacklistOps = mongoTemplate.indexOps("ip_blacklist");
        ipBlacklistOps.ensureIndex(new Index().on("ipAddress", org.springframework.data.domain.Sort.Direction.ASC).unique());
        ipBlacklistOps.ensureIndex(new Index().on("isActive", org.springframework.data.domain.Sort.Direction.ASC).on("expires_at", org.springframework.data.domain.Sort.Direction.ASC));
        ipBlacklistOps.ensureIndex(new Index().on("createdAt", org.springframework.data.domain.Sort.Direction.ASC));
        
        // API whitelist index
        IndexOperations apiWhitelistOps = mongoTemplate.indexOps("api_whitelist");
        apiWhitelistOps.ensureIndex(new Index().on("isActive", org.springframework.data.domain.Sort.Direction.ASC).on("expires_at", org.springframework.data.domain.Sort.Direction.ASC));
        apiWhitelistOps.ensureIndex(new Index().on("priority", org.springframework.data.domain.Sort.Direction.ASC).on("is_active", org.springframework.data.domain.Sort.Direction.ASC));
        apiWhitelistOps.ensureIndex(new Index().on("endpointPattern", org.springframework.data.domain.Sort.Direction.ASC).on("is_active", org.springframework.data.domain.Sort.Direction.ASC));
        apiWhitelistOps.ensureIndex(new Index().on("clientId", org.springframework.data.domain.Sort.Direction.ASC).on("is_active", org.springframework.data.domain.Sort.Direction.ASC));
        apiWhitelistOps.ensureIndex(new Index().on("createdAt", org.springframework.data.domain.Sort.Direction.ASC));
    }

    private void createInitialUsers(MongoTemplate mongoTemplate) {
        // Create admin user
        User adminUser = new User("admin", "{bcrypt}$2a$10$r2lU4bm3aWztQnCVa7T21ek.ssD4vg7PzgXhnnz/SUv38Ph3LuISK", true);
        mongoTemplate.save(adminUser);
        
        Authority adminAuth = new Authority("admin", "ADMIN");
        mongoTemplate.save(adminAuth);
        
        // Create test user
        User testUser = new User("user1", "{bcrypt}$2a$10$r2lU4bm3aWztQnCVa7T21ek.ssD4vg7PzgXhnnz/SUv38Ph3LuISK", true);
        mongoTemplate.save(testUser);
        
        Authority userAuth = new Authority("user1", "USER");
        mongoTemplate.save(userAuth);
    }

    private void createInitialOAuth2Clients(MongoTemplate mongoTemplate) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 1. messaging-client
        OAuth2RegisteredClient messagingClient = new OAuth2RegisteredClient();
        // messagingClient.setId("1");
        messagingClient.setClientId("messaging-client");
        messagingClient.setClientIdIssuedAt(LocalDateTime.of(2025, 7, 9, 11, 56, 43));
        messagingClient.setClientSecret("{bcrypt}$2a$10$R/f5T7pqFdztPcZ9B8iPxeOG1LVvbgBlhQ8zsnT6l/eUmCtFzYjda");
        messagingClient.setClientName("messaging-client");
        messagingClient.setClientAuthenticationMethods("client_secret_basic");
        messagingClient.setAuthorizationGrantTypes("authorization_code,refresh_token,client_credentials");
        messagingClient.setRedirectUris("https://local.codzs.com:8001/login/oauth2/code/messaging-client-oidc,https://local.codzs.com:8001/authorized,https://local.codzs.com:8004/login/oauth2/code/messaging-client-oidc,https://local.codzs.com:8004/authorized");
        messagingClient.setPostLogoutRedirectUris("https://local.codzs.com:8001/logged-out,https://local.codzs.com:8004/logged-out");
        messagingClient.setScopes("openid,profile,message.read,message.write,user.read");
        messagingClient.setClientSetting("{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}");
        messagingClient.setTokenSetting("{\"@class\":\"java.util.Collections$UnmodifiableMap\", \"settings.token.access-token-format\": {\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\", \"value\":\"self-contained\"}, \"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",600.000000000],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",300.000000000],\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",3600.000000000]}");
        mongoTemplate.save(messagingClient);

        // 2. device-messaging-client
        OAuth2RegisteredClient deviceClient = new OAuth2RegisteredClient();
        // deviceClient.setId("2");
        deviceClient.setClientId("device-messaging-client");
        deviceClient.setClientIdIssuedAt(LocalDateTime.of(2025, 7, 9, 11, 56, 43));
        deviceClient.setClientSecret("{bcrypt}$2a$10$R/f5T7pqFdztPcZ9B8iPxeOG1LVvbgBlhQ8zsnT6l/eUmCtFzYjda");
        deviceClient.setClientName("device-messaging-client");
        deviceClient.setClientAuthenticationMethods("client_secret_basic");
        deviceClient.setAuthorizationGrantTypes("urn:ietf:params:oauth:grant-type:device_code,refresh_token");
        // No redirect or post-logout URIs
        deviceClient.setScopes("message.read,message.write");
        deviceClient.setClientSetting("{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":true,\"settings.client.require-authorization-consent\":true,\"settings.client.device-client-certificate-validation\":false,\"settings.client.x509-certificate-subject-dn\":\"CN=device-messaging-client,O=Codzs,L=Melbourne,ST=Victoria,C=AU\",\"settings.client.x509-certificate-fingerprint\":\"sha256:1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef\"}");
        deviceClient.setTokenSetting("{\"@class\":\"java.util.Collections$UnmodifiableMap\", \"settings.token.access-token-format\": {\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\", \"value\":\"self-contained\"}, \"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",600.000000000],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",300.000000000],\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",3600.000000000],\"settings.token.device-code-time-to-live\":[\"java.time.Duration\",1800.000000000]}");
        mongoTemplate.save(deviceClient);

        // 3. token-client
        OAuth2RegisteredClient tokenClient = new OAuth2RegisteredClient();
        // tokenClient.setId("3");
        tokenClient.setClientId("token-client");
        tokenClient.setClientIdIssuedAt(LocalDateTime.of(2025, 7, 9, 11, 56, 43));
        tokenClient.setClientSecret("{bcrypt}$2a$10$9FgCvThJgqtpRD2vKeDBYu4nAydzWjm0iGpm9DXYBDyj5O7adhE4e");
        tokenClient.setClientName("token-client");
        tokenClient.setClientAuthenticationMethods("client_secret_basic");
        tokenClient.setAuthorizationGrantTypes("urn:ietf:params:oauth:grant-type:token-exchange");
        tokenClient.setScopes("message.read,message.write");
        tokenClient.setClientSetting("{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":false}");
        tokenClient.setTokenSetting("{\"@class\":\"java.util.Collections$UnmodifiableMap\", \"settings.token.access-token-format\": {\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\", \"value\":\"self-contained\"}, \"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",600.000000000],\"settings.token.access-token-time-to-live\":300.000000000,\"settings.token.refresh-token-time-to-live\":3600.000000000}");
        mongoTemplate.save(tokenClient);

        // 4. mtls-demo-client
        OAuth2RegisteredClient mtlsClient = new OAuth2RegisteredClient();
        // mtlsClient.setId("4");
        mtlsClient.setClientId("mtls-demo-client");
        mtlsClient.setClientIdIssuedAt(LocalDateTime.of(2025, 7, 9, 11, 56, 43));
        mtlsClient.setClientSecret(null);
        mtlsClient.setClientName("mtls-demo-client");
        mtlsClient.setClientAuthenticationMethods("tls_client_auth,self_signed_tls_client_auth");
        mtlsClient.setAuthorizationGrantTypes("client_credentials");
        mtlsClient.setScopes("message.read,message.write");
        mtlsClient.setClientSetting("{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":false,\"settings.client.x509-certificate-subject-dn\":\"EMAILADDRESS=khaitan.nitin@gmail.com, CN=127.0.0.1, O=Codzs, L=Melbourne, ST=Victoria, C=AU\",\"settings.client.jwk-set-url\":\"https://local.codzs.com:8001/jwks\"}");
        mtlsClient.setTokenSetting("{\"@class\":\"java.util.Collections$UnmodifiableMap\", \"settings.token.access-token-format\": {\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\", \"value\":\"self-contained\"}, \"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",600.000000000],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",300.000000000],\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",3600.000000000],\"settings.token.x509-certificate-bound-access-tokens\":true}");
        mongoTemplate.save(mtlsClient);
    }

    private void createInitialIpBlacklistEntries(MongoTemplate mongoTemplate) {
        // Insert default blacklist entries for known bad IP ranges (from SQL)
        List<Document> entries = Arrays.asList(
            new Document()
                .append("ipAddress", "0.0.0.0")
                .append("ipRange", "0.0.0.0/8")
                .append("reason", "Invalid IP range - reserved")
                .append("blockedAt", LocalDateTime.now())
                .append("blockedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", false)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", "127.0.0.1")
                .append("ipRange", null)
                .append("reason", "Example localhost block (remove in production)")
                .append("blockedAt", LocalDateTime.now())
                .append("blockedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", false)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", "10.0.0.0")
                .append("ipRange", "10.0.0.0/8")
                .append("reason", "Example private network block (configure as needed)")
                .append("blockedAt", LocalDateTime.now())
                .append("blockedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", false)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now())
        );
        mongoTemplate.getCollection("ip_blacklist").insertMany(entries);
    }

    private void createInitialApiWhitelistEntries(MongoTemplate mongoTemplate) {
        // Insert default whitelist entries (from SQL)
        List<Document> entries = Arrays.asList(
            new Document()
                .append("ipAddress", "127.0.0.1")
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/management/*")
                .append("clientId", null)
                .append("description", "Localhost access to management endpoints")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 10)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", "127.0.0.1")
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/actuator/*")
                .append("clientId", null)
                .append("description", "Localhost access to actuator endpoints")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 10)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", "::1")
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/management/*")
                .append("clientId", null)
                .append("description", "IPv6 localhost access to management endpoints")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 10)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", "::1")
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/actuator/*")
                .append("clientId", null)
                .append("description", "IPv6 localhost access to actuator endpoints")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 10)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", null)
                .append("ipRange", "192.168.0.0/16")
                .append("ipPattern", null)
                .append("endpointPattern", "/api/*")
                .append("clientId", null)
                .append("description", "Local network access to API endpoints")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 50)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            
            // Endpoint-only patterns (accessible from any IP address) - from V0_0_9__api_security_whitelist.sql
            new Document()
                .append("ipAddress", null)
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/health")
                .append("clientId", null)
                .append("description", "Health check endpoint - accessible from any IP")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 5)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", null)
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/actuator/health")
                .append("clientId", null)
                .append("description", "Actuator health endpoint - accessible from any IP")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 5)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", null)
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/public/*")
                .append("clientId", null)
                .append("description", "Public API endpoints - accessible from any IP")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 20)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", null)
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/oauth2/*")
                .append("clientId", null)
                .append("description", "OAuth2 endpoints - accessible from any IP")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 15)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", null)
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/login*")
                .append("clientId", null)
                .append("description", "Login pages - accessible from any IP")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 15)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", null)
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/logout*")
                .append("clientId", null)
                .append("description", "Logout pages - accessible from any IP")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 15)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", null)
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/")
                .append("clientId", null)
                .append("description", "Root endpoint - accessible from any IP")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 25)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", null)
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/index*")
                .append("clientId", null)
                .append("description", "Index pages - accessible from any IP")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 25)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", null)
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/home*")
                .append("clientId", null)
                .append("description", "Home pages - accessible from any IP")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 25)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", null)
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/articles*")
                .append("clientId", null)
                .append("description", "Articles pages - accessible from any IP")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 25)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", null)
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/messages*")
                .append("clientId", null)
                .append("description", "Messages pages - accessible from any IP")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 25)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", null)
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/webjars/*")
                .append("clientId", null)
                .append("description", "Static web resources - accessible from any IP")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 30)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", null)
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/css/*")
                .append("clientId", null)
                .append("description", "CSS resources - accessible from any IP")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 30)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", null)
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/js/*")
                .append("clientId", null)
                .append("description", "JavaScript resources - accessible from any IP")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 30)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", null)
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/images/*")
                .append("clientId", null)
                .append("description", "Image resources - accessible from any IP")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 30)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", null)
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/assets/*")
                .append("clientId", null)
                .append("description", "Asset resources - accessible from any IP")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 30)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", null)
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/favicon.ico")
                .append("clientId", null)
                .append("description", "Favicon - accessible from any IP")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 30)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now()),
            new Document()
                .append("ipAddress", null)
                .append("ipRange", null)
                .append("ipPattern", null)
                .append("endpointPattern", "/authorize/*")
                .append("clientId", null)
                .append("description", "Authorize endpoint")
                .append("addedAt", LocalDateTime.now())
                .append("addedBy", "SYSTEM")
                .append("expiresAt", null)
                .append("isActive", true)
                .append("priority", 31)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now())
        );
        mongoTemplate.getCollection("api_whitelist").insertMany(entries);
    }
}