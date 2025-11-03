package com.codzs.dto.organization.response;

import com.codzs.constant.domain.DomainSchemaConstants;
import com.codzs.framework.constant.CommonConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

/**
 * DTO for domain embedded within organization responses.
 * Contains domain information as a sub-object without separate audit fields.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = DomainSchemaConstants.DOMAIN_RESPONSE_DESCRIPTION)
public class DomainResponseDto {

    @Schema(description = DomainSchemaConstants.DOMAIN_ID_DESCRIPTION, example = DomainSchemaConstants.EXAMPLE_DOMAIN_ID)
    private String id;

    @Schema(description = DomainSchemaConstants.DOMAIN_NAME_DESCRIPTION, example = DomainSchemaConstants.EXAMPLE_DOMAIN_NAME)
    private String name;

    @Schema(description = DomainSchemaConstants.DOMAIN_IS_VERIFIED_DESCRIPTION, example = DomainSchemaConstants.EXAMPLE_IS_VERIFIED)
    private Boolean isVerified;

    @Schema(description = DomainSchemaConstants.DOMAIN_IS_PRIMARY_DESCRIPTION, example = DomainSchemaConstants.EXAMPLE_IS_PRIMARY)
    private Boolean isPrimary;

    @Schema(description = DomainSchemaConstants.VERIFICATION_TOKEN_DESCRIPTION, 
            example = DomainSchemaConstants.EXAMPLE_VERIFICATION_TOKEN)
    private String verificationToken;

    @Schema(description = DomainSchemaConstants.VERIFICATION_METHOD_DESCRIPTION,
            example = DomainSchemaConstants.DEFAULT_VERIFICATION_METHOD_EXAMPLE,
            allowableValues = {"DNS", "EMAIL", "FILE"})
    private String verificationMethod;

    @Schema(description = DomainSchemaConstants.DOMAIN_CREATED_DATE_DESCRIPTION, example = DomainSchemaConstants.EXAMPLE_CREATED_ON)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.UTC_TIMESTAMP_PATTERN)
    private Instant createdDate;

    @Schema(description = DomainSchemaConstants.DOMAIN_VERIFIED_DATE_DESCRIPTION, example = DomainSchemaConstants.EXAMPLE_MODIFIED_ON)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.UTC_TIMESTAMP_PATTERN)
    private Instant verifiedDate;
}