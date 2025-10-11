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

/**
 * Configuration class for default organization ID from config server.
 * This is a simple string value configuration.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Configuration(proxyBeanMethods = false)
@Order(-1)
public class DefaultOrganizationConfig {

    @Value("${default.organization.id:}")
    private String defaultOrganizationId;

    public String getDefaultOrganizationId() {
        return defaultOrganizationId;
    }

    public boolean hasDefaultOrganizationId() {
        return defaultOrganizationId != null && !defaultOrganizationId.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "DefaultOrganizationConfig{" +
                "defaultOrganizationId='" + defaultOrganizationId + '\'' +
                '}';
    }
}