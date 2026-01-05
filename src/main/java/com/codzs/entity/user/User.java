package com.codzs.entity.user;

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
import com.codzs.constant.user.UserConstants;

import java.time.Instant;

/**
 * MongoDB Document representing users within the Codzs Platform.
 * This entity stores user information within organizations and tenants,
 * providing user management and authentication support.
 * 
 * Storage Database: codzs_auth_{env}
 * Collection: user
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Document(collection = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User extends BaseEntity {

    @Id
    private String id;

    @NotBlank(message = UserConstants.EMAIL_REQUIRED_MESSAGE)
    @Email(message = UserConstants.EMAIL_FORMAT_MESSAGE)
    @Pattern(regexp = CommonConstants.EMAIL_PATTERN, message = UserConstants.EMAIL_FORMAT_MESSAGE)
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = UserConstants.FIRST_NAME_REQUIRED_MESSAGE)
    @Size(max = UserConstants.MAX_FIRST_NAME_LENGTH, message = UserConstants.FIRST_NAME_SIZE_MESSAGE)
    private String firstName;

    @NotBlank(message = UserConstants.LAST_NAME_REQUIRED_MESSAGE)
    @Size(max = UserConstants.MAX_LAST_NAME_LENGTH, message = UserConstants.LAST_NAME_SIZE_MESSAGE)
    private String lastName;

    @Size(max = UserConstants.MAX_PHONE_NUMBER_LENGTH, message = UserConstants.PHONE_NUMBER_SIZE_MESSAGE)
    @Pattern(regexp = CommonConstants.PHONE_NUMBER_PATTERN, message = UserConstants.PHONE_NUMBER_FORMAT_MESSAGE)
    private String phoneNumber;

    @NotBlank(message = UserConstants.ORGANIZATION_ID_REQUIRED_MESSAGE)
    @Indexed
    private String organizationId;

    @Indexed
    private String tenantId;

    @Indexed
    private String departmentId;

    @NotBlank(message = UserConstants.USER_STATUS_REQUIRED_MESSAGE)
    @Size(max = UserConstants.MAX_USER_STATUS_LENGTH)
    @Indexed
    private String status;

    @NotBlank(message = UserConstants.USER_TYPE_REQUIRED_MESSAGE)
    @Size(max = UserConstants.MAX_USER_TYPE_LENGTH)
    private String userType;

    @Size(max = UserConstants.MAX_PREFERRED_LANGUAGE_LENGTH)
    private String preferredLanguage;

    @Size(max = UserConstants.MAX_TIMEZONE_LENGTH)
    private String timeZone;

    private Instant lastLoginAt;

    private Instant lastPasswordChangeAt;

    private Boolean isEmailVerified;

    private Boolean isPhoneVerified;

    private Boolean isTwoFactorEnabled;

    @NotNull(message = UserConstants.ACTIVE_STATUS_REQUIRED_MESSAGE)
    private Boolean isActive;

    // Password-related fields (hashed)
    private String passwordHash;

    private String passwordResetToken;

    private Instant passwordResetTokenExpiry;

    // Verification fields
    private String emailVerificationToken;

    private Instant emailVerificationTokenExpiry;
}