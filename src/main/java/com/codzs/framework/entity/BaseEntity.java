package com.codzs.framework.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;

/**
 * Base entity class containing common audit fields for all MongoDB entities.
 * This class provides standard audit trail functionality including creation,
 * modification, and soft deletion tracking.
 * 
 * Audit fields are automatically populated by Spring Data MongoDB auditing:
 * - createdDate: Set automatically when entity is first saved
 * - createdBy: Set automatically with current user when entity is first saved
 * - lastModifiedDate: Updated automatically whenever entity is saved
 * - lastModifiedBy: Updated automatically with current user whenever entity is saved
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class BaseEntity {

    @CreatedDate
    @NotNull(message = "Creation timestamp is required")
    @Indexed
    protected Instant createdDate;

    @CreatedBy
    @NotBlank(message = "Created by user ID is required")
    @Indexed
    protected String createdBy;

    @LastModifiedDate
    @NotNull(message = "Update timestamp is required")
    @Indexed
    protected Instant lastModifiedDate;

    @LastModifiedBy
    @NotBlank(message = "Updated by user ID is required")
    @Indexed
    protected String lastModifiedBy;

    @Indexed
    protected Instant deletedDate;

    @Indexed
    protected String deletedBy;

    protected String correlationId;

    /**
     * Constructor with correlation ID for tracking related operations.
     * 
     * @param correlationId ID to correlate related operations
     */
    public BaseEntity(String correlationId) {
        this();
        this.correlationId = correlationId;
    }

    /**
     * Performs a soft delete by setting deletedDate and deletedBy fields.
     * The lastModifiedDate and lastModifiedBy fields will be automatically updated by auditing.
     * 
     * @param deletedBy the user ID who is deleting the entity
     */
    public void softDelete(String deletedBy) {
        this.deletedDate = Instant.now();
        this.deletedBy = deletedBy;
        // lastModifiedDate and lastModifiedBy will be automatically set by Spring Data auditing
    }

    /**
     * Checks if the entity is soft deleted
     * 
     * @return true if the entity is deleted, false otherwise
     */
    public boolean isDeleted() {
        return deletedDate != null;
    }

    /**
     * Restores a soft deleted entity by clearing deletion fields.
     * The lastModifiedDate and lastModifiedBy fields will be automatically updated by auditing.
     * 
     * @param restoredBy the user ID who is restoring the entity (optional, can be null)
     */
    public void restore(String restoredBy) {
        this.deletedDate = null;
        this.deletedBy = null;
        // lastModifiedDate and lastModifiedBy will be automatically set by Spring Data auditing
    }
}