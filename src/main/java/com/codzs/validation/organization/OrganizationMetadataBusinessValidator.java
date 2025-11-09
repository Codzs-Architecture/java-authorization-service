package com.codzs.validation.organization;

import com.codzs.constant.organization.OrganizationIndustryEnum;
import com.codzs.constant.organization.OrganizationSizeEnum;
import com.codzs.entity.organization.Organization;
import com.codzs.exception.bean.ValidationError;
import com.codzs.exception.type.ValidationException;

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
    private final OrganizationIndustryEnum organizationIndustryEnum;
    private final OrganizationSizeEnum organizationSizeEnum;
 
    public OrganizationMetadataBusinessValidator(
        OrganizationIndustryEnum organizationIndustryEnum,
        OrganizationSizeEnum organizationSizeEnum
    ) {
        this.organizationIndustryEnum = organizationIndustryEnum;
        this.organizationSizeEnum = organizationSizeEnum;
    }

    // ========== ENTRY POINT METHODS FOR ORGANIZATION METADATA APIs ==========

    /**
     * Validates industry update for service layer.
     * Entry point for: PUT /api/v1/organizations/{id}/metadata/industry
     */
    public void validateIndustryUpdate(Organization organization, String industry) {
        List<ValidationError> errors = new ArrayList<>();
        
        // Get validation data for industry
        boolean isValidIndustry = isValidIndustry(industry);
        
        validateIndustryBusinessRules(industry, isValidIndustry, errors);
        validateIndustryConstraints(organization, industry, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Industry update validation failed", errors);
        }
    }

    /**
     * Validates size update for service layer.
     * Entry point for: PUT /api/v1/organizations/{id}/metadata/size
     */
    public void validateSizeUpdate(Organization organization, String size) {
        List<ValidationError> errors = new ArrayList<>();
        
        // Get validation data for size
        boolean isValidSize = isValidSize(size);
        
        validateSizeBusinessRules(size, isValidSize, errors);
        validateSizeConstraints(organization, size, errors);
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Size update validation failed", errors);
        }
    }

    // ========== CORE VALIDATION METHODS ==========

    private void validateIndustryBusinessRules(String industry, boolean isValidIndustry,
                                             List<ValidationError> errors) {
        if (!StringUtils.hasText(industry)) {
            return; // Allow null/empty - validation handled by DTO annotations
        }

        // Use validation data passed from service layer
        if (!isValidIndustry) {
            errors.add(
                ValidationError
                    .builder()
                    .field("industry")
                    .rejectedValue(industry)
                    .message("Invalid industry: " + industry + ". Must be one of the supported industries.")
                    .build()
            );
        }
    }

    private void validateSizeBusinessRules(String size, boolean isValidSize,
                                         List<ValidationError> errors) {
        if (!StringUtils.hasText(size)) {
            return; // Allow null/empty - validation handled by DTO annotations
        }

        // Use validation data passed from service layer
        if (!isValidSize) {
            errors.add(
                ValidationError
                    .builder()
                    .field("size")
                    .rejectedValue(size)
                    .message("Invalid organization size: " + size + ". Must be one of the supported sizes.")
                    .build()
            );
        }
    }

    private void validateIndustryConstraints(Organization organization, String industry,
                                           List<ValidationError> errors) {
        // Business rule: Certain industries might have restrictions based on organization type
        if (StringUtils.hasText(industry) && "GOVERNMENT".equals(industry)) {
            if (!"ENTERPRISE".equals(organization.getOrganizationType())) {
                errors.add(
                    ValidationError
                        .builder()
                        .field("industry")
                        .rejectedValue(industry)
                        .message("Government industry is only allowed for Enterprise organizations")
                        .build()
                );
            }
        }
    }

    private void validateSizeConstraints(Organization organization, String size,
                                       List<ValidationError> errors) {
        // Business rule: Validate size consistency with organization type
        if (StringUtils.hasText(size)) {
            if ("INDIVIDUAL".equals(organization.getOrganizationType()) && 
                ("LARGE".equals(size) || "ENTERPRISE".equals(size))) {
                errors.add(
                    ValidationError
                        .builder()
                        .field("size")
                        .rejectedValue(size)
                        .message("Individual organizations cannot be set to LARGE or ENTERPRISE size")
                        .build()
                );
            }
        }
    }

    // ========== HELPER METHODS FOR SKIPFURTHERSTEP LOGIC ==========

    public boolean isValidIndustry(String industry) {
        if (!StringUtils.hasText(industry)) {
            return false;
        }
        
        List<String> validIndustries = getAvailableIndustries();
        return validIndustries.contains(industry);
    }

    public boolean isValidSize(String size) {
        if (!StringUtils.hasText(size)) {
            return false;
        }
        
        List<String> validSizes = getAvailableSizes();
        return validSizes.contains(size);
    }

    public List<String> getAvailableIndustries() {
        return organizationIndustryEnum.getOptions();
    }

    public List<String> getAvailableSizes() {
        return organizationSizeEnum.getOptions();
    }
}