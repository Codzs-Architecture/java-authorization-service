package com.codzs.dto.organization.response;

import com.codzs.constant.domain.DomainConstants;
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
@Schema(description = "Domain response for embedded domain")
public class DomainResponseDto {

    @Schema(description = "Domain unique identifier", example = DomainConstants.EXAMPLE_DOMAIN_ID)
    private String id;

    @Schema(description = "Domain name", example = DomainConstants.EXAMPLE_DOMAIN_NAME)
    private String name;

    @Schema(description = "Whether domain ownership is verified", example = DomainConstants.EXAMPLE_IS_VERIFIED)
    private Boolean isVerified;

    @Schema(description = "Whether this is the primary domain", example = DomainConstants.EXAMPLE_IS_PRIMARY)
    private Boolean isPrimary;

    @Schema(description = "Token for domain verification", 
            example = DomainConstants.EXAMPLE_VERIFICATION_TOKEN)
    private String verificationToken;

    @Schema(description = "Domain verification method", 
            example = DomainConstants.DEFAULT_VERIFICATION_METHOD)
    private String verificationMethod;

    @Schema(description = "Domain creation timestamp", example = DomainConstants.EXAMPLE_CREATED_ON)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.UTC_TIMESTAMP_PATTERN)
    private Instant createdOn;

    @Schema(description = "Domain verification timestamp", example = DomainConstants.EXAMPLE_VERIFIED_ON)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.UTC_TIMESTAMP_PATTERN)
    private Instant verifiedOn;
}