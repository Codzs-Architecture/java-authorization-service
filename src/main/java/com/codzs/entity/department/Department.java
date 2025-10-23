package com.codzs.entity.department;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.codzs.framework.constant.CommonConstants;
import com.codzs.framework.entity.BaseEntity;
import com.codzs.constant.department.DepartmentConstants;

/**
 * MongoDB Document representing departments within the Codzs Platform.
 * This entity stores department information within organizations and tenants,
 * providing organizational structure and user grouping capabilities.
 * 
 * Storage Database: codzs_auth_{env}
 * Collection: department
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Document(collection = "department")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Department extends BaseEntity {

    @Id
    private String id;

    @NotBlank(message = DepartmentConstants.DEPARTMENT_NAME_REQUIRED_MESSAGE)
    @Size(min = DepartmentConstants.MIN_DEPARTMENT_NAME_LENGTH, max = DepartmentConstants.MAX_DEPARTMENT_NAME_LENGTH, 
          message = DepartmentConstants.DEPARTMENT_NAME_SIZE_MESSAGE)
    private String name;

    @Size(max = DepartmentConstants.MAX_DEPARTMENT_CODE_LENGTH, message = DepartmentConstants.DEPARTMENT_CODE_SIZE_MESSAGE)
    @Pattern(regexp = DepartmentConstants.DEPARTMENT_CODE_PATTERN, message = DepartmentConstants.DEPARTMENT_CODE_PATTERN_MESSAGE)
    @Indexed
    private String code;

    @Size(max = DepartmentConstants.MAX_DEPARTMENT_DESCRIPTION_LENGTH, message = DepartmentConstants.DEPARTMENT_DESCRIPTION_SIZE_MESSAGE)
    private String description;

    @NotBlank(message = DepartmentConstants.ORGANIZATION_ID_REQUIRED_MESSAGE)
    @Indexed
    private String organizationId;

    @Indexed
    private String tenantId;

    @Indexed
    private String parentDepartmentId;

    @NotBlank(message = DepartmentConstants.DEPARTMENT_STATUS_REQUIRED_MESSAGE)
    @Size(max = DepartmentConstants.MAX_DEPARTMENT_STATUS_LENGTH)
    @Indexed
    private String status;

    @Size(max = DepartmentConstants.MAX_COST_CENTER_LENGTH, message = DepartmentConstants.COST_CENTER_SIZE_MESSAGE)
    @Pattern(regexp = DepartmentConstants.COST_CENTER_PATTERN, message = DepartmentConstants.COST_CENTER_PATTERN_MESSAGE)
    private String costCenter;

    @Size(max = DepartmentConstants.MAX_LOCATION_LENGTH)
    private String location;

    private String managerId;

    @Min(value = DepartmentConstants.MIN_MAX_USERS, message = DepartmentConstants.MAX_USERS_MIN_MESSAGE)
    private Integer maxUsers;

    @Min(value = DepartmentConstants.MIN_HIERARCHY_LEVEL, message = DepartmentConstants.HIERARCHY_LEVEL_MIN_MESSAGE)
    private Integer hierarchyLevel;

    @NotNull(message = DepartmentConstants.ACTIVE_STATUS_REQUIRED_MESSAGE)
    private Boolean isActive;

    // Budget and financial information
    @DecimalMin(value = "0.0", message = "Budget amount must be non-negative")
    @DecimalMax(value = "999999999.99", message = "Budget amount exceeds maximum allowed")
    private Double budgetAllocated;

    @Size(max = DepartmentConstants.MAX_BUDGET_CURRENCY_LENGTH)
    private String budgetCurrency;

    // Contact information
    @Size(max = DepartmentConstants.MAX_PHONE_NUMBER_LENGTH)
    @Pattern(regexp = CommonConstants.PHONE_NUMBER_PATTERN, message = "Invalid phone number format")
    private String phoneNumber;

    @Size(max = DepartmentConstants.MAX_EMAIL_ADDRESS_LENGTH)
    @Pattern(regexp = CommonConstants.EMAIL_PATTERN, message = "Invalid email format")
    private String emailAddress;
}