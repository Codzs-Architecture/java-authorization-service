package com.codzs.controller.organization;

import com.codzs.constant.organization.OrganizationSchemaConstants;
import com.codzs.dto.organization.request.OrganizationSettingRequestDto;
import com.codzs.dto.organization.response.OrganizationSettingResponseDto;
import com.codzs.entity.organization.OrganizationSetting;
import com.codzs.framework.annotation.header.CommonHeaders;
import com.codzs.framework.constant.HeaderConstant;
import com.codzs.logger.constant.LoggerConstant;
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
            @PathVariable("organizationId")
            String organizationId,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId) 
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

    @PatchMapping()
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
            @PathVariable("organizationId")
            String organizationId,
            
            @Parameter(description = "Setting update request", required = true)
            @Valid 
            @RequestBody 
            OrganizationSettingRequestDto request,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId) 
    {
        
        log.info("Updating setting(s) for organization: {}", organizationId);
        
        OrganizationSetting settingEntity = organizationSettingMapper.toEntity(request);
        organizationSettingService.updateSettingValue(organizationId, settingEntity);
        
        log.info("Successfully updated setting(s) for organization: {}", organizationId);
        return ResponseEntity.noContent().build();
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
            @PathVariable("organizationId")
            String organizationId,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId) 
    {
        
        log.info("Resetting settings to defaults for organization: {}", organizationId);
        
        organizationSettingService.resetToDefaultSetting(organizationId);
        
        log.info("Successfully reset settings for organization: {}", organizationId);
        return ResponseEntity.noContent().build();
    }
}