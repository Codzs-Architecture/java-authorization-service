package com.codzs.mapper.organization;

import com.codzs.dto.organization.request.OrganizationCreateRequestDto;
import com.codzs.dto.organization.request.OrganizationUpdateRequestDto;
import com.codzs.dto.organization.response.OrganizationResponseDto;
import com.codzs.entity.organization.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Complete MapStruct mapper for Organization entity that handles all nested objects.
 * This mapper composes all the individual mappers to provide complete mapping functionality.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Component
public class OrganizationCompleteMapper {

    @Autowired
    private OrganizationMapper organizationMapper;
    
    @Autowired
    private OrganizationDomainMapper domainMapper;
    
    @Autowired
    private DatabaseConfigMapper databaseConfigMapper;
    
    @Autowired
    private OrganizationSettingsMapper settingsMapper;
    
    @Autowired
    private OrganizationMetadataMapper metadataMapper;

    // ========================= CREATE MAPPINGS =========================
    
    /**
     * Complete mapping from request DTO to Organization entity with all nested objects.
     * Audit fields are automatically populated by Spring Data MongoDB auditing.
     */
    public Organization toEntity(OrganizationCreateRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        
        // Map the main organization fields
        // Note: Audit fields (createdBy, createdDate, lastModifiedBy, lastModifiedDate) are automatically
        // populated by Spring Data MongoDB auditing
        Organization organization = organizationMapper.toEntity(requestDto);
        
        // Map nested objects using dedicated mappers
        if (requestDto.getDatabase() != null) {
            organization.setDatabase(databaseConfigMapper.toEntity(requestDto.getDatabase()));
        }
        
        if (requestDto.getSettings() != null) {
            organization.setSettings(settingsMapper.toEntity(requestDto.getSettings()));
        }
        
        if (requestDto.getMetadata() != null) {
            organization.setMetadata(metadataMapper.toEntity(requestDto.getMetadata()));
        }
        
        if (requestDto.getDomains() != null && !requestDto.getDomains().isEmpty()) {
            organization.setDomains(
                requestDto.getDomains().stream()
                    .map(domainMapper::toEntity)
                    .toList()
            );
        }
        
        return organization;
    }

    // ========================= UPDATE MAPPINGS =========================
    
    /**
     * Complete update mapping from request DTO to existing Organization entity.
     * Audit fields are automatically updated by Spring Data MongoDB auditing.
     */
    public void updateEntity(Organization organization, OrganizationUpdateRequestDto requestDto) {
        if (requestDto == null || organization == null) {
            return;
        }
        
        // Update main organization fields
        // Note: Audit fields (lastModifiedBy, lastModifiedDate) are automatically
        // populated by Spring Data MongoDB auditing
        organizationMapper.updateEntity(organization, requestDto);
        
        // Update nested objects using dedicated mappers
        if (requestDto.getDatabase() != null) {
            if (organization.getDatabase() == null) {
                organization.setDatabase(databaseConfigMapper.toEntity(requestDto.getDatabase()));
            } else {
                databaseConfigMapper.updateEntity(organization.getDatabase(), requestDto.getDatabase());
            }
        }
        
        if (requestDto.getSettings() != null) {
            if (organization.getSettings() == null) {
                organization.setSettings(settingsMapper.toEntity(requestDto.getSettings()));
            } else {
                settingsMapper.updateEntity(organization.getSettings(), requestDto.getSettings());
            }
        }
        
        if (requestDto.getMetadata() != null) {
            if (organization.getMetadata() == null) {
                organization.setMetadata(metadataMapper.toEntity(requestDto.getMetadata()));
            } else {
                metadataMapper.updateEntity(organization.getMetadata(), requestDto.getMetadata());
            }
        }
        
        if (requestDto.getDomains() != null) {
            if (organization.getDomains() != null) {
                organization.getDomains().clear();
            }
            organization.setDomains(
                requestDto.getDomains().stream()
                    .map(domainMapper::toEntity)
                    .toList()
            );
        }
    }

    // ========================= RESPONSE MAPPINGS =========================
    
    /**
     * Complete mapping from Organization entity to response DTO with all nested objects.
     */
    public OrganizationResponseDto toResponse(Organization organization) {
        if (organization == null) {
            return null;
        }
        
        // Map the main organization fields
        OrganizationResponseDto responseDto = organizationMapper.toResponse(organization);
        
        // Map nested objects using dedicated mappers
        if (organization.getDatabase() != null) {
            responseDto.setDatabase(databaseConfigMapper.toResponse(organization.getDatabase()));
        }
        
        if (organization.getSettings() != null) {
            responseDto.setSettings(settingsMapper.toResponse(organization.getSettings()));
        }
        
        if (organization.getMetadata() != null) {
            responseDto.setMetadata(metadataMapper.toResponse(organization.getMetadata()));
        }
        
        if (organization.getDomains() != null && !organization.getDomains().isEmpty()) {
            responseDto.setDomains(domainMapper.toResponseList(organization.getDomains()));
        }
        
        return responseDto;
    }

    /**
     * Maps list of Organization entities to list of complete response DTOs.
     */
    public List<OrganizationResponseDto> toResponseList(List<Organization> organizations) {
        if (organizations == null) {
            return null;
        }
        
        return organizations.stream()
                .map(this::toResponse)
                .toList();
    }

    // ========================= SPECIALIZED MAPPINGS =========================
    
    /**
     * Maps Organization entity to autocomplete response (simplified).
     */
    public OrganizationResponseDto toAutocompleteResponse(Organization organization) {
        return organizationMapper.toAutocompleteResponse(organization);
    }

    /**
     * Maps list of Organization entities to autocomplete response list.
     */
    public List<OrganizationResponseDto> toAutocompleteResponseList(List<Organization> organizations) {
        return organizationMapper.toAutocompleteResponseList(organizations);
    }

    /**
     * Maps Organization entity to hierarchy response.
     */
    public OrganizationResponseDto toHierarchyResponse(Organization organization) {
        return organizationMapper.toHierarchyResponse(organization);
    }

    /**
     * Maps list of Organization entities to hierarchy response list.
     */
    public List<OrganizationResponseDto> toHierarchyResponseList(List<Organization> organizations) {
        return organizationMapper.toHierarchyResponseList(organizations);
    }
}