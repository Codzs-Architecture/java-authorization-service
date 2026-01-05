package com.codzs.service.organization;

import com.codzs.entity.organization.Organization;
import com.codzs.entity.organization.OrganizationMetadata;
import com.codzs.repository.organization.OrganizationMetadataRepository;
import com.codzs.repository.organization.OrganizationRepository;
import com.codzs.validation.organization.OrganizationMetadataBusinessValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service implementation for OrganizationMetadata-related business operations.
 * Manages organization metadata including industry and size
 * with proper business validation and transaction management. Follows entity-first design pattern.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class OrganizationMetadataServiceImpl extends BaseOrganizationServiceImpl implements OrganizationMetadataService {

    private final OrganizationMetadataRepository organizationMetadataRepository;
    private final OrganizationMetadataBusinessValidator organizationMetadataBusinessValidator;

    @Autowired
    public OrganizationMetadataServiceImpl(OrganizationMetadataRepository organizationMetadataRepository,
                                         OrganizationRepository organizationRepository, 
                                         OrganizationMetadataBusinessValidator organizationMetadataBusinessValidator,
                                         ObjectMapper objectMapper) {
        super(organizationRepository, objectMapper);
        this.organizationMetadataRepository = organizationMetadataRepository;
        this.organizationMetadataBusinessValidator = organizationMetadataBusinessValidator;
    }

    // ========== API FLOW METHODS ==========

    @Override
    @Transactional
    public Organization updateOrganizationMetadata(String organizationId, OrganizationMetadata metadata) {
        log.debug("Updating organization metadata for organization ID: {}", organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);

        OrganizationMetadata metadataOriginal = organization.getMetadata(); // Ensure metadata object exists
        
        if (metadata != null) {
            if (metadata.getIndustry() != null && !metadata.getIndustry().equals(metadataOriginal.getIndustry())) {
                updateIndustry(organization, metadata.getIndustry());
            }
            if (metadata.getSize() != null && !metadata.getSize().equals(metadataOriginal.getSize())) {
                updateSize(organization, metadata.getSize());
            }
        }
        
        log.info("Updated organization metadata for organization ID: {}", organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    @Override
    public Optional<OrganizationMetadata> getOrganizationMetadata(String organizationId) {
        log.debug("Getting organization metadata for organization ID: {}", organizationId);
        
        return getOrgById(organizationId)
                .map(Organization::getMetadata)
                .filter(metadata -> metadata != null);
    }

    @Override
    @Transactional
    public void updateIndustry(Organization organization, String industry) {
        log.debug("Updating industry for organization ID: {}", organization.getId());
        
        // Business validation for industry update
        organizationMetadataBusinessValidator.validateIndustryUpdate(organization, industry);
        
        // Use MongoDB operation to update industry directly
        organizationMetadataRepository.updateIndustry(organization.getId(), industry);
        
        log.info("Updated industry to {} for organization ID: {}", industry, organization.getId());
    }

    @Override
    @Transactional
    public void updateSize(Organization organization, String size) {
        log.debug("Updating size for organization ID: {}", organization.getId());
        
        // Business validation for size update
        organizationMetadataBusinessValidator.validateSizeUpdate(organization, size);
        
        // Use MongoDB operation to update size directly
        organizationMetadataRepository.updateSize(organization.getId(), size);
        
        log.info("Updated size to {} for organization ID: {}", size, organization.getId());
    }
}