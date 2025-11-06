package com.codzs.framework.dto.localization;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for representing localization codes (country, currency, timezone, language) in API responses.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Localization code information")
public class CodeInfoDto {

    @Schema(description = "Code value (e.g., 'US', 'USD', 'UTC', 'en')", 
            example = "US", 
            required = true)
    private String code;

    @Schema(description = "Human-readable display name", 
            example = "United States", 
            required = true)
    private String value;

    @Schema(description = "Whether this is the default/preferred code", 
            example = "false")
    private boolean isDefault;

    /**
     * Convenience constructor without isDefault (defaults to false).
     */
    public CodeInfoDto(String code, String value) {
        this.code = code;
        this.value = value;
        this.isDefault = false;
    }
}