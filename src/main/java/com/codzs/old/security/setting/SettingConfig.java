// package com.codzs.security.setting;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

// @Configuration
// public class SettingConfig {
//     @Value("${authorization.server.url}")
//     private String authorizationServerUrl;

//     @Bean
//     public AuthorizationServerSettings authorizationServerSettings() {
//         return AuthorizationServerSettings.builder()
//                 .issuer(authorizationServerUrl)
//                 .build();
//     }
// }
