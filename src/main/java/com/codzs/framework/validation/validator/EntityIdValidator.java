package com.codzs.framework.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.codzs.framework.entity.BaseEntity;
import com.codzs.framework.validation.annotation.ValidEntityId;

/**
 * Validator implementation for ValidEntityId annotation.
 * Validates that a string ID references an existing entity in the database.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
public class EntityIdValidator implements ConstraintValidator<ValidEntityId, String> {

    @Autowired
    private MongoTemplate mongoTemplate;

    private Class<?> entityClass;
    private boolean allowNull;
    private ValidEntityId.CheckDeletedStatus checkDeleted;

    @Override
    public void initialize(ValidEntityId constraintAnnotation) {
        this.entityClass = constraintAnnotation.entityClass();
        this.allowNull = constraintAnnotation.allowNull();
        this.checkDeleted = constraintAnnotation.checkDeleted();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Handle null values
        if (value == null) {
            return allowNull;
        }
        
        // Handle empty/blank values
        if (value.trim().isEmpty()) {
            return allowNull;
        }

        try {
            // Build the query
            Query query = new Query(Criteria.where("id").is(value));
            
            // Add deletion status criteria if the entity extends BaseEntity
            if (BaseEntity.class.isAssignableFrom(entityClass)) {
                switch (checkDeleted) {
                    case NON_DELETED:
                        query.addCriteria(Criteria.where("deletedDate").is(null));
                        break;
                    case DELETED_ONLY:
                        query.addCriteria(Criteria.where("deletedDate").ne(null));
                        break;
                    case ALL:
                        // No additional criteria needed
                        break;
                }
            }
            
            // Check if entity exists
            boolean exists = mongoTemplate.exists(query, entityClass);
            
            if (!exists) {
                // Customize error message based on deletion status
                String errorMessage = buildErrorMessage();
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(errorMessage)
                       .addConstraintViolation();
            }
            
            return exists;
            
        } catch (Exception e) {
            // If we can't validate, log error and fail validation
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Unable to validate entity ID for " + entityClass.getSimpleName() + ": " + e.getMessage()
            ).addConstraintViolation();
            return false;
        }
    }

    /**
     * Builds an appropriate error message based on the validation criteria
     */
    private String buildErrorMessage() {
        String entityName = entityClass.getSimpleName();
        
        switch (checkDeleted) {
            case NON_DELETED:
                return "Invalid " + entityName + " ID. The referenced " + entityName + " does not exist or is deleted.";
            case DELETED_ONLY:
                return "Invalid " + entityName + " ID. The referenced " + entityName + " is not deleted or does not exist.";
            case ALL:
                return "Invalid " + entityName + " ID. The referenced " + entityName + " does not exist.";
            default:
                return "Invalid " + entityName + " ID. The referenced " + entityName + " does not exist.";
        }
    }
}