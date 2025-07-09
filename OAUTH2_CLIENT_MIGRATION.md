# OAuth2 Client Migration to Database

## Overview

This document describes the migration of OAuth2 client registration from Java configuration to database-based registration using Flyway migrations.

## Changes Made

### 1. Password/Secret Migration from {noop} to {bcrypt}

All client secrets have been migrated from plain text format (`{noop}secret`) to bcrypt-encoded format (`{bcrypt}$2a$10$...`).

**Before:**
```java
.clientSecret("{noop}secret")
.clientSecret("{noop}token")
```

**After:**
```sql
'{bcrypt}$2a$10$R.gk27uq.4g7cc8EFK0MMeAAne0tRXMWqsv7dFkRgf3Yqni0I6wsK'
'{bcrypt}$2a$10$dJOmBt0MoRMU5gVkRy9HHOOIp9nn1WTMz8juW4tYcZ44ymsM7cJVG'
```

### 2. Client Registration Migration

**Before:** Clients were registered programmatically in `OAuth2ClientRegistrationConfig.java`

**After:** Clients are registered via Flyway migration `V0_0_7__oauth2_clients.sql`

## Migrated Clients

### 1. messaging-client
- **Client ID:** messaging-client
- **Secret:** bcrypt-encoded "secret"
- **Authentication:** CLIENT_SECRET_BASIC
- **Grant Types:** authorization_code, refresh_token, client_credentials
- **Redirect URIs:** 
  - http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc
  - http://127.0.0.1:8080/authorized
- **Scopes:** openid, profile, message.read, message.write, user.read
- **Consent Required:** true

### 2. device-messaging-client
- **Client ID:** device-messaging-client
- **Secret:** None (device flow)
- **Authentication:** NONE
- **Grant Types:** device_code, refresh_token
- **Scopes:** message.read, message.write
- **Consent Required:** false

### 3. token-client
- **Client ID:** token-client
- **Secret:** bcrypt-encoded "token"
- **Authentication:** CLIENT_SECRET_BASIC
- **Grant Types:** token-exchange
- **Scopes:** message.read, message.write
- **Consent Required:** false

### 4. mtls-demo-client
- **Client ID:** mtls-demo-client
- **Secret:** None (mTLS)
- **Authentication:** TLS_CLIENT_AUTH, SELF_SIGNED_TLS_CLIENT_AUTH
- **Grant Types:** client_credentials
- **Scopes:** message.read, message.write
- **Consent Required:** false
- **Special Settings:** x509 certificate binding enabled

## Configuration Changes

### 1. Flyway Configuration
Enabled Flyway in `application.yml` with support for existing databases:
```yaml
flyway:
  baseline-on-migrate: true
  baseline-version: 0
  enabled: true
  locations: classpath:db/migration
  validate-on-migrate: false
  clean-disabled: false
  baseline-description: "Baseline existing database"
  out-of-order: true
```

### 2. OAuth2ClientRegistrationConfig.java
Simplified to only provide the `JdbcRegisteredClientRepository` bean:
```java
@Bean
public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
    return new JdbcRegisteredClientRepository(jdbcTemplate);
}
```

## Migration Files

### Schema Migrations
- `V0_0_1__oauth2-registered-client-schema.sql` - OAuth2 client table schema
- `V0_0_2__oauth2-authorization-consent-schema.sql` - Authorization consent table
- `V0_0_3__oauth2-authorization-schema.sql` - Authorization table
- `V0_0_4__users.sql` - Users table
- `V0_0_5__createAclSchemaMySQL.sql` - ACL schema

### Data Migrations
- `V0_0_6__oauth2_sample_data.sql` - Sample data (articles-client, users)
- `V0_0_7__oauth2_clients.sql` - OAuth2 clients from Java configuration

## Benefits

1. **Security:** Client secrets are now bcrypt-encoded instead of plain text
2. **Version Control:** Client configurations are version-controlled via Flyway migrations
3. **Environment Management:** Easy to manage different client configurations across environments
4. **Database Consistency:** All client data is stored in the database
5. **Maintainability:** No need to redeploy application for client changes
6. **Existing Database Support:** Migrations use `CREATE TABLE IF NOT EXISTS` and `INSERT IGNORE` to handle existing databases

## Testing

To test the migration:

1. Start the application: `./mvnw spring-boot:run`
2. Verify Flyway migrations run successfully
3. Test OAuth2 flows with the migrated clients:
   - messaging-client: `https://localhost:5003/oauth2/authorize?response_type=code&client_id=messaging-client&scope=openid&redirect_uri=http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc`
   - device-messaging-client: Device authorization flow
   - token-client: Token exchange flow
   - mtls-demo-client: mTLS authentication

## Notes

- The bcrypt hashes were generated using Spring Security's `BCryptPasswordEncoder`
- All client configurations maintain the same functionality as before
- The migration is backward compatible
- Client secrets can be updated in the database without application redeployment
- **Database Compatibility:** All migrations use `CREATE TABLE IF NOT EXISTS` and `INSERT IGNORE` to work with existing databases
- **Config Server Integration:** Database configuration is pulled from the config server at `https://local.codzs.com:5002` 