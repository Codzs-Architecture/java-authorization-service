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
package com.codzs.web.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.core.oidc.OidcScopes;

/**
 * Model class representing an OAuth2 scope with its human-readable description.
 * This class is used to display scopes and their descriptions in the consent UI.
 * 
 * @author Extracted from AuthorizationConsentController
 * @since 1.1
 */
public class ScopeWithDescription {
	
	private static final String DEFAULT_DESCRIPTION = "UNKNOWN SCOPE - We cannot provide information about this permission, use caution when granting this.";
	
	private static final Map<String, String> SCOPE_DESCRIPTIONS = new HashMap<>();
	
	static {
		SCOPE_DESCRIPTIONS.put(
				OidcScopes.PROFILE,
				"This application will be able to read your profile information."
		);
		SCOPE_DESCRIPTIONS.put(
				"message.read",
				"This application will be able to read your message."
		);
		SCOPE_DESCRIPTIONS.put(
				"message.write",
				"This application will be able to add new messages. It will also be able to edit and delete existing messages."
		);
		SCOPE_DESCRIPTIONS.put(
				"user.read",
				"This application will be able to read your user information."
		);
		SCOPE_DESCRIPTIONS.put(
				"other.scope",
				"This is another scope example of a scope description."
		);
	}

	public final String scope;
	public final String description;

	public ScopeWithDescription(String scope) {
		this.scope = scope;
		this.description = SCOPE_DESCRIPTIONS.getOrDefault(scope, DEFAULT_DESCRIPTION);
	}

	public String getScope() {
		return scope;
	}

	public String getDescription() {
		return description;
	}
} 