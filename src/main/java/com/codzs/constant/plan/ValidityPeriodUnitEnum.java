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
package com.codzs.constant.plan;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.codzs.framework.base.ConfigParameterBase;

/**
 * Configuration enum for VALIDITY_PERIOD_UNIT parameters from config server.
 * This class maps the configuration from the param table in the config database.
 * 
 * Available options include: HOUR, DAY, MONTH, YEAR
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Configuration(proxyBeanMethods = false)
@Order(-1)
public class ValidityPeriodUnitEnum extends ConfigParameterBase {

    @Value("${validity.period.unit.options:}")
    private String optionsString;
    
    @Value("${validity.period.unit.description:}")
    private String description;

    @Override
    protected String getOptionsString() {
        return optionsString;
    }

    @Override
    protected String getDescription() {
        return description;
    }
}