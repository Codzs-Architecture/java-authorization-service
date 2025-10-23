package com.codzs.mapper.organization;

import com.codzs.constant.organization.OrganizationIndustryEnum;
import com.codzs.constant.organization.OrganizationSizeEnum;
import com.codzs.dto.organization.request.OrganizationMetadataRequestDto;
import com.codzs.dto.organization.response.OrganizationMetadataResponseDto;
import com.codzs.entity.organization.OrganizationMetadata;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MapStruct mapper for OrganizationMetadata entity and DTOs.
 * Handles mapping between OrganizationMetadata entity, request DTOs, and response DTOs
 * with proper data transformations and validation.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
@Component
public abstract class OrganizationMetadataMapper {

    @Autowired
    protected OrganizationIndustryEnum organizationIndustryEnum;
    
    @Autowired
    protected OrganizationSizeEnum organizationSizeEnum;

    // ========================= CREATE MAPPINGS =========================
    
    /**
     * Maps OrganizationMetadataRequestDto to OrganizationMetadata entity.
     * Validates and normalizes metadata values.
     */
    @Mapping(source = "industry", target = "industry", qualifiedByName = "normalizeIndustry")
    @Mapping(source = "size", target = "size", qualifiedByName = "normalizeSize")
    public abstract OrganizationMetadata toEntity(OrganizationMetadataRequestDto requestDto);

    /**
     * Updates existing OrganizationMetadata entity with request data.
     * Only updates provided fields, preserves existing values for null fields.
     */
    @Mapping(source = "industry", target = "industry", qualifiedByName = "normalizeIndustry")
    @Mapping(source = "size", target = "size", qualifiedByName = "normalizeSize")
    public abstract void updateEntity(@MappingTarget OrganizationMetadata metadata, OrganizationMetadataRequestDto requestDto);

    // ========================= RESPONSE MAPPINGS =========================
    
    /**
     * Maps OrganizationMetadata entity to OrganizationMetadataResponseDto.
     */
    public abstract OrganizationMetadataResponseDto toResponse(OrganizationMetadata metadata);

    // ========================= UTILITY METHODS =========================
    
    /**
     * Normalizes industry value to standard format.
     * Validates against dynamic industry categories from config server.
     */
    @Named("normalizeIndustry")
    protected String normalizeIndustry(String industry) {
        if (industry == null || industry.trim().isEmpty()) {
            return null;
        }
        
        String normalized = industry.trim().toUpperCase();
        
        // Validate against dynamic industry categories from config server
        if (organizationIndustryEnum.isValidOption(normalized)) {
            return normalized;
        }
        
        // Use effective value logic which handles defaults from config
        String effectiveValue = organizationIndustryEnum.getEffectiveValue(normalized);
        return effectiveValue != null ? effectiveValue : "OTHER";
    }

    /**
     * Normalizes organization size to standard format.
     * Validates against dynamic size categories from config server.
     */
    @Named("normalizeSize")
    protected String normalizeSize(String size) {
        if (size == null || size.trim().isEmpty()) {
            return null;
        }
        
        String normalized = size.trim();
        
        // Validate against dynamic size categories from config server
        if (organizationSizeEnum.isValidOption(normalized)) {
            return normalized;
        }
        
        // Use effective value logic which handles defaults from config
        String effectiveValue = organizationSizeEnum.getEffectiveValue(normalized);
        return effectiveValue != null ? effectiveValue : "1-10";
    }

}