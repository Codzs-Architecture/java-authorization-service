package com.codzs.validation.organization;

import com.codzs.entity.organization.Organization;
import com.codzs.entity.organization.OrganizationMetadata;
import com.codzs.exception.validation.ValidationException;
import com.codzs.framework.constant.CommonConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.ArrayList;

/**
 * Business validator for Organization Metadata operations.
 * Focuses on metadata business rules and organizational constraints.
 * Works with metadata as embedded sub-object within organizations.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
public class OrganizationMetadataBusinessValidator {

    public OrganizationMetadataBusinessValidator() {
    }

    // ========== ENTRY POINT METHODS FOR ORGANIZATION METADATA APIs ==========

    /**
     * Validates metadata update for service layer.
     * Entry point for: PUT /api/v1/organizations/{id}/metadata
     */
    public void validateMetadataUpdate(Organization organization, OrganizationMetadata metadata, boolean isValidIndustry, boolean isValidSize) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        validateMetadataBusinessRules(metadata, isValidIndustry, isValidSize, errors);
        validateMetadataConstraints(organization, metadata, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Metadata update validation failed", errors);
        }
    }

    /**
     * Validates metadata update and returns skip flag.
     * Returns true if update should be skipped (no changes), false otherwise.
     */
    public Boolean validateMetadataUpdateFlow(Organization organization, OrganizationMetadata metadata, boolean isValidIndustry, boolean isValidSize) {
        // Check if metadata is unchanged (idempotent operation)
        OrganizationMetadata currentMetadata = organization.getMetadata();
        if (isMetadataUnchanged(currentMetadata, metadata)) {
            return CommonConstants.SKIP_FURTHER_STEP;
        }
        
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateMetadataBusinessRules(metadata, isValidIndustry, isValidSize, errors);
        validateMetadataConstraints(organization, metadata, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Metadata update validation failed", errors);
        }
        
        return !CommonConstants.SKIP_FURTHER_STEP;
    }

    /**
     * Validates industry update for service layer.
     * Entry point for: PUT /api/v1/organizations/{id}/metadata/industry
     */
    public void validateIndustryUpdate(Organization organization, String industry, boolean isValidIndustry) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        validateIndustryBusinessRules(industry, isValidIndustry, errors);
        validateIndustryConstraints(organization, industry, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Industry update validation failed", errors);
        }
    }

    /**
     * Validates industry update and returns skip flag.
     * Returns true if update should be skipped (no changes), false otherwise.
     */
    public Boolean validateIndustryUpdateFlow(Organization organization, String industry, boolean isValidIndustry) {
        // Check if industry is unchanged (idempotent operation)
        String currentIndustry = organization.getMetadata() != null ? 
            organization.getMetadata().getIndustry() : null;
        if (isStringUnchanged(currentIndustry, industry)) {
            return CommonConstants.SKIP_FURTHER_STEP;
        }
        
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateIndustryBusinessRules(industry, isValidIndustry, errors);
        validateIndustryConstraints(organization, industry, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Industry update validation failed", errors);
        }
        
        return !CommonConstants.SKIP_FURTHER_STEP;
    }

    /**
     * Validates size update for service layer.
     * Entry point for: PUT /api/v1/organizations/{id}/metadata/size
     */
    public void validateSizeUpdate(Organization organization, String size, boolean isValidSize) {
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        
        validateSizeBusinessRules(size, isValidSize, errors);
        validateSizeConstraints(organization, size, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Size update validation failed", errors);
        }
    }

    /**
     * Validates size update and returns skip flag.
     * Returns true if update should be skipped (no changes), false otherwise.
     */
    public Boolean validateSizeUpdateFlow(Organization organization, String size, boolean isValidSize) {
        // Check if size is unchanged (idempotent operation)
        String currentSize = organization.getMetadata() != null ? 
            organization.getMetadata().getSize() : null;
        if (isStringUnchanged(currentSize, size)) {
            return CommonConstants.SKIP_FURTHER_STEP;
        }
        
        List<ValidationException.ValidationError> errors = new ArrayList<>();
        validateSizeBusinessRules(size, isValidSize, errors);
        validateSizeConstraints(organization, size, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Size update validation failed", errors);
        }
        
        return !CommonConstants.SKIP_FURTHER_STEP;
    }

    // ========== CORE VALIDATION METHODS ==========

    private void validateMetadataBusinessRules(OrganizationMetadata metadata, boolean isValidIndustry, boolean isValidSize,
                                             List<ValidationException.ValidationError> errors) {
        if (metadata == null) {
            errors.add(new ValidationException.ValidationError("metadata", "Metadata cannot be null"));
            return;
        }

        // Only business logic validations remain here - DTO validation handles @NotBlank etc.
        validateIndustryBusinessRules(metadata.getIndustry(), isValidIndustry, errors);
        validateSizeBusinessRules(metadata.getSize(), isValidSize, errors);
    }

    private void validateIndustryBusinessRules(String industry, boolean isValidIndustry,
                                             List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(industry)) {
            return; // Allow null/empty - validation handled by DTO annotations
        }

        // Use validation data passed from service layer
        if (!isValidIndustry) {
            errors.add(new ValidationException.ValidationError("industry", 
                "Invalid industry: " + industry + ". Must be one of the supported industries."));
        }
    }

    private void validateSizeBusinessRules(String size, boolean isValidSize,
                                         List<ValidationException.ValidationError> errors) {
        if (!StringUtils.hasText(size)) {
            return; // Allow null/empty - validation handled by DTO annotations
        }

        // Use validation data passed from service layer
        if (!isValidSize) {
            errors.add(new ValidationException.ValidationError("size", 
                "Invalid organization size: " + size + ". Must be one of the supported sizes."));
        }
    }

    private void validateMetadataConstraints(Organization organization, OrganizationMetadata metadata,
                                           List<ValidationException.ValidationError> errors) {
        // Business rule: Cannot clear metadata if organization has active subscriptions
        if ((metadata.getIndustry() == null || metadata.getIndustry().isEmpty()) &&
            (metadata.getSize() == null || metadata.getSize().isEmpty())) {
            // This would effectively clear all metadata - check if allowed
            validateMetadataClearingAllowed(organization, errors);
        }
    }

    private void validateIndustryConstraints(Organization organization, String industry,
                                           List<ValidationException.ValidationError> errors) {
        // Business rule: Certain industries might have restrictions based on organization type
        if (StringUtils.hasText(industry) && "GOVERNMENT".equals(industry)) {
            if (!"ENTERPRISE".equals(organization.getOrganizationType())) {
                errors.add(new ValidationException.ValidationError("industry", 
                    "Government industry is only allowed for Enterprise organizations"));
            }
        }
    }

    private void validateSizeConstraints(Organization organization, String size,
                                       List<ValidationException.ValidationError> errors) {
        // Business rule: Validate size consistency with organization type
        if (StringUtils.hasText(size)) {
            if ("INDIVIDUAL".equals(organization.getOrganizationType()) && 
                ("LARGE".equals(size) || "ENTERPRISE".equals(size))) {
                errors.add(new ValidationException.ValidationError("size", 
                    "Individual organizations cannot be set to LARGE or ENTERPRISE size"));
            }
        }
    }

    private void validateMetadataClearingAllowed(Organization organization,
                                               List<ValidationException.ValidationError> errors) {
        // Business rule: Cannot clear metadata if it's required for the organization type
        if ("ENTERPRISE".equals(organization.getOrganizationType())) {
            errors.add(new ValidationException.ValidationError("metadata", 
                "Enterprise organizations must have industry and size metadata"));
        }
    }

    // ========== HELPER METHODS FOR SKIPFURTHERSTEP LOGIC ==========

    private boolean isMetadataUnchanged(OrganizationMetadata current, OrganizationMetadata updated) {
        if (current == null && updated == null) {
            return true;
        }
        if (current == null || updated == null) {
            return false;
        }
        
        return isStringUnchanged(current.getIndustry(), updated.getIndustry()) &&
               isStringUnchanged(current.getSize(), updated.getSize());
    }

    private boolean isStringUnchanged(String current, String updated) {
        if (current == null && updated == null) {
            return true;
        }
        if (current == null || updated == null) {
            return false;
        }
        return current.equals(updated);
    }
}