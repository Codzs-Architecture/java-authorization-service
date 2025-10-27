package com.codzs.controller.organization;

import com.codzs.constant.organization.OrganizationSwaggerConstants;
import com.codzs.dto.organization.request.DomainRequestDto;
import com.codzs.dto.organization.response.DomainResponseDto;
import com.codzs.entity.domain.Domain;
import com.codzs.framework.annotation.header.CommonHeaders;
import com.codzs.framework.constant.HeaderConstant;
import com.codzs.framework.validation.annotation.ValidUUID;
import com.codzs.mapper.organization.OrganizationDomainMapper;
import com.codzs.service.organization.OrganizationDomainService;
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
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/domains")
@RequiredArgsConstructor
@Validated
@Tag(name = "Organization Domain Management", description = "APIs for managing organization domains and domain verification")
public class OrganizationDomainController {

    private final OrganizationDomainService organizationDomainService;
    private final OrganizationDomainMapper organizationDomainMapper;

    @PostMapping
    @CommonHeaders
    @Operation(
        summary = "Add domain to organization",
        description = "Adds a new domain to the organization for verification and ownership"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Domain added successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid domain name or verification method"),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "409", description = "Domain already exists or is already assigned to another organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<DomainResponseDto>> addDomainToOrganization(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidUUID(allowNull = false, fieldName = "Organization ID")
            @PathVariable 
            UUID organizationId,
            
            @Parameter(description = "Domain addition request", required = true)
            @Valid 
            @RequestBody 
            DomainRequestDto request,
            
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) {
        
        log.info("Adding domain '{}' to organization: {} with verification method: {}", 
            request.getName(), organizationId, request.getVerificationMethod());
        
        Domain domainEntity = organizationDomainMapper.toEntity(request);
        List<Domain> domainsAfterAdd = organizationDomainService.addDomainToOrganization(
            organizationId.toString(), domainEntity
        );
        
        List<DomainResponseDto> response = domainsAfterAdd.stream()
            .map(organizationDomainMapper::toResponse)
            .toList();
        
        log.info("Successfully added domain '{}' to organization: {}, total domains: {}", 
            request.getName(), organizationId, response.size());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @CommonHeaders
    @Operation(
        summary = "List organization domains",
        description = "Retrieves all domains associated with the organization with optional filtering by verification and primary status"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Domains retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
        ),
        @ApiResponse(responseCode = "404", description = "Organization not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<DomainResponseDto>> listOrganizationDomains(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidUUID(allowNull = false, fieldName = "Organization ID")
            @PathVariable 
            UUID organizationId,
            
            @Parameter(description = "Filter by verification status", example = "true")
            @RequestParam(value = "verified", required = false) 
            Boolean verified,
            
            @Parameter(description = "Filter by primary status", example = "true")
            @RequestParam(value = "primary", required = false) 
            Boolean primary,
            
            @Parameter(description = "Organization context for multi-tenant support", example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            
            @Parameter(description = "Tenant context for multi-tenant support", example = OrganizationSwaggerConstants.EXAMPLE_TENANT_ID)
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            
            @Parameter(description = "Correlation ID for request tracing", example = OrganizationSwaggerConstants.EXAMPLE_CORRELATION_ID)
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) {
        
        log.info("Listing domains for organization: {}, verified: {}, primary: {}", 
            organizationId, verified, primary);
        
        List<Domain> domains = organizationDomainService.getDomainsForEntity(
            organizationId.toString()
        );
        
        // Apply filters if specified
        if (verified != null) {
            domains = domains.stream()
                .filter(domain -> verified.equals(domain.getIsVerified()))
                .toList();
        }
        
        if (primary != null) {
            domains = domains.stream()
                .filter(domain -> primary.equals(domain.getIsPrimary()))
                .toList();
        }
        
        List<DomainResponseDto> response = domains.stream()
            .map(organizationDomainMapper::toResponse)
            .toList();
        
        log.info("Successfully retrieved {} domains for organization: {}", 
            response.size(), organizationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{domainId}")
    @CommonHeaders
    @Operation(
        summary = "Get domain details",
        description = "Retrieves detailed information about a specific domain"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Domain retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DomainResponseDto.class))
        ),
        @ApiResponse(responseCode = "404", description = "Organization or domain not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DomainResponseDto> getDomainDetails(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidUUID(allowNull = false, fieldName = "Organization ID")
            @PathVariable 
            UUID organizationId,
            
            @Parameter(description = "Domain ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_DOMAIN_ID)
            @ValidUUID(allowNull = false, fieldName = "Domain ID")
            @PathVariable 
            UUID domainId,
            
            @Parameter(description = "Organization context for multi-tenant support", example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            
            @Parameter(description = "Tenant context for multi-tenant support", example = OrganizationSwaggerConstants.EXAMPLE_TENANT_ID)
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            
            @Parameter(description = "Correlation ID for request tracing", example = OrganizationSwaggerConstants.EXAMPLE_CORRELATION_ID)
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) {
        
        log.info("Getting domain details for organization: {}, domain: {}", organizationId, domainId);
        
        Domain domain = organizationDomainService.getDomainInEntity(
            organizationId.toString(), domainId.toString()
        );
        
        DomainResponseDto response = organizationDomainMapper.toResponse(domain);
        
        log.info("Successfully retrieved domain: {} for organization: {}", domainId, organizationId);
        return ResponseEntity.ok(response);
    }

    // @PutMapping("/{domainId}/verify")
    // @Operation(
    //     summary = "Verify domain ownership",
    //     description = "Initiates or performs domain ownership verification based on the verification method"
    // )
    // @ApiResponses(value = {
    //     @ApiResponse(
    //         responseCode = "200",
    //         description = "Domain verification completed or initiated successfully",
    //         content = @Content(mediaType = "application/json", schema = @Schema(implementation = DomainResponseDto.class))
    //     ),
    //     @ApiResponse(responseCode = "400", description = "Domain verification failed"),
    //     @ApiResponse(responseCode = "404", description = "Organization or domain not found"),
    //     @ApiResponse(responseCode = "403", description = "Access denied to organization"),
    //     @ApiResponse(responseCode = "409", description = "Domain already verified or invalid verification state"),
    //     @ApiResponse(responseCode = "500", description = "Internal server error")
    // })
    // public ResponseEntity<DomainResponseDto> verifyDomainOwnership(
    //         @Parameter(description = "Organization ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
    //         @ValidUUID(allowNull = false, fieldName = "Organization ID")
    //         @PathVariable 
    //         UUID organizationId,
            
    //         @Parameter(description = "Domain ID to verify", required = true, example = OrganizationSwaggerConstants.EXAMPLE_DOMAIN_ID)
    //         @PathVariable 
    //         UUID domainId,
            
    //         @Parameter(description = "Organization context for multi-tenant support", example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
    //         @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            
    //         @Parameter(description = "Tenant context for multi-tenant support", example = OrganizationSwaggerConstants.EXAMPLE_TENANT_ID)
    //         @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            
    //         @Parameter(description = "Correlation ID for request tracing", example = OrganizationSwaggerConstants.EXAMPLE_CORRELATION_ID)
    //         @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) {
        
    //     log.info("Verifying domain ownership for organization: {}, domain: {}", organizationId, domainId);
        
    //     Domain verifiedDomain = organizationDomainService.verifyDomainOwnership(
    //         organizationId, domainId, headerOrganizationId, tenantId, correlationId
    //     );
        
    //     DomainResponseDto response = organizationDomainMapper.toResponse(verifiedDomain);
        
    //     log.info("Successfully verified domain: {} for organization: {}, isVerified: {}", 
    //         domainId, organizationId, verifiedDomain.getIsVerified());
    //     return ResponseEntity.ok(response);
    // }

    @DeleteMapping("/{domainId}")
    @CommonHeaders
    @Operation(
        summary = "Remove domain from organization",
        description = "Removes a domain from the organization (soft delete)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Domain removed successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
        ),
        @ApiResponse(responseCode = "404", description = "Organization or domain not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "409", description = "Cannot remove primary domain if it's the only domain"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<DomainResponseDto>> removeDomainFromOrganization(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidUUID(allowNull = false, fieldName = "Organization ID")
            @PathVariable 
            UUID organizationId,
            
            @Parameter(description = "Domain ID to remove", required = true, example = OrganizationSwaggerConstants.EXAMPLE_DOMAIN_ID)
            @PathVariable 
            UUID domainId,
            
            @Parameter(description = "Organization context for multi-tenant support", example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            
            @Parameter(description = "Tenant context for multi-tenant support", example = OrganizationSwaggerConstants.EXAMPLE_TENANT_ID)
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            
            @Parameter(description = "Correlation ID for request tracing", example = OrganizationSwaggerConstants.EXAMPLE_CORRELATION_ID)
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) {
        
        log.info("Removing domain from organization: {}, domain: {}", organizationId, domainId);
        
        List<Domain> remainingDomains = organizationDomainService.removeDomainFromOrganization(
            organizationId.toString(), domainId.toString()
        );
        
        List<DomainResponseDto> response = remainingDomains.stream()
            .map(organizationDomainMapper::toResponse)
            .toList();
        
        log.info("Successfully removed domain: {} from organization: {}, remaining domains: {}", 
            domainId, organizationId, response.size());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{domainId}/primary")
    @CommonHeaders
    @Operation(
        summary = "Set domain as primary",
        description = "Sets the specified domain as the primary domain for the organization"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Primary domain updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
        ),
        @ApiResponse(responseCode = "400", description = "Domain must be verified to be set as primary"),
        @ApiResponse(responseCode = "404", description = "Organization or domain not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "409", description = "Domain is already primary"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<DomainResponseDto>> setPrimaryDomain(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidUUID(allowNull = false, fieldName = "Organization ID")
            @PathVariable 
            UUID organizationId,
            
            @Parameter(description = "Domain ID to set as primary", required = true, example = OrganizationSwaggerConstants.EXAMPLE_DOMAIN_ID)
            @PathVariable 
            UUID domainId,
            
            @Parameter(description = "Organization context for multi-tenant support", example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            
            @Parameter(description = "Tenant context for multi-tenant support", example = OrganizationSwaggerConstants.EXAMPLE_TENANT_ID)
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            
            @Parameter(description = "Correlation ID for request tracing", example = OrganizationSwaggerConstants.EXAMPLE_CORRELATION_ID)
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) {
        
        log.info("Setting domain as primary for organization: {}, domain: {}", organizationId, domainId);
        
        List<Domain> updatedDomains = organizationDomainService.setPrimaryDomain(
            organizationId.toString(), domainId.toString()
        );
        
        List<DomainResponseDto> response = updatedDomains.stream()
            .map(organizationDomainMapper::toResponse)
            .toList();
        
        log.info("Successfully set domain: {} as primary for organization: {}, total domains: {}", 
            domainId, organizationId, response.size());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{domainId}/resend-verification")
    @CommonHeaders
    @Operation(
        summary = "Resend domain verification instructions",
        description = "Resends verification instructions for the specified domain"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Verification instructions sent successfully"),
        @ApiResponse(responseCode = "404", description = "Organization or domain not found"),
        @ApiResponse(responseCode = "403", description = "Access denied to organization"),
        @ApiResponse(responseCode = "409", description = "Domain is already verified"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> resendDomainVerificationInstructions(
            @Parameter(description = "Organization ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @ValidUUID(allowNull = false, fieldName = "Organization ID")
            @PathVariable 
            UUID organizationId,
            
            @Parameter(description = "Domain ID", required = true, example = OrganizationSwaggerConstants.EXAMPLE_DOMAIN_ID)
            @ValidUUID(allowNull = false, fieldName = "Domain ID")
            @PathVariable 
            UUID domainId,
            
            @Parameter(description = "Organization context for multi-tenant support", example = OrganizationSwaggerConstants.EXAMPLE_ORGANIZATION_ID)
            @RequestHeader(value = HeaderConstant.HEADER_ORGANIZATION_ID, required = false) String headerOrganizationId,
            
            @Parameter(description = "Tenant context for multi-tenant support", example = OrganizationSwaggerConstants.EXAMPLE_TENANT_ID)
            @RequestHeader(value = HeaderConstant.HEADER_TENANT_ID, required = false) String tenantId,
            
            @Parameter(description = "Correlation ID for request tracing", example = OrganizationSwaggerConstants.EXAMPLE_CORRELATION_ID)
            @RequestHeader(value = HeaderConstant.HEADER_CORRELATION_ID, required = false) String correlationId) {
        
        log.info("Resending verification instructions for organization: {}, domain: {}", organizationId, domainId);
        
        organizationDomainService.getDomainVerificationInstructions(
            organizationId.toString(), domainId.toString()
        );
        
        log.info("Successfully resent verification instructions for domain: {} in organization: {}", 
            domainId, organizationId);
        return ResponseEntity.noContent().build();
    }
}