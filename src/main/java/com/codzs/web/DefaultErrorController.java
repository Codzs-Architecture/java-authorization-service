/*
 * Copyright 2020-2023 the original author or authors.
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

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.codzs.service.error.ErrorService;

/**
 * Default error controller for handling application errors.
 * This controller provides centralized error handling and custom error pages
 * for the OAuth2 authorization service.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
@Controller
public class DefaultErrorController implements ErrorController {

	private final ErrorService errorService;

	public DefaultErrorController(ErrorService errorService) {
		this.errorService = errorService;
	}

	/**
	 * Handle error requests.
	 * This endpoint processes error information and prepares the error model
	 * for display to the user.
	 * 
	 * @param model the Spring MVC model for the error view
	 * @param request the HTTP request containing error information
	 * @return the error view name
	 */
	@RequestMapping("/error")
	public String handleError(Model model, HttpServletRequest request) {
		ErrorService.ErrorModel errorModel = errorService.processError(request);
		
		model.addAttribute("errorTitle", errorModel.getErrorTitle());
		model.addAttribute("errorMessage", errorModel.getErrorMessage());
		
		return "error";
	}
}
