package com.codzs.util.organization;

import com.codzs.entity.organization.DatabaseSchema;
import com.codzs.entity.organization.Organization;
import org.springframework.util.StringUtils;

/**
 * Utility class for database schema operations.
 * Contains static methods for common schema operations that can be reused across services.
 * 
 * @author Codzs Team
 * @since 1.0
 */
public final class DatabaseSchemaUtil {

    private DatabaseSchemaUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Finds a schema by ID within an organization's database schemas.
     * 
     * @param organization the organization containing the schemas
     * @param schemaId the ID of the schema to find
     * @return the found schema or null if not found
     */
    public static DatabaseSchema findSchemaById(Organization organization, String schemaId) {
        if (organization.getDatabase() == null || organization.getDatabase().getSchemas() == null) {
            return null;
        }
        
        return organization.getDatabase().getSchemas().stream()
                .filter(schema -> schemaId.equals(schema.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Applies business logic to a database schema.
     * Auto-constructs full schema name from user-provided service part.
     * User provides service part in schemaName field, we construct: codzs_<org_abbr>_<service>_<env>
     * 
     * @param organization the organization context
     * @param schema the schema to apply business logic to
     * @param activeProfile the active environment profile
     */
    public static void applySchemaBusinessLogic(Organization organization, DatabaseSchema schema, String activeProfile) {
        // Auto-construct full schema name from user-provided service part
        // User provides service part in schemaName field, we construct: codzs_<org_abbr>_<service>_<env>
        String serviceInput = schema.getSchemaName(); // This contains the service part from user
        String autoConstructedName = constructSchemaNameFromService(organization.getAbbr(), serviceInput, activeProfile);
        schema.setSchemaName(autoConstructedName);
    }

    /**
     * Constructs schema name from service input.
     * Format: codzs_<org_abbr>_<service>_<env>
     * 
     * @param organizationAbbr organization abbreviation
     * @param service service name (input from user)
     * @param activeProfile the active environment profile
     * @return constructed schema name
     */
    public static String constructSchemaNameFromService(String organizationAbbr, String service, String activeProfile) {
        StringBuilder schemaName = new StringBuilder("codzs_");
        
        // Add organization abbreviation
        if (StringUtils.hasText(organizationAbbr)) {
            schemaName.append(organizationAbbr.toLowerCase()).append("_");
        } else {
            schemaName.append("default_");
        }
        
        // Add service (user input)
        if (StringUtils.hasText(service)) {
            schemaName.append(service.toLowerCase()).append("_");
        } else {
            schemaName.append("unknown_");
        }
        
        // Add environment (from active profile)
        schemaName.append(activeProfile.toLowerCase());
        
        return schemaName.toString();
    }
}