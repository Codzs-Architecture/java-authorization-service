package com.codzs.framework.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import com.codzs.framework.validation.validator.EntityIdListValidator;
import com.codzs.framework.validation.validator.EntityIdValidator;

/**
 * Custom validation annotation to validate that a string ID or list of string IDs
 * reference existing entities in the database.
 * 
 * Usage for single ID:
 * @ValidEntityId(entityClass = Organization.class, checkDeleted = CheckDeletedStatus.NON_DELETED)
 * private String organizationId;
 * 
 * Usage for list of IDs:
 * @ValidEntityId(entityClass = User.class, checkDeleted = CheckDeletedStatus.NON_DELETED)
 * private List<String> ownerUserIds;
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {EntityIdValidator.class, EntityIdListValidator.class})
public @interface ValidEntityId {
    
    String message() default "Invalid entity ID. The referenced {entityClass} does not exist.";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * The entity class to validate against
     */
    Class<?> entityClass();
    
    /**
     * Allow null values (useful for optional fields)
     */
    boolean allowNull() default false;
    
    /**
     * Check deletion status of the entity
     */
    CheckDeletedStatus checkDeleted() default CheckDeletedStatus.NON_DELETED;
    
    /**
     * Enum to specify what deletion status to check
     */
    enum CheckDeletedStatus {
        /** Only validate against non-deleted entities */
        NON_DELETED,
        /** Only validate against deleted entities */
        DELETED_ONLY,
        /** Validate against all entities regardless of deletion status */
        ALL
    }
}