package com.codzs.dto.organization.response;

import com.codzs.constant.organization.OrganizationStatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for organization summary responses.
 * Contains minimal organization information for list views and autocomplete.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Organization summary response for list views")
public class OrganizationSummaryResponseDto {

    @Schema(description = "Organization unique identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @Schema(description = "Organization name", example = "Acme Corporation")
    private String name;

    @Schema(description = "Organization abbreviation", example = "ACME")
    private String abbr;

    @Schema(description = "Organization display name", example = "Acme Corporation Inc.")
    private String displayName;

    @Schema(description = "Organization status", example = "ACTIVE")
    private OrganizationStatusEnum status;

    @Schema(description = "Organization type", example = "ENTERPRISE")
    private String organizationType;

    @Schema(description = "Primary billing contact email", example = "billing@acme.com")
    private String billingEmail;

    @Schema(description = "Parent organization ID", example = "550e8400-e29b-41d4-a716-446655440001")
    private String parentOrganizationId;

    // Custom constructor for convenience
    public OrganizationSummaryResponseDto(String id, String name, String abbr, String displayName, 
                                         OrganizationStatusEnum status, String organizationType) {
        this.id = id;
        this.name = name;
        this.abbr = abbr;
        this.displayName = displayName;
        this.status = status;
        this.organizationType = organizationType;
    }
}