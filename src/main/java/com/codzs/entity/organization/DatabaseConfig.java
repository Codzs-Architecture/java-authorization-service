package com.codzs.entity.organization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Database configuration sub-entity representing database connection
 * information for organizations.
 * Used as an embedded object within Organization entity.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DatabaseConfig {

    @NotBlank(message = "Connection string is required")
    private String connectionString;

    @NotBlank(message = "Certificate is required")
    private String certificate;

    @NotEmpty(message = "At least one schema is required")
    @Valid
    private List<DatabaseSchema> schemas = List.of();

}