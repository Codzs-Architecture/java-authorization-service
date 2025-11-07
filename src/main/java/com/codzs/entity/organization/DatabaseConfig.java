package com.codzs.entity.organization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.springframework.core.env.Environment;

import com.codzs.framework.helper.SpringContextHelper;

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

    public DatabaseConfig() {
        this.applyDefaults();
    }

    @AfterMapping
    public void applyDefaults() {
        if (this.connectionString == null || this.connectionString.trim().isEmpty()) {
            try {
                Environment environment = SpringContextHelper.getBean(Environment.class);
                this.connectionString = environment.getProperty("spring.data.mongodb.uri");
            } catch (Exception e) {
                // Fallback if Spring context is not available
            }
        }
    }
}