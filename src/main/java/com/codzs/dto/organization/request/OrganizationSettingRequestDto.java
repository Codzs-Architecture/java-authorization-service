package com.codzs.dto.organization.request;

import com.codzs.constant.organization.OrganizationSchemaConstants;
import com.codzs.framework.annotation.validation.ApplyDefaults;
import com.codzs.framework.constant.CommonConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for organization setting in organization requests.
 * Contains localization and configuration setting.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = OrganizationSchemaConstants.ORG_SETTINGS_DESCRIPTION)
@ApplyDefaults
@Getter
@Setter
@NoArgsConstructor
@ToString
public class OrganizationSettingRequestDto {

    @Size(max = OrganizationSchemaConstants.MAX_LANGUAGE_LENGTH, message = OrganizationSchemaConstants.LANGUAGE_SIZE_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.LANGUAGE_DESCRIPTION, 
            example = CommonConstants.DEFAULT_LANGUAGE, 
            defaultValue = CommonConstants.DEFAULT_LANGUAGE)
    private String language;

    @Size(max = OrganizationSchemaConstants.MAX_TIMEZONE_LENGTH, message = OrganizationSchemaConstants.TIMEZONE_SIZE_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.TIMEZONE_DESCRIPTION, 
            example = OrganizationSchemaConstants.EXAMPLE_TIMEZONE, 
            defaultValue = CommonConstants.DEFAULT_TIMEZONE)
    private String timezone;

    @Size(max = OrganizationSchemaConstants.MAX_CURRENCY_LENGTH, message = OrganizationSchemaConstants.CURRENCY_SIZE_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.CURRENCY_DESCRIPTION, 
            example = CommonConstants.DEFAULT_CURRENCY, 
            defaultValue = CommonConstants.DEFAULT_CURRENCY)
    private String currency;

    @Size(max = OrganizationSchemaConstants.MAX_COUNTRY_LENGTH, message = OrganizationSchemaConstants.COUNTRY_SIZE_MESSAGE)
    @Schema(description = OrganizationSchemaConstants.COUNTRY_DESCRIPTION, 
            example = CommonConstants.DEFAULT_COUNTRY, 
            defaultValue = CommonConstants.DEFAULT_COUNTRY)
    private String country;

    public OrganizationSettingRequestDto(String language, String timezone, String currency, String country) {
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
            this.language = CommonConstants.DEFAULT_LANGUAGE;
        }
        if (this.timezone == null) {
            this.timezone = CommonConstants.DEFAULT_TIMEZONE;
        }
        if (this.currency == null) {
            this.currency = CommonConstants.DEFAULT_CURRENCY;
        }
        if (this.country == null) {
            this.country = CommonConstants.DEFAULT_COUNTRY;
        }
    }

}