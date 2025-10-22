package com.codzs.dto.organization.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for organization settings in organization responses.
 * Contains localization and configuration settings.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Organization settings response")
public class OrganizationSettingsResponseDto {

    @Schema(description = "Organization default language", example = "en-US")
    private String language;

    @Schema(description = "Organization default timezone", example = "America/New_York")
    private String timezone;

    @Schema(description = "Organization default currency", example = "USD")
    private String currency;

    @Schema(description = "Organization country code", example = "US")
    private String country;

    // Custom constructor for convenience
    public OrganizationSettingsResponseDto(String language, String timezone, String currency, String country) {
        this.language = language;
        this.timezone = timezone;
        this.currency = currency;
        this.country = country;
    }
}