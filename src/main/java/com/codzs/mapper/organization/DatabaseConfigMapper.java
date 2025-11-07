package com.codzs.mapper.organization;

import com.codzs.dto.organization.request.DatabaseConfigRequestDto;
import com.codzs.dto.organization.response.DatabaseConfigResponseDto;
import com.codzs.entity.organization.DatabaseConfig;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

/**
 * MapStruct mapper for DatabaseConfig entities and DTOs.
 * Handles mapping between database configuration entities, request DTOs, and response DTOs
 * with proper data transformations.
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
public interface DatabaseConfigMapper {

    // ========================= DATABASE CONFIG MAPPINGS =========================
    
    /**
     * Maps DatabaseConfigRequestDto to DatabaseConfig entity.
     */
    DatabaseConfig toEntity(DatabaseConfigRequestDto requestDto);

    /**
     * Updates existing DatabaseConfig entity with request data.
     */
    void updateEntity(@MappingTarget DatabaseConfig databaseConfig, DatabaseConfigRequestDto requestDto);

    /**
     * Maps DatabaseConfig entity to DatabaseConfigResponseDto.
     */
    DatabaseConfigResponseDto toResponse(DatabaseConfig databaseConfig);
}