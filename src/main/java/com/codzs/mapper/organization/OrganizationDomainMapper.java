package com.codzs.mapper.organization;

import com.codzs.dto.organization.request.DomainRequestDto;
import com.codzs.framework.constant.CommonConstants;
import com.codzs.framework.mapper.BaseMapper;
import com.codzs.dto.organization.response.DomainResponseDto;
import com.codzs.entity.domain.Domain;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MapStruct mapper for embedded Domain sub-objects and DTOs.
 * Handles mapping between embedded Domain objects, request DTOs, and response DTOs
 * with proper data transformations and UTC date handling.
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
public interface OrganizationDomainMapper extends BaseMapper{

    // ========================= CREATE MAPPINGS =========================
    
    /**
     * Maps DomainRequestDto to embedded Domain sub-object.
     * Sets default values for domain creation.
     */
    @Mapping(target = "id", expression = "java(generateId())")
    @Mapping(target = "isVerified", ignore = true)
    @Mapping(target = "verificationToken", ignore = true)
    @Mapping(target = "verifiedDate", ignore = true)
    @Mapping(source = "createdDate", target = "createdDate", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    Domain toEntity(DomainRequestDto requestDto);

    // ========================= UPDATE MAPPINGS =========================
    
    /**
     * Updates existing embedded Domain sub-object with request data.
     * Preserves verification status and timestamps unless explicitly changing.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isVerified", ignore = true) 
    @Mapping(target = "verificationToken", ignore = true)
    @Mapping(target = "verifiedDate", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    void updateEntity(@MappingTarget Domain domain, DomainRequestDto requestDto);

    // ========================= RESPONSE MAPPINGS =========================
    
    /**
     * Maps embedded Domain sub-object to DomainResponseDto.
     * Handles UTC date formatting and includes all domain information.
     */
    @Mapping(source = "createdDate", target = "createdDate", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Mapping(source = "verifiedDate", target = "verifiedDate", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Named("toResponse")
    DomainResponseDto toResponse(Domain domain);

    /**
     * Maps list of Domain entities to list of response DTOs.
     */
    @IterableMapping(qualifiedByName = "toResponse")
    List<DomainResponseDto> toResponseList(List<Domain> domains);

    // ========================= VERIFICATION MAPPINGS =========================
    
    /**
     * Maps embedded Domain sub-object for verification response.
     * Includes verification status and token information.
     */
    @Mapping(source = "verifiedDate", target = "verifiedDate", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Mapping(source = "createdDate", target = "createdDate", dateFormat = CommonConstants.UTC_TIMESTAMP_PATTERN)
    @Named("toVerificationResponse")
    DomainResponseDto toVerificationResponse(Domain domain);
}