package com.codzs.service.organization;

import com.codzs.entity.organization.Organization;
import com.codzs.entity.organization.OrganizationMetadata;
import com.codzs.repository.organization.OrganizationMetadataRepository;
import com.codzs.repository.organization.OrganizationRepository;
import com.codzs.validation.organization.OrganizationMetadataBusinessValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.codzs.constant.organization.OrganizationIndustryEnum;
import com.codzs.constant.organization.OrganizationSizeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
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
    private final OrganizationIndustryEnum organizationIndustryEnum;
    private final OrganizationSizeEnum organizationSizeEnum;

    @Autowired
    public OrganizationMetadataServiceImpl(OrganizationMetadataRepository organizationMetadataRepository,
                                         OrganizationIndustryEnum organizationIndustryEnum,
                                         OrganizationSizeEnum organizationSizeEnum,
                                         OrganizationRepository organizationRepository, 
                                         ObjectMapper objectMapper) {
        super(organizationRepository, objectMapper);
        this.organizationMetadataRepository = organizationMetadataRepository;
        this.organizationMetadataBusinessValidator = new OrganizationMetadataBusinessValidator();
        this.organizationIndustryEnum = organizationIndustryEnum;
        this.organizationSizeEnum = organizationSizeEnum;
    }

    // ========== API FLOW METHODS ==========

    @Override
    @Transactional
    public Organization updateOrganizationMetadata(String organizationId, OrganizationMetadata metadata) {
        log.debug("Updating organization metadata for organization ID: {}", organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Get validation data for metadata
        boolean isValidIndustry = metadata.getIndustry() == null || isValidIndustry(metadata.getIndustry());
        boolean isValidSize = metadata.getSize() == null || isValidSize(metadata.getSize());
        
        // Business validation for metadata update
        organizationMetadataBusinessValidator.validateMetadataUpdate(organization, metadata, isValidIndustry, isValidSize);
        
        // Use MongoDB operation to update metadata directly
        organizationMetadataRepository.updateOrganizationMetadata(organizationId, metadata);
        
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
    public Organization updateIndustry(String organizationId, String industry) {
        log.debug("Updating industry for organization ID: {}", organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Get validation data for industry
        boolean isValidIndustry = isValidIndustry(industry);
        
        // Business validation for industry update
        organizationMetadataBusinessValidator.validateIndustryUpdate(organization, industry, isValidIndustry);
        
        // Use MongoDB operation to update industry directly
        organizationMetadataRepository.updateIndustry(organizationId, industry);
        
        log.info("Updated industry to {} for organization ID: {}", industry, organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    @Override
    @Transactional
    public Organization updateSize(String organizationId, String size) {
        log.debug("Updating size for organization ID: {}", organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Get validation data for size
        boolean isValidSize = isValidSize(size);
        
        // Business validation for size update
        organizationMetadataBusinessValidator.validateSizeUpdate(organization, size, isValidSize);
        
        // Use MongoDB operation to update size directly
        organizationMetadataRepository.updateSize(organizationId, size);
        
        log.info("Updated size to {} for organization ID: {}", size, organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    // ========== UTILITY METHODS ==========

    @Override
    public boolean validateOrganizationMetadata(String organizationId, OrganizationMetadata metadata) {
        if (metadata == null) {
            return false;
        }
        
        // Validate industry
        if (StringUtils.hasText(metadata.getIndustry()) && !isValidIndustry(metadata.getIndustry())) {
            return false;
        }
        
        // Validate size
        if (StringUtils.hasText(metadata.getSize()) && !isValidSize(metadata.getSize())) {
            return false;
        }
        
        return true;
    }

    @Override
    public boolean isValidIndustry(String industry) {
        if (!StringUtils.hasText(industry)) {
            return false;
        }
        
        List<String> validIndustries = getAvailableIndustries();
        return validIndustries.contains(industry);
    }

    @Override
    public boolean isValidSize(String size) {
        if (!StringUtils.hasText(size)) {
            return false;
        }
        
        List<String> validSizes = getAvailableSizes();
        return validSizes.contains(size);
    }

    @Override
    public List<String> getAvailableIndustries() {
        return organizationIndustryEnum.getOptions();
    }

    @Override
    public List<String> getAvailableSizes() {
        return organizationSizeEnum.getOptions();
    }

}