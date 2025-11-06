package com.codzs.controller.organization;

import com.codzs.constant.organization.OrganizationSchemaConstants;
import com.codzs.dto.organization.request.OrganizationPlanRequestDto;
import com.codzs.dto.organization.response.OrganizationPlanResponseDto;
import com.codzs.entity.organization.OrganizationPlan;
import com.codzs.framework.annotation.header.CommonHeaders;
import com.codzs.framework.constant.HeaderConstant;
import com.codzs.framework.constant.PaginationConstant;
import com.codzs.mapper.organization.OrganizationPlanMapper;
import com.codzs.service.organization.OrganizationPlanService;
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
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Slf4j
@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/plan")
@RequiredArgsConstructor
@Validated
@Tag(name = "Organization Plan Management", description = "APIs for managing organization subscription plans and billing")
public class OrganizationPlanController {

    private final OrganizationPlanService organizationPlanService;
    private final OrganizationPlanMapper organizationPlanMapper;

    @GetMapping
    @CommonHeaders
    @Operation(
        summary = "Get organization plan",
        description = "Retrieves current plan details and subscription information for the organization"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Plan information retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizationPlanResponseDto.class))
        ),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrganizationPlanResponseDto> getCurrentActivePlan(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
            @NotNull(message = "Organization ID is required") 
            @PathVariable 
            String organizationId,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) {
        
        log.info("Getting plan for organization: {}", organizationId);
        
        OrganizationPlan organizationPlan = organizationPlanService.getCurrentActivePlan(
            organizationId
        );
        
        OrganizationPlanResponseDto response = organizationPlanMapper.toResponse(organizationPlan);
        
        log.info("Successfully retrieved plan for organization: {}", organizationId);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @CommonHeaders
    @Operation(
        summary = "Update organization plan",
        description = "Updates the organization's subscription plan with validation and transition logic"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Plan updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizationPlanResponseDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid plan or transition not allowed"),
        @ApiResponse(responseCode = "404", description = "Organization or plan not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "409", description = "Plan change conflict or billing issue"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrganizationPlanResponseDto> updateOrganizationPlan(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
            @NotNull(message = "Organization ID is required") 
            @PathVariable 
            String organizationId,
            
            @Parameter(description = "Plan update request", required = true)
            @Valid 
            @RequestBody 
            OrganizationPlanRequestDto request,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) {
        
        log.info("Updating plan for organization: {}, newPlanId: {}, comment: {}", 
            organizationId, request.getPlanId(), request.getComment());
        
        OrganizationPlan planEntity = organizationPlanMapper.toEntity(request);
        OrganizationPlan updatedPlan = organizationPlanService.changeOrganizationPlan(
            organizationId, planEntity);
        
        OrganizationPlanResponseDto response = organizationPlanMapper.toResponse(updatedPlan);
        
        log.info("Successfully updated plan for organization: {} to plan: {}", 
            organizationId, request.getPlanId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    @CommonHeaders
    @Operation(
        summary = "Get plan change history",
        description = "Retrieves the complete plan change history for the organization"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Plan history retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<OrganizationPlanResponseDto>> getOrganizationPlanHistory(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
            @NotNull(message = "Organization ID is required") 
            @PathVariable 
            String organizationId,
            
            @Parameter(description = "Page number (1-based)", example = "1")
            @Min(value = 1, message = "Page number must be at least 1") 
            @RequestParam(value = "page", defaultValue = PaginationConstant.DEFAULT_PAGE_NUMBER_STR) 
            Integer page,
            
            @Parameter(description = "Items per page", example = "20")
            @Min(value = 1, message = "Limit must be at least 1") 
            @Max(value = 100, message = "Limit cannot exceed 100") 
            @RequestParam(value = "limit", defaultValue = PaginationConstant.DEFAULT_PAGE_SIZE_STR) 
            Integer limit,
            
            @Parameter(description = "Filter history from date (ISO 8601)", example = "2024-01-01")
            @RequestParam(value = "startDate", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            
            @Parameter(description = "Filter history to date (ISO 8601)", example = "2024-12-31")
            @RequestParam(value = "endDate", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) {
        
        log.info("Getting plan history for organization: {}, page: {}, limit: {}, startDate: {}, endDate: {}", 
            organizationId, page, limit, startDate, endDate);
        
        Pageable pageable = PageRequest.of(page - 1, limit);
        
        // Convert LocalDate to Instant for service layer
        Instant startInstant = startDate != null ? startDate.atStartOfDay(ZoneOffset.UTC).toInstant() : null;
        Instant endInstant = endDate != null ? endDate.atTime(23, 59, 59, 999_999_999).atZone(ZoneOffset.UTC).toInstant() : null;
        
        Page<OrganizationPlan> planHistoryPage = organizationPlanService.getOrganizationPlanHistory(
            organizationId, startInstant, endInstant, pageable
        );
        
        Page<OrganizationPlanResponseDto> response = planHistoryPage.map(organizationPlanMapper::toResponse);
        
        log.info("Successfully retrieved {} plan history records for organization: {}", 
            response.getNumberOfElements(), organizationId);
        return ResponseEntity.ok(response);
    }
}