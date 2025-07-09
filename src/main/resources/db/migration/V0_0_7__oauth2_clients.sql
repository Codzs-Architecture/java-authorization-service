-- Migration to insert OAuth2 clients from Java configuration to database
-- This replaces the client registration in OAuth2ClientRegistrationConfig.java

-- Messaging Client (messaging-client)
INSERT IGNORE INTO `oauth2_registered_client` VALUES (
    '2',
    'messaging-client',
    '2025-07-09 11:56:43',
    '{bcrypt}$2a$10$R.gk27uq.4g7cc8EFK0MMeAAne0tRXMWqsv7dFkRgf3Yqni0I6wsK',
    NULL,
    'messaging-client',
    'client_secret_basic',
    'refresh_token,client_credentials,authorization_code',
    'http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc,http://127.0.0.1:8080/authorized',
    'http://127.0.0.1:8080/logged-out',
    'openid,profile,message.read,message.write,user.read',
    '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}',
    '{\"@class\":\"java.util.Collections$UnmodifiableMap\", \"settings.token.access-token-format\": {\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\", \"value\":\"self-contained\"}, \"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",3600.000000000],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",300.000000000],\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",3600.000000000]}'
);

-- Device Client (device-messaging-client)
INSERT IGNORE INTO `oauth2_registered_client` VALUES (
    '3',
    'device-messaging-client',
    '2025-07-09 11:56:43',
    NULL,
    NULL,
    'device-messaging-client',
    'none',
    'refresh_token,urn:ietf:params:oauth:grant-type:device_code',
    NULL,
    NULL,
    'message.read,message.write',
    '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":false}',
    '{\"@class\":\"java.util.Collections$UnmodifiableMap\", \"settings.token.access-token-format\": {\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\", \"value\":\"self-contained\"}, \"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",3600.000000000],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",300.000000000],\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",3600.000000000]}'
);

-- Token Exchange Client (token-client)
INSERT IGNORE INTO `oauth2_registered_client` VALUES (
    '4',
    'token-client',
    '2025-07-09 11:56:43',
    '{bcrypt}$2a$10$dJOmBt0MoRMU5gVkRy9HHOOIp9nn1WTMz8juW4tYcZ44ymsM7cJVG',
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
INSERT IGNORE INTO `oauth2_registered_client` VALUES (
    '5',
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
    '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":false,\"settings.client.x509-certificate-subject-dn\":\"CN=demo-client-sample,OU=Spring Samples,O=Spring,C=US\",\"settings.client.jwk-set-url\":\"http://127.0.0.1:8080/jwks\"}',
    '{\"@class\":\"java.util.Collections$UnmodifiableMap\", \"settings.token.access-token-format\": {\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\", \"value\":\"self-contained\"}, \"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",3600.000000000],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",300.000000000],\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",3600.000000000],\"settings.token.x509-certificate-bound-access-tokens\":true}'
); 