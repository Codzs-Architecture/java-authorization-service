package com.codzs.dto.organization.response;

import com.codzs.constant.organization.OrganizationSchemaConstants;
import com.codzs.constant.organization.OrganizationStatusEnum;
import com.codzs.dto.domain.response.DomainResponseDto;
import com.codzs.framework.constant.CommonConstants;
import com.codzs.framework.dto.BaseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
@JsonPropertyOrder({"id", "name", "abbr", "displayName", "description", 
                   "organizationType", "billingEmail", "expiresDate", "database", 
                   "setting", "metadata", "domains", "ownerUserIds", "parentOrganizationId", "status", 
                   "lastModifiedDate", "lastModifiedBy", "createdDate", "createdBy"})
@Schema(description = OrganizationSchemaConstants.ORG_RESPONSE_DESCRIPTION)
public class OrganizationResponseDto extends BaseDto {

    @Schema(description = OrganizationSchemaConstants.ORG_ID_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ID)
    private String id;

    @Schema(description = OrganizationSchemaConstants.ORG_NAME_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_NAME)
    private String name;

    @Schema(description = OrganizationSchemaConstants.ORG_ABBR_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_ABBR)
    private String abbr;

    @Schema(description = OrganizationSchemaConstants.ORG_DISPLAY_NAME_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_DISPLAY_NAME)
    private String displayName;

    @Schema(description = OrganizationSchemaConstants.ORG_DESCRIPTION_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_DESCRIPTION)
    private String description;

    @Schema(description = OrganizationSchemaConstants.ORG_TYPE_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_ORGANIZATION_TYPE)
    private String organizationType;

    @Schema(description = OrganizationSchemaConstants.BILLING_EMAIL_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_BILLING_EMAIL)
    private String billingEmail;

    @Schema(description = OrganizationSchemaConstants.EXPIRY_DATE_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_EXPIRY_DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.ISO_INSTANT_PATTERN)
    private Instant expiresDate;

    @Schema(description = OrganizationSchemaConstants.DATABASE_RESPONSE_DESCRIPTION)
    private DatabaseConfigResponseDto database;

    @Schema(description = OrganizationSchemaConstants.SETTING_RESPONSE_DESCRIPTION)
    private OrganizationSettingResponseDto setting;

    @Schema(description = OrganizationSchemaConstants.METADATA_RESPONSE_DESCRIPTION)
    private OrganizationMetadataResponseDto metadata;

    @Schema(description = OrganizationSchemaConstants.DOMAINS_RESPONSE_DESCRIPTION)
    private List<DomainResponseDto> domains;

    @Schema(description = OrganizationSchemaConstants.OWNER_USER_IDS_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_USER_ID_LIST)
    private List<String> ownerUserIds;

    @Schema(description = OrganizationSchemaConstants.PARENT_ORG_ID_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_PARENT_ORG_ID)
    private String parentOrganizationId;

    @Schema(description = OrganizationSchemaConstants.STATUS_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_PLAN_ACTIVE)
    private OrganizationStatusEnum status;
}