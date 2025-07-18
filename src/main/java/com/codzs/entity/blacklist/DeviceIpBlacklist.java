package com.codzs.entity.blacklist;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing IP addresses blacklisted from device authorization.
 * This entity is used to prevent abuse of device authorization endpoints
 * by blocking known malicious or suspicious IP addresses.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Entity
@Table(name = "device_ip_blacklist", 
       uniqueConstraints = @UniqueConstraint(name = "uk_device_ip_blacklist_ip", columnNames = "ip_address"),
       indexes = {
           @Index(name = "idx_device_ip_blacklist_active", columnList = "is_active, expires_at"),
           @Index(name = "idx_device_ip_blacklist_created", columnList = "created_at")
       })
public class DeviceIpBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "ip_range", length = 100)
    private String ipRange;

    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    @Column(name = "blocked_at", nullable = false)
    private LocalDateTime blockedAt;

    @Column(name = "blocked_by", nullable = false, length = 100)
    private String blockedBy;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public DeviceIpBlacklist() {
        this.blockedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
        this.blockedBy = "SYSTEM";
    }

    public DeviceIpBlacklist(String ipAddress, String reason) {
        this();
        this.ipAddress = ipAddress;
        this.reason = reason;
    }

    public DeviceIpBlacklist(String ipAddress, String ipRange, String reason, String blockedBy) {
        this();
        this.ipAddress = ipAddress;
        this.ipRange = ipRange;
        this.reason = reason;
        this.blockedBy = blockedBy;
    }

    // Update timestamp before persistence
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (blockedAt == null) {
            blockedAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

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
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
        return "DeviceIpBlacklist{" +
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