-- Migration to insert OAuth2 clients from Java configuration to database
-- This replaces the client registration in OAuth2ClientRegistrationConfig.java

-- Messaging Client (messaging-client)
-- Final redirect URIs support both Group 1 (port 8001) and Group 2 (port 8004)
INSERT IGNORE INTO `oauth2_registered_client` VALUES (
    '1',
    'messaging-client', 
    '2025-07-09 11:56:43',
    '{bcrypt}$2a$10$R/f5T7pqFdztPcZ9B8iPxeOG1LVvbgBlhQ8zsnT6l/eUmCtFzYjda',
    NULL,
    'messaging-client',
    'client_secret_basic',
    'authorization_code,refresh_token,client_credentials',
    'https://local.codzs.com:8001/login/oauth2/code/messaging-client-oidc,https://local.codzs.com:8001/authorized,https://local.codzs.com:8004/login/oauth2/code/messaging-client-oidc,https://local.codzs.com:8004/authorized',
    'https://local.codzs.com:8001/logged-out,https://local.codzs.com:8004/logged-out',
    'openid,profile,message.read,message.write,user.read',
    '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}',
    '{\"@class\":\"java.util.Collections$UnmodifiableMap\", \"settings.token.access-token-format\": {\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\", \"value\":\"self-contained\"}, \"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",3600.000000000],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",300.000000000],\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",3600.000000000]}'
);

-- Device Client (device-messaging-client)
INSERT IGNORE INTO `oauth2_registered_client` VALUES (
    '2',
    'device-messaging-client',
    '2025-07-09 11:56:43',
    NULL,
    NULL,
    'device-messaging-client',
    'none',
    'urn:ietf:params:oauth:grant-type:device_code,refresh_token',
    NULL,
    NULL,
    'message.read,message.write',
    '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":false}',
    '{\"@class\":\"java.util.Collections$UnmodifiableMap\", \"settings.token.access-token-format\": {\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\", \"value\":\"self-contained\"}, \"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",3600.000000000],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",300.000000000],\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",3600.000000000],\"settings.token.device-code-time-to-live\":[\"java.time.Duration\",1800.000000000]}'
);

-- Token Exchange Client (token-client)
INSERT IGNORE INTO `oauth2_registered_client` VALUES (
    '3',
    'token-client',
    '2025-07-09 11:56:43',
    '{bcrypt}$2a$10$9FgCvThJgqtpRD2vKeDBYu4nAydzWjm0iGpm9DXYBDyj5O7adhE4e',
    NULL,
    'token-client',
    'client_secret_basic',
    'urn:ietf:params:oauth:grant-type:token-exchange',
    NULL,
    NULL,
    'message.read,message.write',
    '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":false}',
    '{\"@class\":\"java.util.Collections$UnmodifiableMap\", \"settings.token.access-token-format\": {\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\", \"value\":\"self-contained\"}, \"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",3600.000000000],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",300.000000000],\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",3600.000000000]}'
);

-- mTLS Demo Client (mtls-demo-client)
-- Final configuration with correct certificate DN and enabled certificate bound tokens
INSERT IGNORE INTO `oauth2_registered_client` VALUES (
    '4',
    'mtls-demo-client',
    '2025-07-09 11:56:43',
    NULL,
    NULL,
    'mtls-demo-client',
    'tls_client_auth,self_signed_tls_client_auth',
    'client_credentials',
    NULL,
    NULL,
    'message.read,message.write',
    '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":false,\"settings.client.x509-certificate-subject-dn\":\"EMAILADDRESS=khaitan.nitin@gmail.com, CN=127.0.0.1, O=Codzs, L=Melbourne, ST=Victoria, C=AU\",\"settings.client.jwk-set-url\":\"https://local.codzs.com:8001/jwks\"}',
    '{\"@class\":\"java.util.Collections$UnmodifiableMap\", \"settings.token.access-token-format\": {\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\", \"value\":\"self-contained\"}, \"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",3600.000000000],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",300.000000000],\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",3600.000000000],\"settings.token.x509-certificate-bound-access-tokens\":true}'
); 