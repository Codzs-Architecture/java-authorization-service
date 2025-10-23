package com.codzs.entity.tenant;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.codzs.framework.entity.BaseEntity;
import com.codzs.constant.tenant.TenantConstants;

/**
 * MongoDB Document representing tenants within the Codzs Platform.
 * This entity stores tenant information within organizations,
 * providing multi-tenancy support for the platform.
 * 
 * Storage Database: codzs_auth_{env}
 * Collection: tenant
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Document(collection = "tenant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Tenant extends BaseEntity {

    @Id
    private String id;

    @NotBlank(message = TenantConstants.TENANT_NAME_REQUIRED_MESSAGE)
    @Size(min = TenantConstants.MIN_TENANT_NAME_LENGTH, max = TenantConstants.MAX_TENANT_NAME_LENGTH, 
          message = TenantConstants.TENANT_NAME_SIZE_MESSAGE)
    private String name;

    @Size(max = TenantConstants.MAX_TENANT_CODE_LENGTH, message = TenantConstants.TENANT_CODE_SIZE_MESSAGE)
    @Pattern(regexp = TenantConstants.TENANT_CODE_PATTERN, message = TenantConstants.TENANT_CODE_PATTERN_MESSAGE)
    @Indexed
    private String code;

    @Size(max = TenantConstants.MAX_TENANT_DESCRIPTION_LENGTH, message = TenantConstants.TENANT_DESCRIPTION_SIZE_MESSAGE)
    private String description;

    @NotBlank(message = TenantConstants.ORGANIZATION_ID_REQUIRED_MESSAGE)
    @Indexed
    private String organizationId;

    @NotBlank(message = TenantConstants.TENANT_STATUS_REQUIRED_MESSAGE)
    @Size(max = TenantConstants.MAX_TENANT_STATUS_LENGTH)
    @Indexed
    private String status;

    @Indexed
    private String parentTenantId;

    @Size(max = TenantConstants.MAX_TIMEZONE_LENGTH)
    private String timeZone;

    @Size(max = TenantConstants.MAX_LOCALE_LENGTH)
    private String locale;

    @Size(max = TenantConstants.MAX_CURRENCY_LENGTH)
    private String currency;

    @Min(value = TenantConstants.MIN_MAX_USERS, message = TenantConstants.MAX_USERS_MIN_MESSAGE)
    private Integer maxUsers;

    @Min(value = TenantConstants.MIN_MAX_DEPARTMENTS, message = TenantConstants.MAX_DEPARTMENTS_MIN_MESSAGE)
    private Integer maxDepartments;

    @Min(value = TenantConstants.MIN_STORAGE_LIMIT, message = TenantConstants.STORAGE_LIMIT_MIN_MESSAGE)
    private Long storageLimit;

    @NotNull(message = TenantConstants.ACTIVE_STATUS_REQUIRED_MESSAGE)
    private Boolean isActive;
}