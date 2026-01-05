package com.codzs.mapper.organization;

import com.codzs.dto.organization.request.DatabaseSchemaRequestDto;
import com.codzs.dto.organization.response.DatabaseSchemaResponseDto;
import com.codzs.entity.organization.DatabaseSchema;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MapStruct mapper for DatabaseSchema entities and DTOs.
 * Handles mapping between database schema entities, request DTOs, and response DTOs
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
public interface DatabaseSchemaMapper {

    // ========================= DATABASE SCHEMA MAPPINGS =========================
    
    /**
     * Maps DatabaseSchemaRequestDto to DatabaseSchema entity.
     * Sets default values and validates service type.
     * Audit fields are automatically populated by Spring Data MongoDB auditing.
     */
    @Mapping(target = "id", ignore = true)
    DatabaseSchema toEntity(DatabaseSchemaRequestDto requestDto);

    /**
     * Updates existing DatabaseSchema entity with request data.
     * Audit fields are automatically updated by Spring Data MongoDB auditing.
     */
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget DatabaseSchema schema, 
                     DatabaseSchemaRequestDto requestDto);

    /**
     * Maps DatabaseSchema entity to DatabaseSchemaResponseDto.
     */
    DatabaseSchemaResponseDto toResponse(DatabaseSchema schema);

    /**
     * Maps list of DatabaseSchema entities to list of response DTOs.
     */
    List<DatabaseSchemaResponseDto> toResponseList(List<DatabaseSchema> schemas);
}