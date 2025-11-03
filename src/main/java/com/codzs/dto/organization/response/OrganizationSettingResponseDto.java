package com.codzs.dto.organization.response;

import com.codzs.constant.organization.OrganizationSchemaConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for organization setting in organization responses.
 * Contains localization and configuration setting.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = OrganizationSchemaConstants.ORG_SETTINGS_RESPONSE_DESCRIPTION)
public class OrganizationSettingResponseDto {

    @Schema(description = OrganizationSchemaConstants.LANGUAGE_DESCRIPTION, example = "en-US")
    private String language;

    @Schema(description = OrganizationSchemaConstants.TIMEZONE_DESCRIPTION, example = OrganizationSchemaConstants.EXAMPLE_TIMEZONE)
    private String timezone;

    @Schema(description = OrganizationSchemaConstants.CURRENCY_DESCRIPTION, example = "USD")
    private String currency;

    @Schema(description = OrganizationSchemaConstants.COUNTRY_DESCRIPTION, example = "US")
    private String country;

    // Custom constructor for convenience
    public OrganizationSettingResponseDto(String language, String timezone, String currency, String country) {
        this.language = language;
        this.timezone = timezone;
        this.currency = currency;
        this.country = country;
    }
}