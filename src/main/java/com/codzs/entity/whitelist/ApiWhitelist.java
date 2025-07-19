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

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a whitelist entry for API endpoint access control.
 * Supports pattern-based matching for IP addresses, ranges, and endpoint patterns.
 *
 * @author Nitin Khaitan
 * @since 1.1
 */
@Entity
@Table(name = "api_whitelist",
       indexes = {
           @Index(name = "idx_api_whitelist_active", columnList = "is_active, expires_at"),
           @Index(name = "idx_api_whitelist_priority", columnList = "priority, is_active"),
           @Index(name = "idx_api_whitelist_endpoint", columnList = "endpoint_pattern, is_active"),
           @Index(name = "idx_api_whitelist_client", columnList = "client_id, is_active"),
           @Index(name = "idx_api_whitelist_created", columnList = "created_at")
       })
public class ApiWhitelist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ip_address", length = 45)
    @Size(max = 45, message = "IP address must not exceed 45 characters")
    private String ipAddress;

    @Column(name = "ip_range", length = 100)
    @Size(max = 100, message = "IP range must not exceed 100 characters")
    private String ipRange;

    @Column(name = "ip_pattern", length = 200)
    @Size(max = 200, message = "IP pattern must not exceed 200 characters")
    private String ipPattern;

    @Column(name = "endpoint_pattern", length = 500)
    @Size(max = 500, message = "Endpoint pattern must not exceed 500 characters")
    private String endpointPattern;

    @Column(name = "client_id", length = 100)
    @Size(max = 100, message = "Client ID must not exceed 100 characters")
    private String clientId;

    @Column(name = "description", length = 500, nullable = false)
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Column(name = "added_at", nullable = false)
    @NotNull(message = "Added at timestamp is required")
    private LocalDateTime addedAt;

    @Column(name = "added_by", length = 100, nullable = false)
    @NotBlank(message = "Added by is required")
    @Size(max = 100, message = "Added by must not exceed 100 characters")
    private String addedBy;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "is_active", nullable = false)
    @NotNull(message = "Active status is required")
    private Boolean isActive = true;

    @Column(name = "priority", nullable = false)
    @NotNull(message = "Priority is required")
    private Integer priority = 100;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (addedAt == null) {
            addedAt = LocalDateTime.now();
        }
        if (addedBy == null) {
            addedBy = "SYSTEM";
        }
        if (isActive == null) {
            isActive = true;
        }
        if (priority == null) {
            priority = 100;
        }
    }

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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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