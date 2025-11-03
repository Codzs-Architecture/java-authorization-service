package com.codzs.util;

public interface OrganizationDatabaseConfigUtil {
  public static String normalizeSchemaName(String schemaName) {
    if (schemaName == null || schemaName.trim().isEmpty()) {
        return null;
    }
    
    // Convert to lowercase and replace any invalid characters
    String normalized = schemaName.trim().toLowerCase();
      
    // Replace spaces and special characters with underscores
    normalized = normalized.replaceAll("[^a-z0-9_]", "_");
    
    // Remove multiple consecutive underscores
    normalized = normalized.replaceAll("_{2,}", "_");
    
    // Remove leading/trailing underscores
    normalized = normalized.replaceAll("^_+|_+$", "");
    
    return normalized;
  }

}
