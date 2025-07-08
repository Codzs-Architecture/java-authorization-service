INSERT INTO `oauth2_registered_client` VALUES (
                                               '1',
                                               'articles-client',
                                               '2023-07-05 09:41:41',
                                               '{bcrypt}$2a$10$iiU/FPsG3wzuau.mEdwFieEkwiOO7oHFXUzG8J8SfVLIX8DHWg1V6',
                                               NULL,
                                               'articles-client',
                                               'client_secret_basic',
                                               'refresh_token,client_credentials,authorization_code',
                                               'https://local.codzs.com:8001/authorized,https://local.codzs.com:8001/login/oauth2/code/articles-client-oidc,https://local.codzs.com:8001/login/oauth2/code/articles-client-authorization-code',
                                               'https://local.codzs.com:8001/',
                                               'articles.read,openid',
                                               '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":false}',
                                               '{\"@class\":\"java.util.Collections$UnmodifiableMap\", \"settings.token.access-token-format\": {\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\", \"value\":\"self-contained\"}, \"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",3600.000000000],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",300.000000000],\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",3600.000000000]}'
                                            );

INSERT INTO `oauth2_authorization_consent` VALUES ('1','admin','ROLE_ADMIN');

INSERT INTO `users` VALUES ('admin','{bcrypt}$2a$10$63CNAw69rPATq7hoyGbQFe4GojJ.xK9bNWs1rH88U0GPiHyK2ueIC',1);

INSERT INTO `authorities` VALUES ('admin','ADMIN');
