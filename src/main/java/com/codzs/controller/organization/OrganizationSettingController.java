package com.codzs.controller.organization;

import com.codzs.constant.organization.OrganizationConstants;
import com.codzs.constant.organization.OrganizationSchemaConstants;
import com.codzs.dto.organization.request.OrganizationSettingRequestDto;
import com.codzs.dto.organization.response.OrganizationSettingResponseDto;
import com.codzs.entity.organization.OrganizationSetting;
import com.codzs.framework.annotation.header.CommonHeaders;
import com.codzs.framework.constant.HeaderConstant;
import com.codzs.mapper.organization.OrganizationSettingMapper;
import com.codzs.service.organization.OrganizationSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/settings")
@RequiredArgsConstructor
@Validated
@Tag(name = "Organization Settings Management", description = "APIs for managing organization-specific settings and configurations")
public class OrganizationSettingController {

    private final OrganizationSettingService organizationSettingService;
    private final OrganizationSettingMapper organizationSettingMapper;

    @GetMapping
    @CommonHeaders
    @Operation(
        summary = "Get organization settings",
        description = "Retrieves settings for the specified organization"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Settings retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizationSettingResponseDto.class))
        ),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrganizationSettingResponseDto> getOrganizationSetting(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
            @NotNull(message = "Organization ID is required") 
            @NotBlank(message = "Organization ID cannot be blank") 
            @PathVariable 
            String organizationId,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Getting settings for organization: {}", organizationId);
        
        return organizationSettingService.getOrganizationSetting(organizationId)
                .map(setting -> {
                    OrganizationSettingResponseDto response = organizationSettingMapper.toResponse(setting);
                    
                    log.info("Successfully retrieved settings for organization: {}", organizationId);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    log.warn("Organization settings not found for organization: {}", organizationId);
                    return ResponseEntity.notFound().build();
                });
    }

    @PutMapping
    @CommonHeaders
    @Operation(
        summary = "Update organization settings",
        description = "Updates ALL organization settings, replacing existing settings with the provided ones"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Settings updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data or setting values"),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> updateOrganizationSetting(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
            @NotNull(message = "Organization ID is required")
            @NotBlank(message = "Organization ID cannot be blank")
            @PathVariable
            String organizationId,
            
            @Parameter(description = "Complete settings update request", required = true)
            @Valid 
            @RequestBody 
            OrganizationSettingRequestDto request,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Updating settings for organization: {}", organizationId);
        
        OrganizationSetting settingEntity = organizationSettingMapper.toEntity(request);
        organizationSettingService.updateOrganizationSetting(organizationId, settingEntity);
        
        log.info("Successfully updated settings for organization: {}", organizationId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{settingKey}")
    @CommonHeaders
    @Operation(
        summary = "Update single organization setting",
        description = "Updates a specific organization setting by key"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Setting updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data or setting key/value"),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> updateSingleOrganizationSetting(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
            @NotNull(message = "Organization ID is required") 
            @NotBlank(message = "Organization ID cannot be blank") 
            @PathVariable 
            String organizationId,
            
            @Parameter(description = "Setting key to update", required = true, example = OrganizationConstants.SETTING_LANGUAGE)
            @NotBlank(message = "Setting key is required") 
            @PathVariable 
            String settingKey,
            
            @Parameter(description = "Setting value", required = true, example = "en-US")
            @NotBlank(message = "Setting value is required") 
            @RequestParam(value = "value") 
            String settingValue,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Updating single setting '{}' for organization: {} with value: {}", settingKey, organizationId, settingValue);
        
        organizationSettingService.updateSettingValue(organizationId, settingKey, settingValue);
        
        log.info("Successfully updated setting '{}' for organization: {}", settingKey, organizationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{settingKey}")
    @CommonHeaders
    @Operation(
        summary = "Get single organization setting",
        description = "Retrieves a specific organization setting by key"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Setting retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))
        ),
        @ApiResponse(responseCode = "404", description = "Organization or setting not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Object> getSingleOrganizationSetting(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
            @NotNull(message = "Organization ID is required")
            @NotBlank(message = "Organization ID cannot be blank") 
            @PathVariable
            String organizationId,
            
            @Parameter(description = "Setting key to retrieve", required = true, example = OrganizationConstants.SETTING_LANGUAGE)
            @NotBlank(message = "Setting key is required") 
            @PathVariable 
            String settingKey,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Getting setting '{}' for organization: {}", settingKey, organizationId);
        
        return organizationSettingService.getSettingValue(organizationId, settingKey)
                .map(settingValue -> {
                    log.info("Successfully retrieved setting '{}' for organization: {}", settingKey, organizationId);
                    return ResponseEntity.ok((Object) settingValue);
                })
                .orElseGet(() -> {
                    log.warn("Setting '{}' not found for organization: {}", settingKey, organizationId);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping("/reset")
    @CommonHeaders
    @Operation(
        summary = "Reset organization settings to defaults",
        description = "Resets all organization settings to their default values"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Settings reset successfully"),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> resetOrganizationSetting(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
            @NotNull(message = "Organization ID is required") 
            @NotBlank(message = "Organization ID cannot be blank") 
            @PathVariable 
            String organizationId,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Resetting settings to defaults for organization: {}", organizationId);
        
        organizationSettingService.resetToDefaultSetting(organizationId);
        
        log.info("Successfully reset settings for organization: {}", organizationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/defaults")
    @CommonHeaders
    @Operation(
        summary = "Get default organization settings",
        description = "Retrieves the default settings values for organization configuration"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Default settings retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizationSettingResponseDto.class))
        ),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrganizationSettingResponseDto> getDefaultOrganizationSetting(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
            @NotNull(message = "Organization ID is required") 
            @NotBlank(message = "Organization ID cannot be blank") 
            @PathVariable 
            String organizationId,
            
            @Parameter(description = "Organization type for defaults", example = "ENTERPRISE")
            @RequestParam(value = "organizationType", required = false, defaultValue = "STANDARD") 
            String organizationType,
            
            @Parameter(description = "Country code for defaults", example = "US")
            @RequestParam(value = OrganizationConstants.SETTING_COUNTRY, required = false, defaultValue = "US") 
            String country,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Getting default settings for organization: {}, type: {}, country: {}", organizationId, organizationType, country);
        
        OrganizationSetting defaultSetting = organizationSettingService.createDefaultSetting(organizationType, country);
        
        OrganizationSettingResponseDto response = organizationSettingMapper.toResponse(defaultSetting);
        
        log.info("Successfully retrieved default settings");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/keys")
    @CommonHeaders
    @Operation(
        summary = "Get available setting keys",
        description = "Retrieves all available setting keys for organization configuration"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Setting keys retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
        ),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<String>> getAvailableSettingKeys(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
            @NotNull(message = "Organization ID is required") 
            @NotBlank(message = "Organization ID cannot be blank") 
            @PathVariable 
            String organizationId,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Getting available setting keys for organization: {}", organizationId);
        
        String[] availableKeys = organizationSettingService.getAvailableSettingKeys();
        List<String> response = List.of(availableKeys);
        
        log.info("Successfully retrieved {} setting keys", response.size());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    @CommonHeaders
    @Operation(
        summary = "Validate organization settings",
        description = "Validates the provided organization settings"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Validation completed"),
        @ApiResponse(responseCode = "400", description = "Validation failed with invalid values"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> validateOrganizationSetting(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
            @NotNull(message = "Organization ID is required") 
            @NotBlank(message = "Organization ID cannot be blank") 
            @PathVariable 
            String organizationId,
            
            @Parameter(description = "Settings validation request", required = true)
            @Valid 
            @RequestBody 
            OrganizationSettingRequestDto request,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Validating settings for organization: {}", organizationId);
        
        OrganizationSetting settingEntity = organizationSettingMapper.toEntity(request);
        boolean isValid = organizationSettingService.validateOrganizationSetting(organizationId, settingEntity);
        
        log.info("Successfully validated settings for organization: {}, isValid: {}", organizationId, isValid);
        return ResponseEntity.ok(isValid);
    }
}