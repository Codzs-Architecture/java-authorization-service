package com.codzs.entity.domain;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

import com.codzs.constant.domain.DomainConstants;
import com.codzs.constant.domain.DomainVerificationMethodEnum;
import com.codzs.framework.entity.EntityDefaultInitializer;
import com.codzs.framework.helper.SpringContextHelper;

/**
 * Embedded Domain sub-object within Organization and Partner entities.
 * Each domain represents a verified domain name associated with the parent entity.
 * 
 * This is an embedded object, not a separate MongoDB document.
 * Storage: Embedded within organization.domains[] and partner.domains[] arrays
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "Domain configuration embedded within Organization or Partner")
public class Domain {

    @Schema(description = "Unique domain identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @NotBlank(message = "Domain name is required")
    @Size(max = 255, message = "Domain name must not exceed 255 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.[a-zA-Z]{2,}$", 
             message = "Invalid domain name format")
    private String name;

    @NotNull(message = "Verification status is required")
    private Boolean isVerified;

    @NotNull(message = "Primary status is required")
    private Boolean isPrimary;

    @Size(max = 100, message = "Verification token must not exceed 100 characters")
    private String verificationToken;
 
    @NotBlank(message = "Verification method is required")
    private String verificationMethod;

    private Instant createdDate;

    private Instant verifiedDate;

    // Constructor for new domain creation
    public Domain(String name, String verificationMethod) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.verificationMethod = verificationMethod;
        this.isVerified = DomainConstants.DEFAULT_IS_VERIFIED;
        this.isPrimary = DomainConstants.DEFAULT_IS_PRIMARY;
        // this.createdDate = Instant.now();
    }

    // Constructor with ID for existing domain
    public Domain(String id, String name, String verificationMethod) {
        this.id = id;
        this.name = name;
        this.verificationMethod = verificationMethod;
        this.isVerified = DomainConstants.DEFAULT_IS_VERIFIED;
        this.isPrimary = DomainConstants.DEFAULT_IS_PRIMARY;
        // this.createdDate = Instant.now();
    }

    // Utility method to mark domain as verified
    public void markAsVerified() {
        this.isVerified = true;
        this.verifiedDate = Instant.now();
    }

    // Utility method to set as primary domain
    public void setAsPrimary() {
        this.isPrimary = true;
    }

    // Utility method to remove primary status
    public void removePrimaryStatus() {
        this.isPrimary = false;
    }

    // Initialize default values for default constructor
    @PostConstruct
    private void initDefaults() {
        DomainVerificationMethodEnum domainVerificationMethodEnum = SpringContextHelper.getBean(DomainVerificationMethodEnum.class);

        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        this.isVerified = EntityDefaultInitializer.setDefaultIfNull(this.isVerified, DomainConstants.DEFAULT_IS_VERIFIED);
        this.isPrimary = EntityDefaultInitializer.setDefaultIfNull(this.isPrimary, DomainConstants.DEFAULT_IS_PRIMARY);
        this.verificationMethod = EntityDefaultInitializer.setDefaultIfNull(this.verificationMethod, domainVerificationMethodEnum.getDefaultValue());
        // if (this.createdDate == null) {
        //     this.createdDate = Instant.now();
        // }
    }
}