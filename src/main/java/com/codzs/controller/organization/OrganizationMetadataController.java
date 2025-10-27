package com.codzs.controller.organization;

import com.codzs.constant.organization.OrganizationSwaggerConstants;
import com.codzs.dto.organization.request.OrganizationMetadataRequestDto;
import com.codzs.dto.organization.response.OrganizationMetadataResponseDto;
import com.codzs.entity.organization.OrganizationMetadata;
import com.codzs.framework.annotation.header.CommonHeaders;
import com.codzs.framework.constant.HeaderConstant;
import com.codzs.framework.validation.annotation.ValidUUID;
import com.codzs.mapper.organization.OrganizationMetadataMapper;
import com.codzs.service.organization.OrganizationMetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/metadata")
@RequiredArgsConstructor
@Validated
@Tag(name = "Organization Metadata Management", description = "APIs for managing organization metadata including industry, size, and custom attributes")
public class OrganizationMetadataController {

    private final OrganizationMetadataService organizationMetadataService;
    private final OrganizationMetadataMapper organizationMetadataMapper;

    @GetMapping
    @CommonHeaders
    @Operation(
        summary = "Get organization metadata",
        description = "Retrieves metadata information for the specified organization"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Metadata retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizationMetadataResponseDto.class))
        ),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrganizationMetadataResponseDto> getOrganizationMetadata(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @NotNull(message = "Organization ID is required") 
            @ValidUUID(message = "Invalid Organization ID format")
            @PathVariable 
            UUID organizationId,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) {
        
        log.info("Getting metadata for organization: {}", organizationId);
        
        OrganizationMetadata metadata = organizationMetadataService.getOrganizationMetadata(
            organizationId.toString()
        );
        
        OrganizationMetadataResponseDto response = organizationMetadataMapper.toResponse(metadata);
        
        log.info("Successfully retrieved metadata for organization: {}", organizationId);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @CommonHeaders
    @Operation(
        summary = "Update organization metadata",
        description = "Updates metadata information for the specified organization (full update)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Metadata updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid metadata values"),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> updateOrganizationMetadata(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @NotNull(message = "Organization ID is required") 
            @ValidUUID(message = "Invalid Organization ID format")
            @PathVariable 
            UUID organizationId,
            
            @Parameter(description = "Metadata update request", required = true)
            @Valid 
            @RequestBody 
            OrganizationMetadataRequestDto request,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) {
        
        log.info("Updating metadata for organization: {}, industry: {}, size: {}", 
            organizationId, request.getIndustry(), request.getSize());
        
        OrganizationMetadata metadataEntity = organizationMetadataMapper.toEntity(request);
        organizationMetadataService.updateOrganizationMetadata(
            organizationId.toString(), metadataEntity
        );
        
        log.info("Successfully updated metadata for organization: {}", organizationId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    @CommonHeaders
    @Operation(
        summary = "Partially update organization metadata",
        description = "Updates specific metadata fields for the specified organization (partial update)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Metadata updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid metadata values"),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> patchOrganizationMetadata(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @NotNull(message = "Organization ID is required") 
            @ValidUUID(message = "Invalid Organization ID format")
            @PathVariable 
            UUID organizationId,
            
            @Parameter(description = "Partial metadata update request", required = true)
            @Valid 
            @RequestBody 
            OrganizationMetadataRequestDto request,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) {
        
        log.info("Partially updating metadata for organization: {}", organizationId);
        
        organizationMetadataService.updateIndustry(organizationId.toString(), request.getIndustry());
        organizationMetadataService.updateSize(organizationId.toString(), request.getSize());
        
        log.info("Successfully partially updated metadata for organization: {}", organizationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @CommonHeaders
    @Operation(
        summary = "Reset organization metadata",
        description = "Resets organization metadata to default values"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Metadata reset successfully"),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> resetOrganizationMetadata(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @NotNull(message = "Organization ID is required") 
            @ValidUUID(message = "Invalid Organization ID format")
            @PathVariable 
            UUID organizationId,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) {
        
        log.info("Resetting metadata for organization: {}", organizationId);
        
        organizationMetadataService.updateIndustry(organizationId.toString(), null);
        organizationMetadataService.updateSize(organizationId.toString(), null);
        
        log.info("Successfully reset metadata for organization: {}", organizationId);
        return ResponseEntity.noContent().build();
    }
}