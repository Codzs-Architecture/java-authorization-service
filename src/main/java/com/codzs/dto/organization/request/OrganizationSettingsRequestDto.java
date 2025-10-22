package com.codzs.dto.organization.request;

import com.codzs.constant.organization.OrganizationConstants;
import com.codzs.constant.organization.OrganizationSwaggerConstants;
import com.codzs.framework.annotation.validation.ApplyDefaults;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for organization settings in organization requests.
 * Contains localization and configuration settings.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Organization settings configuration")
@ApplyDefaults
@Getter
@Setter
@NoArgsConstructor
@ToString
public class OrganizationSettingsRequestDto {

    @Size(max = OrganizationConstants.MAX_LANGUAGE_LENGTH, message = "Language must not exceed " + OrganizationConstants.MAX_LANGUAGE_LENGTH + " characters")
    @Schema(description = "Organization default language", 
            example = OrganizationConstants.DEFAULT_LANGUAGE, 
            defaultValue = OrganizationConstants.DEFAULT_LANGUAGE)
    private String language;

    @Size(max = OrganizationConstants.MAX_TIMEZONE_LENGTH, message = "Timezone must not exceed " + OrganizationConstants.MAX_TIMEZONE_LENGTH + " characters")
    @Schema(description = "Organization default timezone", 
            example = OrganizationSwaggerConstants.EXAMPLE_TIMEZONE, 
            defaultValue = OrganizationConstants.DEFAULT_TIMEZONE)
    private String timezone;

    @Size(max = OrganizationConstants.MAX_CURRENCY_LENGTH, message = "Currency must not exceed " + OrganizationConstants.MAX_CURRENCY_LENGTH + " characters")
    @Schema(description = "Organization default currency", 
            example = OrganizationConstants.DEFAULT_CURRENCY, 
            defaultValue = OrganizationConstants.DEFAULT_CURRENCY)
    private String currency;

    @Size(max = OrganizationConstants.MAX_COUNTRY_LENGTH, message = "Country must not exceed " + OrganizationConstants.MAX_COUNTRY_LENGTH + " characters")
    @Schema(description = "Organization country code", 
            example = OrganizationConstants.DEFAULT_COUNTRY, 
            defaultValue = OrganizationConstants.DEFAULT_COUNTRY)
    private String country;

    public OrganizationSettingsRequestDto(String language, String timezone, String currency, String country) {
        this.language = language;
        this.timezone = timezone;
        this.currency = currency;
        this.country = country;
    }

    /**
     * Applies default values for null fields
     */
    public void applyDefaults() {
        if (this.language == null) {
            this.language = OrganizationConstants.DEFAULT_LANGUAGE;
        }
        if (this.timezone == null) {
            this.timezone = OrganizationConstants.DEFAULT_TIMEZONE;
        }
        if (this.currency == null) {
            this.currency = OrganizationConstants.DEFAULT_CURRENCY;
        }
        if (this.country == null) {
            this.country = OrganizationConstants.DEFAULT_COUNTRY;
        }
    }

}