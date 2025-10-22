package com.codzs.dto.organization.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for organization metadata in organization responses.
 * Contains additional categorization information for organizations.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Organization metadata response")
public class OrganizationMetadataResponseDto {

    @Schema(description = "Organization industry", example = "TECHNOLOGY")
    private String industry;

    @Schema(description = "Organization size by employee count", example = "11-200")
    private String size;

    // Custom constructor for convenience
    public OrganizationMetadataResponseDto(String industry, String size) {
        this.industry = industry;
        this.size = size;
    }
}