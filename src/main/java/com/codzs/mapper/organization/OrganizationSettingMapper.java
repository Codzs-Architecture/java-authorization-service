package com.codzs.mapper.organization;

import com.codzs.dto.organization.request.OrganizationSettingRequestDto;
import com.codzs.dto.organization.response.OrganizationSettingResponseDto;
import com.codzs.entity.organization.OrganizationSetting;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

/**
 * MapStruct mapper for OrganizationSetting entity and DTOs.
 * Simplified mapper that relies on Bean Validation annotations for validation.
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
public interface OrganizationSettingMapper {

    /**
     * Maps OrganizationSettingRequestDto to OrganizationSetting entity.
     * Validation is handled by Bean Validation annotations on the DTO.
     */
    OrganizationSetting toEntity(OrganizationSettingRequestDto requestDto);

    /**
     * Updates existing OrganizationSetting entity with request data.
     * Only updates provided fields, preserves existing values for null fields.
     * Validation is handled by Bean Validation annotations on the DTO.
     */
    void updateEntity(@MappingTarget OrganizationSetting setting, OrganizationSettingRequestDto requestDto);

    /**
     * Maps OrganizationSetting entity to OrganizationSettingResponseDto.
     */
    OrganizationSettingResponseDto toResponse(OrganizationSetting setting);
}