/*
 * Copyright 2020-2025 Nitin Khaitan.
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
package com.codzs.constant.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.codzs.framework.constant.ConfigParameterBase;

/**
 * Configuration enum for DOMAIN_VERIFICATION_METHOD parameters from config server.
 * This class maps the configuration from the param table in the config database.
 * 
 * Available options include: DNS, EMAIL, FILE
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Configuration(proxyBeanMethods = false)
@Order(-1)
public class DomainVerificationMethodEnum extends ConfigParameterBase {

    @Value("${domain.verification.method.options:}")
    private String optionsString;
    
    @Value("${domain.verification.method.description:}")
    private String description;
    
    @Value("${domain.verification.method.default:}")
    private String defaultValue;

    @Override
    protected String getOptionsString() {
        return optionsString;
    }

    @Override
    protected String getDescription() {
        return description;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }
}