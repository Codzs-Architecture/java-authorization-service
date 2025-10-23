package com.codzs.entity.organization;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Organization settings sub-entity representing configuration
 * settings for organizations.
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
public class OrganizationSettings {

    @Size(max = 50, message = "Language must not exceed 50 characters")
    private String language;

    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    private String timezone;

    @Size(max = 10, message = "Currency must not exceed 10 characters")
    private String currency;

    @Size(max = 10, message = "Country must not exceed 10 characters")
    private String country;

}