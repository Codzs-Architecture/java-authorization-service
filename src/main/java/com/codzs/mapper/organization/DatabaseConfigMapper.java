package com.codzs.mapper.organization;

import com.codzs.dto.organization.request.DatabaseConfigRequestDto;
import com.codzs.dto.organization.request.DatabaseSchemaRequestDto;
import com.codzs.dto.organization.response.DatabaseConfigResponseDto;
import com.codzs.dto.organization.response.DatabaseSchemaResponseDto;
import com.codzs.entity.organization.DatabaseConfig;
import com.codzs.entity.organization.DatabaseSchema;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MapStruct mapper for DatabaseConfig and DatabaseSchema entities and DTOs.
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

    // ========================= DATABASE SCHEMA MAPPINGS =========================
    
    /**
     * Maps DatabaseSchemaRequestDto to DatabaseSchema entity.
     * Sets default values and validates service type.
     * Audit fields are automatically populated by Spring Data MongoDB auditing.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "schemaName", target = "schemaName", qualifiedByName = "normalizeSchemaName")
    DatabaseSchema toSchemaEntity(DatabaseSchemaRequestDto requestDto);

    /**
     * Post-mapping method to set non-audit fields for schema creation.
     */

    /**
     * Updates existing DatabaseSchema entity with request data.
     * Audit fields are automatically updated by Spring Data MongoDB auditing.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "schemaName", target = "schemaName", qualifiedByName = "normalizeSchemaName")
    void updateSchemaEntity(@MappingTarget DatabaseSchema schema, 
                           DatabaseSchemaRequestDto requestDto);


    /**
     * Maps DatabaseSchema entity to DatabaseSchemaResponseDto.
     */
    DatabaseSchemaResponseDto toSchemaResponse(DatabaseSchema schema);

    /**
     * Maps list of DatabaseSchema entities to list of response DTOs.
     */
    List<DatabaseSchemaResponseDto> toSchemaResponseList(List<DatabaseSchema> schemas);

    // ========================= UTILITY METHODS =========================
    
    /**
     * Normalizes schema name according to naming convention.
     * Format: codzs_<org_abbr>_<service_type>_<env>
     */
    @Named("normalizeSchemaName")
    default String normalizeSchemaName(String schemaName) {
        if (schemaName == null || schemaName.trim().isEmpty()) {
            return null;
        }
        
        // Convert to lowercase and replace any invalid characters
        String normalized = schemaName.trim().toLowerCase();
        
        // Replace spaces and special characters with underscores
        normalized = normalized.replaceAll("[^a-z0-9_]", "_");
        
        // Remove multiple consecutive underscores
        normalized = normalized.replaceAll("_{2,}", "_");
        
        // Remove leading/trailing underscores
        normalized = normalized.replaceAll("^_+|_+$", "");
        
        return normalized;
    }

}