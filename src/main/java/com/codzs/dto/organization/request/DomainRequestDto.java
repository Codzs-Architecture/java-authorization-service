package com.codzs.dto.organization.request;

import com.codzs.constant.domain.DomainConstants;
import com.codzs.constant.domain.DomainVerificationMethodEnum;
import com.codzs.framework.annotation.validation.ApplyDefaults;
import com.codzs.framework.annotation.validation.ValidDynamicEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for domain in organization requests.
 * Contains domain information for organization.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Domain configuration for organization")
@ApplyDefaults
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DomainRequestDto {

    @NotBlank(message = "Domain name is required")
    @Size(min = DomainConstants.DOMAIN_NAME_MIN_LENGTH, max = DomainConstants.DOMAIN_NAME_MAX_LENGTH, 
          message = DomainConstants.DOMAIN_NAME_SIZE_MESSAGE)
    @Pattern(regexp = DomainConstants.DOMAIN_NAME_PATTERN, 
             message = DomainConstants.DOMAIN_NAME_PATTERN_MESSAGE)
    @Schema(description = "Domain name", example = DomainConstants.EXAMPLE_DOMAIN_NAME, required = true)
    private String name;

    @Schema(description = "Whether domain ownership is verified", 
            example = DomainConstants.DEFAULT_IS_VERIFIED, 
            defaultValue = DomainConstants.DEFAULT_IS_VERIFIED)
    private Boolean isVerified;

    @Schema(description = "Whether this is the primary domain", 
            example = "true", 
            defaultValue = DomainConstants.DEFAULT_IS_PRIMARY)
    private Boolean isPrimary;

    @Size(max = DomainConstants.VERIFICATION_TOKEN_MAX_LENGTH, 
          message = DomainConstants.VERIFICATION_TOKEN_SIZE_MESSAGE)
    @Schema(description = "Token for domain verification", 
            example = DomainConstants.EXAMPLE_VERIFICATION_TOKEN)
    private String verificationToken;

    @NotBlank(message = "Verification method is required")
    @ValidDynamicEnum(enumClass = DomainVerificationMethodEnum.class, message = "Invalid verification method")
    @Schema(description = "Domain verification method", example = "DNS", required = true, 
            allowableValues = {"DNS", "EMAIL", "FILE"})
    private String verificationMethod;

    public DomainRequestDto(String name, String verificationMethod) {
        this.name = name;
        this.verificationMethod = verificationMethod;
        this.isVerified = DomainConstants.BOOLEAN_DEFAULT_IS_VERIFIED;
        this.isPrimary = DomainConstants.BOOLEAN_DEFAULT_IS_PRIMARY;
    }

    /**
     * Applies default values for null fields
     */
    public void applyDefaults() {
        if (this.isVerified == null) {
            this.isVerified = DomainConstants.BOOLEAN_DEFAULT_IS_VERIFIED;
        }
        if (this.isPrimary == null) {
            this.isPrimary = DomainConstants.BOOLEAN_DEFAULT_IS_PRIMARY;
        }
    }

}