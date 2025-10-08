package com.codzs.entity.blacklist;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import java.time.LocalDateTime;

/**
 * MongoDB Document representing IP addresses blacklisted from API access.
 * This entity is used to prevent abuse of API endpoints
 * by blocking known malicious or suspicious IP addresses.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Document(collection = "ip_blacklist")
@CompoundIndexes({
    @CompoundIndex(name = "idx_ip_blacklist_active", def = "{'isActive': 1, 'expiresAt': 1}"),
    @CompoundIndex(name = "idx_ip_blacklist_created", def = "{'createdAt': 1}")
})
public class IpBlacklist {

    @Id
    private String id; // MongoDB ObjectId

    @Indexed(unique = true)
    private String ipAddress;

    private String ipRange;

    private String reason;

    private LocalDateTime blockedAt;

    private String blockedBy;

    private LocalDateTime expiresAt;

    private Boolean isActive;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Constructors
    public IpBlacklist() {
        this.blockedAt = LocalDateTime.now();
        this.isActive = true;
        this.blockedBy = "SYSTEM";
        // Note: createdAt and updatedAt are now managed by MongoDB auditing
    }

    public IpBlacklist(String ipAddress, String reason) {
        this();
        this.ipAddress = ipAddress;
        this.reason = reason;
    }

    public IpBlacklist(String ipAddress, String ipRange, String reason, String blockedBy) {
        this();
        this.ipAddress = ipAddress;
        this.ipRange = ipRange;
        this.reason = reason;
        this.blockedBy = blockedBy;
    }

    // Note: MongoDB auditing (@CreatedDate, @LastModifiedDate) handles timestamps automatically
    // No @PrePersist or @PreUpdate needed

    /**
     * Checks if this blacklist entry is currently effective.
     * An entry is effective if it's active and hasn't expired.
     */
    public boolean isEffective() {
        if (!Boolean.TRUE.equals(isActive)) {
            return false;
        }
        if (expiresAt != null && LocalDateTime.now().isAfter(expiresAt)) {
            return false;
        }
        return true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getIpRange() { return ipRange; }
    public void setIpRange(String ipRange) { this.ipRange = ipRange; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getBlockedAt() { return blockedAt; }
    public void setBlockedAt(LocalDateTime blockedAt) { this.blockedAt = blockedAt; }

    public String getBlockedBy() { return blockedBy; }
    public void setBlockedBy(String blockedBy) { this.blockedBy = blockedBy; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "IpBlacklist{" +
                "id=" + id +
                ", ipAddress='" + ipAddress + '\'' +
                ", ipRange='" + ipRange + '\'' +
                ", reason='" + reason + '\'' +
                ", blockedAt=" + blockedAt +
                ", blockedBy='" + blockedBy + '\'' +
                ", expiresAt=" + expiresAt +
                ", isActive=" + isActive +
                '}';
    }
}