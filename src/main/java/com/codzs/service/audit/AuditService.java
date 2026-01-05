package com.codzs.service.audit;

import com.codzs.framework.aware.audit.AuditorAwareImpl;
import com.codzs.framework.entity.BaseEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Service for handling manual audit operations when automatic auditing
 * is not sufficient or when special audit requirements exist.
 * 
 * This service provides utilities for:
 * - Manual audit field population for bulk operations
 * - Audit field management for entities not using automatic auditing
 * - Special audit scenarios like system migrations or batch processes
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditorAwareImpl auditorAware;

    /**
     * Manually sets creation audit fields on an entity.
     * Use this method only in special cases where automatic auditing is not available.
     * 
     * @param entity The entity to audit
     * @param userId The user ID to set (optional, will use current user if null)
     */
    public void setCreationAudit(BaseEntity entity, String userId) {
        if (entity == null) {
            log.warn("Cannot set creation audit on null entity");
            return;
        }

        Instant now = Instant.now();
        String auditUser = userId != null ? userId : auditorAware.getCurrentUserId();

        entity.setCreatedDate(now);
        entity.setCreatedBy(auditUser);
        entity.setLastModifiedDate(now);
        entity.setLastModifiedBy(auditUser);

        log.debug("Set creation audit for entity: user={}, timestamp={}", auditUser, now);
    }

    /**
     * Manually sets update audit fields on an entity.
     * Use this method only in special cases where automatic auditing is not available.
     * 
     * @param entity The entity to audit
     * @param userId The user ID to set (optional, will use current user if null)
     */
    public void setUpdateAudit(BaseEntity entity, String userId) {
        if (entity == null) {
            log.warn("Cannot set update audit on null entity");
            return;
        }

        Instant now = Instant.now();
        String auditUser = userId != null ? userId : auditorAware.getCurrentUserId();

        entity.setLastModifiedDate(now);
        entity.setLastModifiedBy(auditUser);

        log.debug("Set update audit for entity: user={}, timestamp={}", auditUser, now);
    }

    /**
     * Performs a manual soft delete with audit tracking.
     * 
     * @param entity The entity to soft delete
     * @param userId The user ID performing the deletion (optional, will use current user if null)
     */
    public void performSoftDelete(BaseEntity entity, String userId) {
        if (entity == null) {
            log.warn("Cannot perform soft delete on null entity");
            return;
        }

        String deleteUser = userId != null ? userId : auditorAware.getCurrentUserId();
        entity.softDelete(deleteUser);

        log.debug("Performed soft delete for entity: deletedBy={}, deletedDate={}", 
                 deleteUser, entity.getDeletedDate());
    }

    /**
     * Restores a soft-deleted entity with audit tracking.
     * 
     * @param entity The entity to restore
     * @param userId The user ID performing the restoration (optional, will use current user if null)
     */
    public void performRestore(BaseEntity entity, String userId) {
        if (entity == null) {
            log.warn("Cannot restore null entity");
            return;
        }

        if (!entity.isDeleted()) {
            log.warn("Attempting to restore entity that is not deleted");
            return;
        }

        String restoreUser = userId != null ? userId : auditorAware.getCurrentUserId();
        entity.restore(restoreUser);

        log.debug("Restored entity: restoredBy={}", restoreUser);
    }

    /**
     * Gets the current user ID from the security context.
     * 
     * @return Current user ID
     */
    public String getCurrentUserId() {
        return auditorAware.getCurrentUserId();
    }

    /**
     * Checks if there's an authenticated user in the current context.
     * 
     * @return true if there's an authenticated user, false otherwise
     */
    public boolean hasAuthenticatedUser() {
        return auditorAware.hasAuthenticatedUser();
    }

    /**
     * Sets a correlation ID for tracking related operations.
     * 
     * @param entity The entity to set correlation ID on
     * @param correlationId The correlation ID (optional, will generate one if null)
     */
    public void setCorrelationId(BaseEntity entity, String correlationId) {
        if (entity == null) {
            log.warn("Cannot set correlation ID on null entity");
            return;
        }

        String corrId = correlationId != null ? correlationId : generateCorrelationId();
        entity.setCorrelationId(corrId);

        log.debug("Set correlation ID for entity: {}", corrId);
    }

    /**
     * Generates a unique correlation ID.
     * 
     * @return Generated correlation ID
     */
    private String generateCorrelationId() {
        return "audit_" + System.currentTimeMillis() + "_" + 
               Thread.currentThread().getId();
    }
}