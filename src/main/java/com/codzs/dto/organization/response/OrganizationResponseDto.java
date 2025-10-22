package com.codzs.dto.organization.response;

import com.codzs.constant.organization.OrganizationStatusEnum;
import com.codzs.framework.constant.CommonConstants;
import com.codzs.framework.dto.BaseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

/**
 * DTO for organization responses.
 * Contains complete organization information with audit fields.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Organization response DTO")
public class OrganizationResponseDto extends BaseDto {

    @Schema(description = "Organization unique identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @Schema(description = "Organization name", example = "Acme Corporation")
    private String name;

    @Schema(description = "Organization abbreviation", example = "ACME")
    private String abbr;

    @Schema(description = "Organization display name", example = "Acme Corporation Inc.")
    private String displayName;

    @Schema(description = "Organization description", example = "Leading provider of enterprise solutions")
    private String description;

    @Schema(description = "Organization status", example = "ACTIVE")
    private OrganizationStatusEnum status;

    @Schema(description = "Organization type", example = "ENTERPRISE")
    private String organizationType;

    @Schema(description = "Primary billing contact email", example = "billing@acme.com")
    private String billingEmail;

    @Schema(description = "Organization expiration date", example = "2025-12-31T23:59:59Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.UTC_TIMESTAMP_PATTERN)
    private Instant expiresOn;

    @Schema(description = "Database configuration for the organization")
    private DatabaseConfigResponseDto database;

    @Schema(description = "Organization settings")
    private OrganizationSettingsResponseDto settings;

    @Schema(description = "Organization metadata")
    private OrganizationMetadataResponseDto metadata;

    @Schema(description = "Organization domains")
    private List<DomainResponseDto> domains;

    @Schema(description = "List of owner user IDs", example = "[\"550e8400-e29b-41d4-a716-446655440000\"]")
    private List<String> ownerUserIds;

    @Schema(description = "Parent organization ID", example = "550e8400-e29b-41d4-a716-446655440001")
    private String parentOrganizationId;
}