/*
 * Copyright 2020-2023 Nitin Khaitan.
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
package com.codzs.web;

import java.security.Principal;

import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.codzs.service.authentication.AuthenticationService;
import com.codzs.web.consent.ConsentService;
import com.codzs.web.consent.ConsentService.ConsentData;
import com.codzs.web.consent.ScopeDescriptionService;

/**
 * Controller for handling OAuth2 authorization consent.
 * This controller has been refactored to use separate services for consent logic,
 * scope descriptions, and authentication handling.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
@Controller
public class AuthorizationConsentController {
	
	private final ConsentService consentService;
	private final ScopeDescriptionService scopeDescriptionService;
	private final AuthenticationService authenticationService;
	public AuthorizationConsentController(ConsentService consentService, 
			ScopeDescriptionService scopeDescriptionService,
			AuthenticationService authenticationService) {
		this.consentService = consentService;
		this.scopeDescriptionService = scopeDescriptionService;
		this.authenticationService = authenticationService;
	}

	/**
	 * Handle consent page display for OAuth2 authorization.
	 * This method processes the consent request and prepares the model for the consent page.
	 * 
	 * @param principal the authenticated user principal
	 * @param model the Spring MVC model for the view
	 * @param clientId the OAuth2 client ID
	 * @param scope the requested scopes as a space-delimited string
	 * @param state the OAuth2 state parameter
	 * @param userCode the user code for device flow (optional)
	 * @return the consent view name
	 */
	@GetMapping(value = "/oauth2/consent")
	public String consent(Principal principal, Model model,
			@RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
			@RequestParam(OAuth2ParameterNames.SCOPE) String scope,
			@RequestParam(OAuth2ParameterNames.STATE) String state,
			@RequestParam(name = OAuth2ParameterNames.USER_CODE, required = false) String userCode) {

		// Validate user authentication
		if (!authenticationService.isUserAuthenticated(principal)) {
			throw new IllegalStateException("User must be authenticated to access consent page");
		}

		// Note: OAuth2 parameters are already validated by Spring Authorization Server
		// before reaching this consent controller, so additional validation is not needed

		// Process consent request to determine scope approval requirements
		ConsentData consentData = consentService.processConsentRequest(principal, clientId, scope);

		// Prepare model attributes for the consent view
		model.addAttribute("clientId", clientId);
		model.addAttribute("state", state);
		model.addAttribute("scopes", scopeDescriptionService.withDescription(consentData.getScopesToApprove()));
		model.addAttribute("previouslyApprovedScopes", scopeDescriptionService.withDescription(consentData.getPreviouslyApprovedScopes()));
		model.addAttribute("principalName", authenticationService.getUserDisplayName(principal));
		model.addAttribute("userCode", userCode);
		
		// Determine request URI based on flow type
		if (StringUtils.hasText(userCode)) {
			model.addAttribute("requestURI", "/oauth2/device_verification");
		} else {
			model.addAttribute("requestURI", "/oauth2/authorize");
		}

		return "consent";
	}
}
