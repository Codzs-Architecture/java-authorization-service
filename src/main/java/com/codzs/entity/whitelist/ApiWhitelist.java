/*
 * Copyright 2020-2024 Nitin Khaitan.
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
package com.codzs.entity.whitelist;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * MongoDB Document representing a whitelist entry for API endpoint access control.
 * Supports pattern-based matching for IP addresses, ranges, and endpoint patterns.
 *
 * @author Nitin Khaitan
 * @since 1.1
 */
@Document(collection = "api_whitelist")
@CompoundIndexes({
    @CompoundIndex(name = "idx_api_whitelist_active", def = "{'isActive': 1, 'expiresAt': 1}"),
    @CompoundIndex(name = "idx_api_whitelist_priority", def = "{'priority': 1, 'isActive': 1}"),
    @CompoundIndex(name = "idx_api_whitelist_endpoint", def = "{'endpointPattern': 1, 'isActive': 1}"),
    @CompoundIndex(name = "idx_api_whitelist_client", def = "{'clientId': 1, 'isActive': 1}"),
    @CompoundIndex(name = "idx_api_whitelist_created", def = "{'createdAt': 1}")
})
public class ApiWhitelist {

    @Id
    private String id; // MongoDB ObjectId

    @Size(max = 45, message = "IP address must not exceed 45 characters")
    private String ipAddress;

    @Size(max = 100, message = "IP range must not exceed 100 characters")
    private String ipRange;

    @Size(max = 200, message = "IP pattern must not exceed 200 characters")
    private String ipPattern;

    @Size(max = 500, message = "Endpoint pattern must not exceed 500 characters")
    private String endpointPattern;

    @Size(max = 100, message = "Client ID must not exceed 100 characters")
    private String clientId;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Added at timestamp is required")
    private LocalDateTime addedAt;

    @NotBlank(message = "Added by is required")
    @Size(max = 100, message = "Added by must not exceed 100 characters")
    private String addedBy;

    private LocalDateTime expiresAt;

    @NotNull(message = "Active status is required")
    private Boolean isActive = true;

    @NotNull(message = "Priority is required")
    private Integer priority = 100;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Note: MongoDB auditing (@CreatedDate, @LastModifiedDate) handles timestamps automatically
    // Default values are set in field declarations

    /**
     * Checks if this whitelist entry is currently active and not expired.
     *
     * @return true if the entry is active and not expired
     */
    public boolean isCurrentlyActive() {
        if (!Boolean.TRUE.equals(isActive)) {
            return false;
        }
        if (expiresAt != null && LocalDateTime.now().isAfter(expiresAt)) {
            return false;
        }
        return true;
    }

    /**
     * Checks if this whitelist entry has any IP matching criteria defined.
     *
     * @return true if at least one IP matching criterion is defined
     */
    public boolean hasIpCriteria() {
        return (ipAddress != null && !ipAddress.trim().isEmpty()) ||
               (ipRange != null && !ipRange.trim().isEmpty()) ||
               (ipPattern != null && !ipPattern.trim().isEmpty());
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpRange() {
        return ipRange;
    }

    public void setIpRange(String ipRange) {
        this.ipRange = ipRange;
    }

    public String getIpPattern() {
        return ipPattern;
    }

    public void setIpPattern(String ipPattern) {
        this.ipPattern = ipPattern;
    }

    public String getEndpointPattern() {
        return endpointPattern;
    }

    public void setEndpointPattern(String endpointPattern) {
        this.endpointPattern = endpointPattern;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiWhitelist that = (ApiWhitelist) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ApiWhitelist{" +
                "id=" + id +
                ", ipAddress='" + ipAddress + '\'' +
                ", ipRange='" + ipRange + '\'' +
                ", ipPattern='" + ipPattern + '\'' +
                ", endpointPattern='" + endpointPattern + '\'' +
                ", clientId='" + clientId + '\'' +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                ", priority=" + priority +
                '}';
    }
}