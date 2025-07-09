/*
 * Copyright 2020-2024 the original author or authors.
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
package com.codzs.config.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import com.codzs.oauth2.authentication.federation.FederatedIdentityIdTokenCustomizer;
import com.codzs.jose.Jwks;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

/**
 * Configuration class for OAuth2 token customization and JWT settings.
 * This class handles the configuration of JWT token customization and
 * OAuth2 token generation settings.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
@Configuration(proxyBeanMethods = false)
public class OAuth2TokenConfig {

	/**
	 * Configure the OAuth2 token customizer for ID tokens.
	 * This customizer handles federated identity information in ID tokens.
	 * 
	 * @return OAuth2TokenCustomizer for JWT encoding context
	 */
	@Bean
	public OAuth2TokenCustomizer<JwtEncodingContext> idTokenCustomizer() {
		return new FederatedIdentityIdTokenCustomizer();
	}

	/**
	 * Configure the JWK source for JWT token signing.
	 * This provides the cryptographic keys used to sign JWT tokens.
	 * 
	 * @return JWKSource containing the RSA key for token signing
	 */
	@Bean
	public JWKSource<SecurityContext> jwkSource() {
		RSAKey rsaKey = Jwks.generateRsa();
		JWKSet jwkSet = new JWKSet(rsaKey);
		return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
	}

	/**
	 * Configure the JWT decoder for token validation.
	 * This decoder is used to validate and decode JWT tokens issued by the authorization server.
	 * 
	 * @param jwkSource the JWK source containing the signing keys
	 * @return JwtDecoder for token validation
	 */
	@Bean
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}
} 