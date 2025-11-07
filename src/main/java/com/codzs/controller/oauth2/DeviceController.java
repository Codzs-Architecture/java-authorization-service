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
package com.codzs.controller.oauth2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.util.StringUtils;

import com.codzs.service.device.DeviceService;
import com.codzs.service.oauth2.ValidationService;

/**
 * Controller for handling OAuth2 device authorization flow.
 * This controller manages device flow operations including device activation,
 * user code verification, and device flow completion.
 * 
 * @author Nitin Khaitan
 * @since 1.1
 */
@Controller
public class DeviceController {

	private final DeviceService deviceService;
	private final ValidationService validationService;

	public DeviceController(DeviceService deviceService, ValidationService validationService) {
		this.deviceService = deviceService;
		this.validationService = validationService;
	}

	/**
	 * Handle device activation requests.
	 * This endpoint processes device activation with optional user code
	 * and handles appropriate redirection or view display.
	 * 
	 * @param userCode the user code from the device activation request (optional)
	 * @return the appropriate view name or redirect instruction
	 */
	@GetMapping("/activate")
	public String activate(@RequestParam(value = "user_code", required = false) String userCode) {
		// Validate user code if provided
		if (StringUtils.hasText(userCode)) {
			validationService.validateUserCode(userCode);
		}
		
		DeviceService.DeviceActivationResult result = deviceService.processDeviceActivation(userCode);
		
		if (result.isRedirect()) {
			return "redirect:" + result.getDestination();
		} else {
			return result.getDestination();
		}
	}

	/**
	 * Handle device activation success.
	 * This endpoint displays the success page after device activation.
	 * 
	 * @return the device activation success view name
	 */
	@GetMapping("/activated")
	public String activated() {
		return deviceService.processDeviceActivationSuccess();
	}

	/**
	 * Handle device success callback.
	 * This endpoint handles success callback scenarios for device flow.
	 * 
	 * @return the device success view name
	 */
	@GetMapping(value = "/", params = "success")
	public String success() {
		return deviceService.processDeviceSuccessCallback();
	}
}
