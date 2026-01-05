package com.codzs.entity.domain;

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

import org.mapstruct.AfterMapping;

import com.codzs.constant.domain.DomainConstants;
import com.codzs.constant.domain.DomainVerificationMethodEnum;
import com.codzs.framework.context.spring.SpringContextHelper;
import com.codzs.framework.entity.BaseEntity;
import com.codzs.framework.util.StringUtil;
import com.codzs.util.domain.DomainUtil;

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
public class Domain extends BaseEntity {

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

    private Instant verifiedDate;

    // Constructor for new domain creation
    public Domain(String name, String verificationMethod) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.verificationMethod = verificationMethod;
        this.isVerified = DomainConstants.DEFAULT_IS_VERIFIED;
        this.isPrimary = DomainConstants.DEFAULT_IS_PRIMARY;
        this.createdDate = Instant.now();
    }

    // Constructor with ID for existing domain
    public Domain(String id, String name, String verificationMethod) {
        this.id = id;
        this.name = name;
        this.verificationMethod = verificationMethod;
        this.isVerified = DomainConstants.DEFAULT_IS_VERIFIED;
        this.isPrimary = DomainConstants.DEFAULT_IS_PRIMARY;
    }

    // Comprehensive constructor with null checking for all fields
    public Domain(String id, String name, Boolean isVerified, Boolean isPrimary, 
                  String verificationToken, String verificationMethod, Instant verifiedDate,
                  Instant createdDate, String createdBy, Instant lastModifiedDate, 
                  String lastModifiedBy, Instant deletedDate, String deletedBy, String correlationId) {
        
        // Domain-specific fields with null checks
        if (id != null) {
            this.id = id;
        }
        if (name != null) {
            this.name = name;
        }
        if (isVerified != null) {
            this.isVerified = isVerified;
        }
        if (isPrimary != null) {
            this.isPrimary = isPrimary;
        }
        if (verificationToken != null) {
            this.verificationToken = verificationToken;
        }
        if (verificationMethod != null) {
            this.verificationMethod = verificationMethod;
        }
        if (verifiedDate != null) {
            this.verifiedDate = verifiedDate;
        }
        
        // BaseEntity inherited fields with null checks
        if (createdDate != null) {
            this.createdDate = createdDate;
        }
        if (createdBy != null) {
            this.createdBy = createdBy;
        }
        if (lastModifiedDate != null) {
            this.lastModifiedDate = lastModifiedDate;
        }
        if (lastModifiedBy != null) {
            this.lastModifiedBy = lastModifiedBy;
        }
        if (deletedDate != null) {
            this.deletedDate = deletedDate;
        }
        if (deletedBy != null) {
            this.deletedBy = deletedBy;
        }
        if (correlationId != null) {
            this.correlationId = correlationId;
        }
    }

    public void setName(String name) {
      this.name = DomainUtil.normalizeDomainName(name);
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
    @AfterMapping
    private void initDefaults() {
        DomainVerificationMethodEnum domainVerificationMethodEnum = SpringContextHelper.getBean(DomainVerificationMethodEnum.class);

        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        this.isVerified = StringUtil.setDefaultIfNull(this.isVerified, DomainConstants.DEFAULT_IS_VERIFIED);
        this.isPrimary = StringUtil.setDefaultIfNull(this.isPrimary, DomainConstants.DEFAULT_IS_PRIMARY);
        this.verificationMethod = StringUtil.setDefaultIfNull(this.verificationMethod, domainVerificationMethodEnum.getDefaultValue());
    }
}