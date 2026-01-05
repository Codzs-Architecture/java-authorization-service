package com.codzs.controller.organization;

import com.codzs.constant.organization.OrganizationSchemaConstants;
import com.codzs.dto.organization.request.DatabaseConfigRequestDto;
import com.codzs.dto.organization.response.DatabaseConfigResponseDto;
import com.codzs.entity.organization.DatabaseConfig;
import com.codzs.entity.organization.Organization;
import com.codzs.framework.annotation.header.CommonHeaders;
import com.codzs.framework.constant.HeaderConstant;
import com.codzs.framework.validation.annotation.ValidObjectId;
import com.codzs.logger.constant.LoggerConstant;
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
 
@Slf4j
@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/database")
@RequiredArgsConstructor
@Validated
@Tag(name = "Database Configuration Management", description = "APIs for managing organization-specific database configurations and connectivity")
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
            @Parameter(description = "Organization ID", required = true, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidObjectId(allowNull = false)
            @PathVariable("organizationId") 
            String organizationId,
            
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
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId)
    {
        
        log.info("Getting database configuration for organization: {}, includeConnectionString: {}, testConnection: {}", 
            organizationId, includeConnectionString, testConnection);
        
        return databaseConfigService.getDatabaseConfig(organizationId)
                .map(databaseConfig -> {
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
                })
                .orElseGet(() -> {
                    log.warn("Database configuration not found for organization: {}", organizationId);
                    return ResponseEntity.notFound().build();
                });
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
            @Parameter(description = "Organization ID", required = true, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidObjectId(allowNull = false)
            @PathVariable("organizationId") 
            String organizationId,
            
            @Parameter(description = "Database connection update request", required = true)
            @Valid 
            @RequestBody 
            DatabaseConfigRequestDto request,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId) 
    {

        log.info("Updating database connection for organization: {}", organizationId);
        
        DatabaseConfig databaseConfigEntity = databaseConfigMapper.toEntity(request);
        Organization organization = databaseConfigService.updateDatabaseConfig(
            organizationId, databaseConfigEntity.getConnectionString(), databaseConfigEntity.getCertificate()
        );
        
        DatabaseConfigResponseDto response = databaseConfigMapper.toResponse(organization.getDatabase());
        
        log.info("Successfully updated database connection for organization: {}", organizationId);
        return ResponseEntity.ok(response);
    }

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
            @Parameter(description = "Organization ID", required = true, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidObjectId(allowNull = false)
            @PathVariable("organizationId") 
            String organizationId,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId) 
    {
        
        log.info("Testing database connectivity for organization: {}", organizationId);
        
        boolean testResults = testConnection(organizationId);
        
        log.info("Successfully tested database connectivity for organization: {}", organizationId);
        return ResponseEntity.ok(testResults);
    }

    private boolean testConnection(String organizationId) {
        boolean testResults = false;
        try {
            log.info("Testing database connectivity for organization: {}", organizationId);
            testResults = databaseConfigService.testDatabaseConnection(organizationId);
            log.info("Database connectivity test completed for organization: {}", organizationId);
        } catch (Exception e) {
            log.error("Database connectivity test failed for organization: {}", organizationId, e);
        }

        return testResults;
    }
}