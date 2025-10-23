package com.codzs.entity.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Organization metadata sub-entity representing additional
 * categorization data for organizations.
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
public class OrganizationMetadata {

    private String industry;

    private String size;

}