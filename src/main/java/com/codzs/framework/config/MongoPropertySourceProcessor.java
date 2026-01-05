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
package com.codzs.framework.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Environment post-processor to flatten MongoDB property sources.
 * This processor takes complex JSON objects from MongoDB property sources
 * and flattens them into simple key-value pairs that Spring can use.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
public class MongoPropertySourceProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Look for MongoDB property sources
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            if (propertySource.getName().contains("mongodb-param")) {
                processMongoPropertySource(environment, propertySource);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processMongoPropertySource(ConfigurableEnvironment environment, PropertySource<?> propertySource) {
        Map<String, Object> flattenedProperties = new HashMap<>();
        
        if (propertySource.getSource() instanceof Map) {
            Map<String, Object> sourceMap = (Map<String, Object>) propertySource.getSource();
            
            for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                
                // Flatten complex objects
                if (value instanceof Map) {
                    Map<String, Object> nestedMap = (Map<String, Object>) value;
                    flattenMap(key, nestedMap, flattenedProperties);
                } else {
                    flattenedProperties.put(key, value);
                }
            }
            
            // Add flattened properties as a new property source
            environment.getPropertySources().addAfter(
                propertySource.getName(),
                new MapPropertySource(propertySource.getName() + "-flattened", flattenedProperties)
            );
        }
    }

    @SuppressWarnings("unchecked")
    private void flattenMap(String prefix, Map<String, Object> map, Map<String, Object> result) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix + "." + entry.getKey();
            Object value = entry.getValue();
            
            if (value instanceof Map) {
                flattenMap(key, (Map<String, Object>) value, result);
            } else if (value instanceof List) {
                List<?> list = (List<?>) value;
                // Convert list to comma-separated string
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) sb.append(",");
                    sb.append(list.get(i).toString());
                }
                result.put(key, sb.toString());
            } else {
                result.put(key, value);
            }
        }
    }
}