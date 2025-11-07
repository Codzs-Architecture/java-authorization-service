package com.codzs.dto.domain.request;

import com.codzs.constant.domain.DomainConstants;
import com.codzs.constant.domain.DomainSchemaConstants;
import com.codzs.constant.domain.DomainVerificationMethodEnum;
import com.codzs.framework.annotation.validation.ApplyDefaults;
import com.codzs.framework.validation.annotation.ValidDynamicEnum;
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
@Schema(description = DomainSchemaConstants.DOMAIN_CONFIGURATION_DESCRIPTION)
@ApplyDefaults
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DomainRequestDto {

    @NotBlank(message = DomainSchemaConstants.DOMAIN_NAME_REQUIRED_MESSAGE)
    @Size(min = DomainSchemaConstants.DOMAIN_NAME_MIN_LENGTH, max = DomainSchemaConstants.DOMAIN_NAME_MAX_LENGTH, 
          message = DomainSchemaConstants.DOMAIN_NAME_SIZE_MESSAGE)
    @Pattern(regexp = DomainSchemaConstants.DOMAIN_NAME_PATTERN, 
             message = DomainSchemaConstants.DOMAIN_NAME_PATTERN_MESSAGE)
    @Schema(description = DomainSchemaConstants.DOMAIN_NAME_DESCRIPTION, example = DomainSchemaConstants.EXAMPLE_DOMAIN_NAME, required = true)
    private String name;

    @Schema(description = DomainSchemaConstants.DOMAIN_IS_PRIMARY_DESCRIPTION, 
            example = DomainSchemaConstants.EXAMPLE_IS_PRIMARY, 
            defaultValue = DomainSchemaConstants.EXAMPLE_IS_PRIMARY)
    private Boolean isPrimary;

    @NotBlank(message = DomainSchemaConstants.VERIFICATION_METHOD_REQUIRED_MESSAGE)
    @ValidDynamicEnum(enumClass = DomainVerificationMethodEnum.class, message = DomainSchemaConstants.VERIFICATION_METHOD_INVALID_MESSAGE)
    @Schema(description = DomainSchemaConstants.VERIFICATION_METHOD_REQUEST_DESCRIPTION, 
            example = DomainSchemaConstants.DEFAULT_VERIFICATION_METHOD_EXAMPLE, 
            required = true, 
            allowableValues = {"DNS", "EMAIL", "FILE"})
    private String verificationMethod;

    public DomainRequestDto(String name, String verificationMethod) {
        this.name = name;
        this.verificationMethod = verificationMethod;
        this.isPrimary = DomainConstants.DEFAULT_IS_PRIMARY;
    }
}