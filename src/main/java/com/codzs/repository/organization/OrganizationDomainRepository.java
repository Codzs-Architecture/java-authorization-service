package com.codzs.repository.organization;

import com.codzs.entity.organization.Organization;
import com.codzs.repository.domain.DomainRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Organization Domain MongoDB operations.
 * Provides methods for managing domains as embedded objects within organizations.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Repository
public interface OrganizationDomainRepository extends DomainRepository<Organization> {
    // Inherits all domain operations from DomainRepository<Organization>
    // No additional organization-specific domain operations needed at this time
}