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
package com.codzs.constant.organization;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.codzs.framework.base.ConfigParameterBase;

/**
 * Configuration enum for ORGANIZATION_SIZE parameters from config server.
 * This class maps the configuration from the param table in the config database.
 * 
 * Available options include: 1-10, 11-200, 201-500, 500+
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Configuration(proxyBeanMethods = false)
@Order(-1)
public class OrganizationSizeEnum extends ConfigParameterBase {

    @Value("${organization.size.options:}")
    private String optionsString;
    
    @Value("${organization.size.description:}")
    private String description;
    
    @Value("${organization.size.default:}")
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