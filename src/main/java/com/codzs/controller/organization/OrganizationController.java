package com.codzs.controller.organization;

import com.codzs.constant.organization.OrganizationSwaggerConstants;
import com.codzs.dto.organization.request.OrganizationCreateRequestDto;
import com.codzs.dto.organization.request.OrganizationUpdateRequestDto;
import com.codzs.dto.organization.response.OrganizationResponseDto;
import com.codzs.dto.organization.response.OrganizationSummaryResponseDto;
import com.codzs.entity.organization.Organization;
import com.codzs.framework.annotation.header.CommonHeaders;
import com.codzs.framework.constant.HeaderConstant;
import com.codzs.framework.constant.PaginationConstant;
import com.codzs.framework.validation.annotation.ValidUUID;
import com.codzs.mapper.organization.OrganizationMapper;
import com.codzs.service.organization.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Validated
@Tag(name = "Organization Management", description = "APIs for managing organizations within the platform")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final OrganizationMapper organizationMapper;

    @PostMapping
    @CommonHeaders
    @Operation(
        summary = "Create a new organization",
        description = "Creates a new organization with automatic geolocation-based defaults and default plan assignment"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Organization created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizationResponseDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "409", description = "Organization name or abbreviation already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    }) 
    public ResponseEntity<OrganizationResponseDto> createOrganization(
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String organizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId,
            
            @Parameter(description = "Organization creation request", required = true)
            @Valid 
            @RequestBody 
            OrganizationCreateRequestDto request) 
    {
        
        log.info("Creating organization with name: {}, organizationType: {}", request.getName(), request.getOrganizationType());
        
        Organization organizationEntity = organizationMapper.toEntity(request);
        Organization createdOrganization = organizationService.createOrganization(organizationEntity);
        OrganizationResponseDto response = organizationMapper.toResponse(createdOrganization);
        
        log.info("Successfully created organization with ID: {}", createdOrganization.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{organizationId}")
    @CommonHeaders
    @Operation(
        summary = "Get organization details",
        description = "Retrieves detailed information about a specific organization including optional nested data"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Organization retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizationResponseDto.class))
        ),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrganizationResponseDto> getOrganization(
            @Parameter(description = "Organization ID to retrieve", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidUUID(allowNull = false, fieldName = "Organization ID")
            @PathVariable 
            UUID organizationId,
            
            @Parameter(description = "Additional data to include", example = "setting,domain,database,metadata")
            @RequestParam(value = "include", required = false) 
            List<String> include,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Retrieving organization with ID: {}, include: {}", organizationId, include);
        
        Organization organization = organizationService.getOrganizationById(organizationId.toString(), include);
        OrganizationResponseDto response = organizationMapper.toResponse(organization);
        
        log.info("Successfully retrieved organization: {}", organizationId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{organizationId}")
    @CommonHeaders
    @Operation(
        summary = "Update organization",
        description = "Updates all fields of an organization (full update)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Organization updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> updateOrganization(
            @Parameter(description = "Organization ID to update", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidUUID(allowNull = false, fieldName = "Organization ID") 
            @PathVariable 
            UUID organizationId,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId,
            
            @Parameter(description = "Organization update request", required = true)
            @Valid 
            @RequestBody 
            OrganizationUpdateRequestDto request) 
    {
        
        log.info("Updating organization with ID: {}", organizationId);
        
        Organization organizationEntity = organizationMapper.toUpdateEntity(request);
        organizationEntity.setId(organizationId.toString());
        organizationService.updateOrganization(organizationEntity);
        
        log.info("Successfully updated organization: {}", organizationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{organizationId}")
    @CommonHeaders
    @Operation(
        summary = "Delete organization",
        description = "Marks an organization as deleted (soft delete)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Organization deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "409", description = "Organization cannot be deleted due to dependencies"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteOrganization(
            @Parameter(description = "Organization ID to delete", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidUUID(allowNull = false, fieldName = "Organization ID")
            @PathVariable 
            UUID organizationId,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Deleting organization with ID: {}", organizationId);
        
        organizationService.deleteOrganization(organizationId.toString());
        
        log.info("Successfully deleted organization: {}", organizationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @CommonHeaders
    @Operation(
        summary = "List organizations",
        description = "Retrieves a paginated list of organizations with filtering and search capabilities"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Organizations retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<OrganizationSummaryResponseDto>> listOrganizations(
            @Parameter(description = "Page number (1-based)", example = "1")
            @RequestParam(value = "page", defaultValue = PaginationConstant.DEFAULT_PAGE_NUMBER_STR) 
            @Min(value = 1, message = "Page number must be at least 1") 
            Integer page,
            
            @Parameter(description = "Items per page", example = "20")
            @RequestParam(value = "limit", defaultValue = PaginationConstant.DEFAULT_PAGE_SIZE_STR) 
            @Min(value = 1, message = "Limit must be at least 1") 
            @Max(value = 100, message = "Limit cannot exceed 100") 
            Integer limit,
            
            @Parameter(description = "Filter by organization status", example = "ACTIVE")
            @RequestParam(value = "status", required = false) 
            String status,
            
            @Parameter(description = "Filter by organization type", example = "ENTERPRISE")
            @RequestParam(value = "organizationType", required = false) 
            String organizationType,
            
            @Parameter(description = "Filter by industry", example = "TECHNOLOGY")
            @RequestParam(value = "industry", required = false) 
            String industry,
            
            @Parameter(description = "Filter by organization size", example = "500+")
            @RequestParam(value = "size", required = false) 
            String size,
            
            @Parameter(description = "Search term for name and display name", example = "acme")
            @RequestParam(value = "search", required = false) 
            String search,
            
            @Parameter(description = "Sort field", example = "name")
            @RequestParam(value = "sortBy", defaultValue = PaginationConstant.DEFAULT_SORT_BY) 
            String sortBy,
            
            @Parameter(description = "Sort order", example = "asc")
            @RequestParam(value = "sortOrder", defaultValue = PaginationConstant.DEFAULT_SORT_ORDER) 
            String sortOrder,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) 
            String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) 
            String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) 
            String correlationId) 
    {
        
        log.info("Listing organizations - page: {}, limit: {}, search: {}", page, limit, search);
        
        // Create Pageable object from page and limit parameters
        Pageable pageable = PageRequest.of(page - 1, limit, 
            Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
        
        // Convert single values to lists for service method
        List<String> statuses = status != null ? List.of(status) : null;
        List<String> organizationTypes = organizationType != null ? List.of(organizationType) : null;
        List<String> industries = industry != null ? List.of(industry) : null;
        List<String> sizes = size != null ? List.of(size) : null;
        
        Page<Organization> organizationsPage = organizationService.listOrganizations(
            statuses, organizationTypes, industries, sizes, search, pageable
        );
        
        // Use autocomplete response for summary-like functionality
        Page<OrganizationSummaryResponseDto> response = organizationsPage.map(organizationMapper::toAutocompleteResponse);
        
        log.info("Successfully retrieved {} organizations out of {} total", 
            response.getNumberOfElements(), response.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/autocomplete")
    @CommonHeaders
    @Operation(
        summary = "Get organizations for autocomplete",
        description = "Retrieves a simplified list of organizations for autocomplete dropdown components"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Organizations retrieved successfully for autocomplete",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<OrganizationSummaryResponseDto>> getOrganizationsForAutocomplete(
            @Parameter(description = "Search query to filter organizations", example = "acme")
            @RequestParam(value = "q", required = false) 
            String query,
            
            @Parameter(description = "Filter by status (default: ACTIVE)", example = "ACTIVE,PENDING")
            @RequestParam(value = "status", required = false, defaultValue = "ACTIVE") 
            List<String> status,
            
            @Parameter(description = "Maximum number of results", example = "10")
            @RequestParam(value = "limit", defaultValue = PaginationConstant.DEFAULT_AUTOCOMPLETE_LIMIT_STR) 
            @Min(value = 1, message = "Limit must be at least 1") 
            @Max(value = 50, message = "Limit cannot exceed 50") 
            Integer limit,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Getting organizations for autocomplete - query: {}, status: {}, limit: {}", query, status, limit);
        
        // Create Pageable object for autocomplete
        Pageable pageable = PageRequest.of(0, limit);
        
        List<Organization> organizations = organizationService.getOrganizationsForAutocomplete(
            status, query, pageable
        );
        
        List<OrganizationSummaryResponseDto> response = organizations.stream()
            .map(organizationMapper::toAutocompleteResponse)
            .toList();
        
        log.info("Successfully retrieved {} organizations for autocomplete", response.size());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{organizationId}/activate")
    @CommonHeaders
    @Operation(
        summary = "Activate organization",
        description = "Activates a previously deactivated organization"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Organization activated successfully"),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "409", description = "Invalid status transition"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> activateOrganization(
            @Parameter(description = "Organization ID to activate", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidUUID(allowNull = false, fieldName = "Organization ID")
            @PathVariable 
            UUID organizationId,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Activating organization with ID: {}", organizationId);
        
        organizationService.activateOrganization(organizationId.toString());
        
        log.info("Successfully activated organization: {}", organizationId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{organizationId}/deactivate")
    @CommonHeaders
    @Operation(
        summary = "Deactivate organization", 
        description = "Deactivates an active organization without deletion"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Organization deactivated successfully"),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "409", description = "Invalid status transition"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deactivateOrganization(
            @Parameter(description = "Organization ID to deactivate", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidUUID(allowNull = false, fieldName = "Organization ID")
            @PathVariable 
            UUID organizationId,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) 
    {
        
        log.info("Deactivating organization with ID: {}", organizationId);
        
        organizationService.deactivateOrganization(organizationId.toString());
        
        log.info("Successfully deactivated organization: {}", organizationId);
        return ResponseEntity.noContent().build();
    }
}