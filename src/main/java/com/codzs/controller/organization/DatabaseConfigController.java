package com.codzs.controller.organization;

import com.codzs.constant.organization.OrganizationSwaggerConstants;
import com.codzs.dto.organization.request.DatabaseConfigRequestDto;
import com.codzs.dto.organization.request.DatabaseSchemaRequestDto;
import com.codzs.dto.organization.response.DatabaseConfigResponseDto;
import com.codzs.dto.organization.response.DatabaseSchemaResponseDto;
import com.codzs.entity.organization.DatabaseConfig;
import com.codzs.entity.organization.DatabaseSchema;
import com.codzs.entity.organization.Organization;
import com.codzs.framework.annotation.header.CommonHeaders;
import com.codzs.framework.constant.HeaderConstant;
import com.codzs.framework.validation.annotation.ValidUUID;
import com.codzs.mapper.organization.DatabaseConfigMapper;
import com.codzs.service.organization.DatabaseConfigService;
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
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/database")
@RequiredArgsConstructor
@Validated
@Tag(name = "Database Configuration Management", description = "APIs for managing organization-specific database configurations, schemas, and connectivity")
public class DatabaseConfigController {

    private final DatabaseConfigService databaseConfigService;
    private final DatabaseConfigMapper databaseConfigMapper;

    @GetMapping
    @CommonHeaders
    @Operation(
        summary = "Get database configuration",
        description = "Retrieves comprehensive database configuration and status for the organization"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Database configuration retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DatabaseConfigResponseDto.class))
        ),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DatabaseConfigResponseDto> getDatabaseConfiguration(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidUUID(allowNull = false, fieldName = "Organization ID")
            @PathVariable 
            UUID organizationId,
            
            @Parameter(description = "Include connection string in response (admin only)", example = "false")
            @RequestParam(value = "includeConnectionString", defaultValue = "false") 
            Boolean includeConnectionString,
            
            @Parameter(description = "Include certificate details (admin only)", example = "false")
            @RequestParam(value = "includeCertificate", defaultValue = "false") 
            Boolean includeCertificate,
            
            @Parameter(description = "Test database connectivity", example = "false")
            @RequestParam(value = "testConnection", defaultValue = "false") 
            Boolean testConnection,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Getting database configuration for organization: {}, includeConnectionString: {}, testConnection: {}", 
            organizationId, includeConnectionString, testConnection);
        
        DatabaseConfig databaseConfig = databaseConfigService.getDatabaseConfig(
            organizationId.toString()
        );

        // Map to response DTO
        DatabaseConfigResponseDto response = databaseConfigMapper.toResponse(databaseConfig);
        
        // Filter sensitive fields based on request parameters
        if (!includeConnectionString) {
            response.setConnectionString(null);
            log.debug("Connection string excluded from response for organization: {}", organizationId);
        }
        
        if (!includeCertificate) {
            response.setCertificate(null);
            log.debug("Certificate excluded from response for organization: {}", organizationId);
        }
        
        // Test connection if requested
        if (testConnection) {
            boolean testResults = testConnection(organizationId);
            response.setConnectionTestResults(testResults);
        }
            
        log.info("Successfully retrieved database configuration for organization: {}", organizationId);
        return ResponseEntity.ok(response);
    }

    private boolean testConnection(UUID organizationId) {
        boolean testResults = false;
        try {
            log.info("Testing database connectivity for organization: {}", organizationId);
            testResults = databaseConfigService.testDatabaseConnection(
                organizationId.toString()
            );
            log.info("Database connectivity test completed for organization: {}", organizationId);
        } catch (Exception e) {
            log.error("Database connectivity test failed for organization: {}", organizationId, e);
        }

        return testResults;
    }

    @PutMapping("/connection")
    @CommonHeaders
    @Operation(
        summary = "Update database connection",
        description = "Updates the database connection string for the organization with proper validation"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Database connection updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DatabaseConfigResponseDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid connection string or test failed"),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DatabaseConfigResponseDto> updateDatabaseConnection(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidUUID(allowNull = false, fieldName = "Organization ID")
            @PathVariable 
            UUID organizationId,
            
            @Parameter(description = "Database connection update request", required = true)
            @Valid 
            @RequestBody 
            DatabaseConfigRequestDto request,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) {
        
        log.info("Updating database connection for organization: {}", organizationId);
        
        DatabaseConfig databaseConfigEntity = databaseConfigMapper.toEntity(request);
        Organization updatedConfig = databaseConfigService.updateDatabaseConfig(
            organizationId.toString(), databaseConfigEntity.getConnectionString(), databaseConfigEntity.getCertificate()
        );
        
        DatabaseConfigResponseDto response = databaseConfigMapper.toResponse(updatedConfig.getDatabase());
        
        log.info("Successfully updated database connection for organization: {}", organizationId);
        return ResponseEntity.ok(response);
    }

    // @PostMapping("/certificate/rotate")
    // @CommonHeaders
    // @Operation(
    //     summary = "Rotate database certificate",
    //     description = "Rotates the database SSL certificate for the organization"
    // )
    // @ApiResponses(value = {
    //     @ApiResponse(
    //         responseCode = "200",
    //         description = "Certificate rotated successfully",
    //         content = @Content(mediaType = "application/json", schema = @Schema(implementation = DatabaseConfigResponseDto.class))
    //     ),
    //     @ApiResponse(responseCode = "404", description = "Organization not found"),
    //     @ApiResponse(responseCode = "403", description = "Access denied to organization"),
    //     @ApiResponse(responseCode = "500", description = "Internal server error or certificate generation failed")
    // })
    // public ResponseEntity<DatabaseConfigResponseDto> rotateDatabaseCertificate(
    //         @Parameter(description = "Organization ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
    //         @ValidUUID(allowNull = false, fieldName = "Organization ID")
    //         @PathVariable 
    //         UUID organizationId,
            
    //         @Parameter(description = "Reason for certificate rotation", example = "Annual certificate rotation")
    //         @RequestParam(value = "reason", required = false, defaultValue = "Manual certificate rotation") 
    //         String reason,
            
    //         @Parameter(description = "Certificate validity period", example = "365d")
    //         @RequestParam(value = "validityPeriod", required = false, defaultValue = "365d") 
    //         String validityPeriod,
            
    //         @Parameter(description = "Notify users about certificate rotation", example = "true")
    //         @RequestParam(value = "notifyUsers", required = false, defaultValue = "true") 
    //         Boolean notifyUsers,
            
    //         @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
    //         @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
    //         @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    // {
        
    //     log.info("Rotating database certificate for organization: {}, reason: {}, validityPeriod: {}", 
    //         organizationId, reason, validityPeriod);
        
    //     DatabaseConfig updatedConfig = databaseConfigService.rotateDatabaseCertificate(
    //         organizationId, reason, validityPeriod, notifyUsers, 
    //         headerOrganizationId, tenantId, correlationId
    //     );
        
    //     DatabaseConfigResponseDto response = databaseConfigMapper.toResponse(updatedConfig);
        
    //     log.info("Successfully rotated database certificate for organization: {}", organizationId);
    //     return ResponseEntity.ok(response);
    // }

    @PostMapping("/test")
    @CommonHeaders
    @Operation(
        summary = "Test database connectivity",
        description = "Tests database connectivity and returns detailed connection information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Connectivity test completed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> testDatabaseConnectivity(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidUUID(allowNull = false, fieldName = "Organization ID")
            @PathVariable 
            UUID organizationId,
            
            @Parameter(description = "Include performance metrics", example = "false")
            @RequestParam(value = "includePerformance", defaultValue = "false")
            Boolean includePerformance,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Testing database connectivity for organization: {}, includePerformance: {}", 
            organizationId, includePerformance);
        
        boolean testResults = testConnection(organizationId);
        
        log.info("Successfully tested database connectivity for organization: {}", organizationId);
        return ResponseEntity.ok(testResults);
    }

    @PostMapping("/schemas")
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
            @Parameter(description = "Organization ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidUUID(allowNull = false, fieldName = "Organization ID")
            @PathVariable 
            UUID organizationId,
            
            @Parameter(description = "Database schema creation request", required = true)
            @Valid 
            @RequestBody 
            DatabaseSchemaRequestDto request,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) {
        
        log.info("Adding database schema for organization: {}, forService: {}", 
            organizationId, request.getForService());
        
        DatabaseSchema schemaEntity = databaseConfigMapper.toSchemaEntity(request);
        List<DatabaseSchema> allSchemas = databaseConfigService.addDatabaseSchema(
            organizationId.toString(), schemaEntity
        );
        
        // Find the newly created schema (it should be the one with the ID we set)
        DatabaseSchema createdSchema = allSchemas.stream()
            .filter(schema -> schemaEntity.getId().equals(schema.getId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Created schema not found in returned list"));
        
        DatabaseSchemaResponseDto response = databaseConfigMapper.toSchemaResponse(createdSchema);
        
        log.info("Successfully initiated schema creation for organization: {}, schema: {}", 
            organizationId, createdSchema.getSchemaName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/schemas")
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
            @Parameter(description = "Organization ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidUUID(allowNull = false, fieldName = "Organization ID")
            @PathVariable 
            UUID organizationId,
            
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
        
        List<DatabaseSchema> schemas = databaseConfigService.listDatabaseSchemas(
            organizationId.toString(), forService, status, headerOrganizationId, tenantId
        );
        
        List<DatabaseSchemaResponseDto> response = schemas.stream()
            .map(databaseConfigMapper::toSchemaResponse)
            .toList();
        
        log.info("Successfully retrieved {} schemas for organization: {}", response.size(), organizationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/schemas/{schemaId}")
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
            @Parameter(description = "Organization ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidUUID(allowNull = false, fieldName = "Organization ID")
            @PathVariable 
            UUID organizationId,
            
            @Parameter(description = "Schema ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_SCHEMA_ID)
            @ValidUUID(allowNull = false, fieldName = "Schema ID")
            @PathVariable 
            UUID schemaId,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Getting schema details for organization: {}, schema: {}", organizationId, schemaId);
        
        DatabaseSchema schema = databaseConfigService.getDatabaseSchema(
            organizationId.toString(), schemaId.toString()
        );
        
        DatabaseSchemaResponseDto response = databaseConfigMapper.toSchemaResponse(schema);
        
        log.info("Successfully retrieved schema details: {} for organization: {}", schemaId, organizationId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/schemas/{schemaId}")
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
            @Parameter(description = "Organization ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidUUID(allowNull = false, fieldName = "Organization ID")
            @PathVariable 
            UUID organizationId,
            
            @Parameter(description = "Schema ID to remove", required = true, example = OrganizationSwaggerConstants.EXAMPLE_SCHEMA_ID)
            @ValidUUID(allowNull = false, fieldName = "Schema ID")
            @PathVariable 
            UUID schemaId,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Removing database schema for organization: {}, schema: {}", organizationId, schemaId);
        
        databaseConfigService.removeDatabaseSchema(
            organizationId.toString(), schemaId.toString()
        );
        
        log.info("Successfully removed schema: {} from organization: {}", schemaId, organizationId);
        return ResponseEntity.noContent().build();
    }
}