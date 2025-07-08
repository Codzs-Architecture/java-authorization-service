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

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.codzs.web.model.ScopeWithDescription;

/**
 * Service class for handling OAuth2 scope descriptions.
 * This service provides functionality to convert scopes to their human-readable descriptions.
 * 
 * @author Extracted from AuthorizationConsentController
 * @since 1.1
 */
@Service
public class ScopeDescriptionService {

	/**
	 * Convert a set of scope strings to a set of ScopeWithDescription objects.
	 * Each scope is enriched with its human-readable description.
	 * 
	 * @param scopes the set of scope strings to convert
	 * @return Set of ScopeWithDescription objects with descriptions
	 */
	public Set<ScopeWithDescription> withDescription(Set<String> scopes) {
		Set<ScopeWithDescription> scopeWithDescriptions = new HashSet<>();
		for (String scope : scopes) {
			scopeWithDescriptions.add(new ScopeWithDescription(scope));
		}
		return scopeWithDescriptions;
	}
} 