// package com.codzs.security;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.annotation.Order;
// import org.springframework.http.MediaType;
// import org.springframework.security.config.Customizer;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// // PasswordEncoder is provided by auth-user-detail-service dependency
// import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
// import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
// import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

// import static org.springframework.security.config.Customizer.withDefaults;

// @Configuration
// @EnableWebSecurity
// public class WebSecurityConfig {
//     @Bean
//     @Order(1)
//     public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
//         OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
//         http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
//                 .oidc(withDefaults());

//         http
//                 .exceptionHandling(exception -> exception
//                         .defaultAuthenticationEntryPointFor(
//                                 new LoginUrlAuthenticationEntryPoint("/login"),
//                                 new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
//                         )
//                 )
//                 .oauth2ResourceServer(resourceServer -> resourceServer
//                         .jwt(Customizer.withDefaults())
//                 );

//         return http.build();
//     }

//     // Add security filter chain for login pages only - adminServiceFilterChain handles other requests
//     @Bean
//     @Order(2)
//     public SecurityFilterChain loginSecurityFilterChain(HttpSecurity http) throws Exception {
//         http
//                 .securityMatcher("/login**", "/error", "/logout")
//                 .authorizeHttpRequests(authorize -> authorize
//                         .requestMatchers("/login**", "/error", "/logout").permitAll()
//                 )
//                 .formLogin(withDefaults())
//                 .httpBasic(withDefaults());

//         return http.build();
//     }
// }
