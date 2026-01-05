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
    public abstract OrganizationMetadata toEntity(OrganizationMetadataRequestDto requestDto);

    /**
     * Updates existing OrganizationMetadata entity with request data.
     * Only updates provided fields, preserves existing values for null fields.
     */
    public abstract void updateEntity(@MappingTarget OrganizationMetadata metadata, OrganizationMetadataRequestDto requestDto);

    // ========================= RESPONSE MAPPINGS =========================
    
    /**
     * Maps OrganizationMetadata entity to OrganizationMetadataResponseDto.
     */
    public abstract OrganizationMetadataResponseDto toResponse(OrganizationMetadata metadata);

}