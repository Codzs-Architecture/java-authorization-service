package com.codzs.framework.mapper.localization;

import com.codzs.framework.dto.localization.CodeInfoDto;
import com.codzs.framework.service.localization.LocalizationCodeService.CodeInfo;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MapStruct mapper for CodeInfo and CodeInfoDto.
 * Handles mapping between service layer CodeInfo and DTO layer CodeInfoDto.
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
public interface CodeInfoMapper {

    /**
     * Maps CodeInfo to CodeInfoDto.
     */
    CodeInfoDto toDto(CodeInfo codeInfo);

    /**
     * Maps list of CodeInfo to list of CodeInfoDto.
     */
    List<CodeInfoDto> toDtoList(List<CodeInfo> codeInfoList);
}