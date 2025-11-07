package com.codzs.controller.organization;

import com.codzs.constant.organization.OrganizationSchemaConstants;
import com.codzs.dto.organization.request.DatabaseSchemaRequestDto;
import com.codzs.dto.organization.response.DatabaseSchemaResponseDto;
import com.codzs.entity.organization.DatabaseSchema;
import com.codzs.framework.annotation.header.CommonHeaders;
import com.codzs.framework.constant.HeaderConstant;
import com.codzs.framework.validation.annotation.ValidObjectId;
import com.codzs.mapper.organization.DatabaseSchemaMapper;
import com.codzs.service.organization.DatabaseSchemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/database/schemas")
@RequiredArgsConstructor
@Validated
@Tag(name = "Database Schema Management", description = "APIs for managing organization database schemas")
public class DatabaseSchemaController {

    private final DatabaseSchemaService databaseSchemaService;
    private final DatabaseSchemaMapper databaseSchemaMapper;

    @PostMapping
    @CommonHeaders
    @Operation(
        summary = "Add database schema",
        description = "Adds a new database schema for additional services to the organization"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Schema creation initiated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DatabaseSchemaResponseDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid service type or schema already exists"),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DatabaseSchemaResponseDto> addDatabaseSchema(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidObjectId(allowNull = false)
            @PathVariable("organizationId") 
            String organizationId,
            
            @Parameter(description = "Database schema creation request", required = true)
            @Valid 
            @RequestBody 
            DatabaseSchemaRequestDto request,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Adding database schema for organization: {}, forService: {}", 
            organizationId, request.getForService());
        
        DatabaseSchema databaseSchema = databaseSchemaMapper.toEntity(request);
        List<DatabaseSchema> allSchemas = databaseSchemaService.addDatabaseSchema(
            organizationId, databaseSchema
        );
        
        // Find the newly created schema (it should be the one with the ID we set)
        DatabaseSchema createdSchema = allSchemas.stream()
            .filter(schema -> databaseSchema.getId().equals(schema.getId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Created schema not found in returned list"));
        
        DatabaseSchemaResponseDto response = databaseSchemaMapper.toResponse(createdSchema);
        
        log.info("Successfully initiated schema creation for organization: {}, schema: {}", 
            organizationId, createdSchema.getSchemaName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @CommonHeaders
    @Operation(
        summary = "List database schemas",
        description = "Retrieves all database schemas associated with the organization"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Schemas retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
        ),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<DatabaseSchemaResponseDto>> listDatabaseSchemas(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidObjectId(allowNull = false)
            @PathVariable("organizationId") 
            String organizationId,
            
            @Parameter(description = "Filter by service type", example = "auth")
            @RequestParam(value = "forService", required = false) 
            String forService,
            
            @Parameter(description = "Filter by schema status", example = "active")
            @RequestParam(value = "status", required = false) 
            String status,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Listing database schemas for organization: {}, forService: {}, status: {}", 
            organizationId, forService, status);
        
        List<DatabaseSchema> schemas = databaseSchemaService.listDatabaseSchemas(
            organizationId, forService, status
        );
        
        List<DatabaseSchemaResponseDto> response = schemas.stream()
            .map(databaseSchemaMapper::toResponse)
            .toList();
        
        log.info("Successfully retrieved {} schemas for organization: {}", response.size(), organizationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{schemaId}")
    @CommonHeaders
    @Operation(
        summary = "Get database schema details",
        description = "Retrieves detailed information about a specific database schema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Schema details retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DatabaseSchemaResponseDto.class))
        ),
        @ApiResponse(responseCode = "404", description = "Organization or schema not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DatabaseSchemaResponseDto> getDatabaseSchemaDetails(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidObjectId(allowNull = false)
            @PathVariable("organizationId") 
            String organizationId,
            
            @Parameter(description = "Schema ID", required = true, example = OrganizationSchemaConstants.EXAMPLE_SCHEMA_ID)
            @ValidObjectId(allowNull = false)
            @PathVariable("schemaId") 
            String schemaId,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Getting schema details for organization: {}, schema: {}", organizationId, schemaId);
        
        return databaseSchemaService.getDatabaseSchema(organizationId, schemaId)
                .map(schema -> {
                    DatabaseSchemaResponseDto response = databaseSchemaMapper.toResponse(schema);
                    
                    log.info("Successfully retrieved schema details: {} for organization: {}", schemaId, organizationId);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    log.warn("Database schema not found: {} for organization: {}", schemaId, organizationId);
                    return ResponseEntity.notFound().build();
                });
    }

    @PutMapping("/{schemaId}")
    @CommonHeaders
    @Operation(
        summary = "Update database schema",
        description = "Updates an existing database schema for the organization"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Schema updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Organization or schema not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> updateDatabaseSchema(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidObjectId(allowNull = false)
            @PathVariable("organizationId") 
            String organizationId,
            
            @Parameter(description = "Schema ID to update", required = true, example = OrganizationSchemaConstants.EXAMPLE_SCHEMA_ID)
            @ValidObjectId(allowNull = false)
            @PathVariable("schemaId") 
            String schemaId,
            
            @Parameter(description = "Database schema update request", required = true)
            @Valid 
            @RequestBody 
            DatabaseSchemaRequestDto request,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Updating database schema for organization: {}, schema: {}", organizationId, schemaId);
        
        DatabaseSchema databaseSchema = databaseSchemaMapper.toEntity(request);
        databaseSchema.setId(schemaId);
        
        databaseSchemaService.updateDatabaseSchema(organizationId, databaseSchema);
        
        log.info("Successfully updated schema: {} for organization: {}", schemaId, organizationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{schemaId}")
    @CommonHeaders
    @Operation(
        summary = "Remove database schema",
        description = "Removes a database schema from the organization (soft delete)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Schema removed successfully"),
        @ApiResponse(responseCode = "404", description = "Organization or schema not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "409", description = "Schema cannot be removed due to dependencies"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> removeDatabaseSchema(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidObjectId(allowNull = false)
            @PathVariable("organizationId") 
            String organizationId,
            
            @Parameter(description = "Schema ID to remove", required = true, example = OrganizationSchemaConstants.EXAMPLE_SCHEMA_ID)
            @ValidObjectId(allowNull = false)
            @PathVariable("schemaId") 
            String schemaId,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Removing database schema for organization: {}, schema: {}", organizationId, schemaId);
        
        databaseSchemaService.removeDatabaseSchema(organizationId, schemaId);
        
        log.info("Successfully removed schema: {} from organization: {}", schemaId, organizationId);
        return ResponseEntity.noContent().build();
    }
}