package com.codzs.framework.dto;

import com.codzs.framework.constant.CommonConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Base DTO class containing common audit fields for all response DTOs.
 * This class provides standard audit trail information that is included
 * in most API responses.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Base DTO containing common audit fields")
@Getter
@Setter
@NoArgsConstructor
@ToString
public abstract class BaseDto {

    @Schema(description = "Entity creation timestamp", example = "2024-01-20T16:30:00.000Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.ISO_INSTANT_PATTERN)
    protected String createdDate;

    @Schema(description = "ID of user who created the entity", example = "550e8400-e29b-41d4-a716-446655440000")
    protected String createdBy;

    @Schema(description = "Last update timestamp", example = "2024-01-20T16:30:00.000Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.ISO_INSTANT_PATTERN)
    protected String lastModifiedDate;

    @Schema(description = "ID of user who last updated the entity", example = "550e8400-e29b-41d4-a716-446655440000")
    protected String lastModifiedBy;

    @JsonIgnore
    @Schema(description = "Soft deletion timestamp", example = "2024-01-20T16:30:00.000Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.ISO_INSTANT_PATTERN)
    protected String deletedDate;

    @JsonIgnore
    @Schema(description = "ID of user who deleted the entity", example = "550e8400-e29b-41d4-a716-446655440000")
    protected String deletedBy;

    @JsonIgnore
    @Schema(description = "Correlation ID for request tracking", example = "req_789")
    protected String correlationId;

    /**
     * Checks if the entity is soft deleted
     * 
     * @return true if the entity is deleted, false otherwise
     */
    @JsonIgnore
    public boolean isDeleted() {
        return deletedDate != null;
    }
}