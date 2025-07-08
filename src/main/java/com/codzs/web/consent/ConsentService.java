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
package com.codzs.web.consent;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Service class for handling OAuth2 consent business logic.
 * This service manages the consent flow including scope analysis and consent determination.
 * 
 * @author Extracted from AuthorizationConsentController
 * @since 1.1
 */
@Service
public class ConsentService {

	private final RegisteredClientRepository registeredClientRepository;
	private final OAuth2AuthorizationConsentService authorizationConsentService;

	public ConsentService(RegisteredClientRepository registeredClientRepository,
			OAuth2AuthorizationConsentService authorizationConsentService) {
		this.registeredClientRepository = registeredClientRepository;
		this.authorizationConsentService = authorizationConsentService;
	}

	/**
	 * Process consent request and determine which scopes need approval.
	 * This method analyzes the requested scopes against previously approved scopes
	 * and separates them into scopes that need approval and previously approved scopes.
	 * 
	 * @param principal the authenticated user principal
	 * @param clientId the OAuth2 client ID
	 * @param scope the requested scopes as a space-delimited string
	 * @return ConsentData containing scopes to approve and previously approved scopes
	 */
	public ConsentData processConsentRequest(Principal principal, String clientId, String scope) {
		// Remove scopes that were already approved
		Set<String> scopesToApprove = new HashSet<>();
		Set<String> previouslyApprovedScopes = new HashSet<>();
		
		RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
		OAuth2AuthorizationConsent currentAuthorizationConsent =
				this.authorizationConsentService.findById(registeredClient.getId(), principal.getName());
		
		Set<String> authorizedScopes;
		if (currentAuthorizationConsent != null) {
			authorizedScopes = currentAuthorizationConsent.getScopes();
		} else {
			authorizedScopes = Collections.emptySet();
		}
		
		for (String requestedScope : StringUtils.delimitedListToStringArray(scope, " ")) {
			if (OidcScopes.OPENID.equals(requestedScope)) {
				continue; // Skip openid scope as it doesn't require explicit consent
			}
			if (authorizedScopes.contains(requestedScope)) {
				previouslyApprovedScopes.add(requestedScope);
			} else {
				scopesToApprove.add(requestedScope);
			}
		}

		return new ConsentData(scopesToApprove, previouslyApprovedScopes);
	}

	/**
	 * Data class to hold consent processing results.
	 * Contains scopes that need approval and previously approved scopes.
	 */
	public static class ConsentData {
		private final Set<String> scopesToApprove;
		private final Set<String> previouslyApprovedScopes;

		public ConsentData(Set<String> scopesToApprove, Set<String> previouslyApprovedScopes) {
			this.scopesToApprove = scopesToApprove;
			this.previouslyApprovedScopes = previouslyApprovedScopes;
		}

		public Set<String> getScopesToApprove() {
			return scopesToApprove;
		}

		public Set<String> getPreviouslyApprovedScopes() {
			return previouslyApprovedScopes;
		}
	}
} 