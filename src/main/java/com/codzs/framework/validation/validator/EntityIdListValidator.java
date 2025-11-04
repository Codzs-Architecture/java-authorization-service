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

import java.util.List;

/**
 * Validator implementation for ValidEntityId annotation for List<String> fields.
 * Validates that a list of string IDs reference existing entities in the database.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
public class EntityIdListValidator implements ConstraintValidator<ValidEntityId, List<String>> {

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
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        // Handle null values
        if (value == null) {
            return allowNull;
        }
        
        // Handle empty list
        if (value.isEmpty()) {
            return allowNull;
        }

        try {
            // Remove null and empty strings from the list
            List<String> validIds = value.stream()
                    .filter(id -> id != null && !id.trim().isEmpty())
                    .toList();
            
            // If after filtering we have no valid IDs and original list wasn't empty, fail
            if (validIds.isEmpty() && !value.isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    "List contains only null or empty ID values"
                ).addConstraintViolation();
                return false;
            }
            
            // If no IDs to validate, return based on allowNull
            if (validIds.isEmpty()) {
                return allowNull;
            }

            // Build the query for all IDs
            Query query = new Query(Criteria.where("id").in(validIds));
            
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
            
            // Count how many entities exist
            long existingCount = mongoTemplate.count(query, entityClass);
            
            // Check if all IDs exist
            if (existingCount != validIds.size()) {
                // Find which IDs don't exist for better error message
                List<Object> existingIds = mongoTemplate.findDistinct(query, "id", entityClass, Object.class);
                List<String> missingIds = validIds.stream()
                        .filter(id -> !existingIds.contains(id))
                        .toList();
                
                String errorMessage = buildErrorMessage(missingIds);
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(errorMessage)
                       .addConstraintViolation();
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            // If we can't validate, log error and fail validation
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Unable to validate entity IDs for " + entityClass.getSimpleName() + ": " + e.getMessage()
            ).addConstraintViolation();
            return false;
        }
    }

    /**
     * Builds an appropriate error message based on the validation criteria and missing IDs
     */
    private String buildErrorMessage(List<String> missingIds) {
        String entityName = entityClass.getSimpleName();
        String idsText = missingIds.size() == 1 ? "ID" : "IDs";
        String missingIdsStr = String.join(", ", missingIds);
        
        switch (checkDeleted) {
            case NON_DELETED:
                return "Invalid " + entityName + " " + idsText + ": [" + missingIdsStr + "]. The referenced " + 
                       entityName + "(s) do not exist or are deleted.";
            case DELETED_ONLY:
                return "Invalid " + entityName + " " + idsText + ": [" + missingIdsStr + "]. The referenced " + 
                       entityName + "(s) are not deleted or do not exist.";
            case ALL:
                return "Invalid " + entityName + " " + idsText + ": [" + missingIdsStr + "]. The referenced " + 
                       entityName + "(s) do not exist.";
            default:
                return "Invalid " + entityName + " " + idsText + ": [" + missingIdsStr + "]. The referenced " + 
                       entityName + "(s) do not exist.";
        }
    }
}