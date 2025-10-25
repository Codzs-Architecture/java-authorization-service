package com.codzs.service.organization;

import com.codzs.entity.organization.Organization;
import com.codzs.entity.organization.OrganizationMetadata;
import com.codzs.exception.validation.ValidationException;
import com.codzs.repository.organization.OrganizationMetadataRepository;
import com.codzs.validation.organization.OrganizationMetadataBusinessValidator;
import com.codzs.constant.organization.OrganizationIndustryEnum;
import com.codzs.constant.organization.OrganizationSizeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

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
public class OrganizationMetadataServiceImpl implements OrganizationMetadataService {

    private final OrganizationMetadataRepository organizationMetadataRepository;
    private final OrganizationService organizationService;
    private final OrganizationMetadataBusinessValidator organizationMetadataBusinessValidator;
    private final OrganizationIndustryEnum organizationIndustryEnum;
    private final OrganizationSizeEnum organizationSizeEnum;

    @Autowired
    public OrganizationMetadataServiceImpl(OrganizationMetadataRepository organizationMetadataRepository,
                                         OrganizationService organizationService,
                                         OrganizationMetadataBusinessValidator organizationMetadataBusinessValidator,
                                         OrganizationIndustryEnum organizationIndustryEnum,
                                         OrganizationSizeEnum organizationSizeEnum) {
        this.organizationMetadataRepository = organizationMetadataRepository;
        this.organizationService = organizationService;
        this.organizationMetadataBusinessValidator = organizationMetadataBusinessValidator;
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
        
        // Business validation for metadata update
        organizationMetadataBusinessValidator.validateMetadataUpdate(organization, metadata);
        
        // Use MongoDB operation to update metadata directly
        organizationMetadataRepository.updateOrganizationMetadata(organizationId, metadata);
        
        log.info("Updated organization metadata for organization ID: {}", organizationId);
        
        // Return updated organization
        return getOrganizationAndValidate(organizationId);
    }

    @Override
    public OrganizationMetadata getOrganizationMetadata(String organizationId) {
        log.debug("Getting organization metadata for organization ID: {}", organizationId);
        
        try {
            Organization organization = getOrganizationAndValidate(organizationId);
            return organization.getMetadata();
        } catch (ValidationException e) {
            log.warn("Organization not found with ID: {}", organizationId);
            return null;
        }
    }

    @Override
    @Transactional
    public Organization updateIndustry(String organizationId, String industry) {
        log.debug("Updating industry for organization ID: {}", organizationId);
        
        // Get organization and validate it exists
        Organization organization = getOrganizationAndValidate(organizationId);
        
        // Business validation for industry update
        organizationMetadataBusinessValidator.validateIndustryUpdate(organization, industry);
        
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
        
        // Business validation for size update
        organizationMetadataBusinessValidator.validateSizeUpdate(organization, size);
        
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

    // ========== PRIVATE HELPER METHODS ==========

    private Organization getOrganizationAndValidate(String organizationId) {
        Organization organization = organizationService.findById(organizationId);
        if (organization == null) {
            throw new ValidationException("Organization not found with ID: " + organizationId);
        }
        return organization;
    }
}