package com.codzs.repository.organization;

import com.codzs.constant.organization.OrganizationStatusEnum;
import com.codzs.entity.organization.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Organization MongoDB documents.
 * Provides methods for managing organizations with minimal, reusable functionality.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Repository
public interface OrganizationRepository extends MongoRepository<Organization, String> {

    // Basic CRUD and existence checks
    Optional<Organization> findByIdAndDeletedOnIsNull(String id);
    
    Optional<Organization> findByNameAndDeletedOnIsNull(String name);
    
    Optional<Organization> findByName(String name);
    
    Optional<Organization> findByAbbrAndDeletedOnIsNull(String abbr);
    
    Optional<Organization> findByAbbr(String abbr);
    
    boolean existsByNameAndDeletedOnIsNull(String name);
    
    boolean existsByAbbrAndDeletedOnIsNull(String abbr);

    // Status-based queries
    List<Organization> findByStatusAndDeletedOnIsNull(OrganizationStatusEnum status);
    
    Page<Organization> findByStatusAndDeletedOnIsNull(OrganizationStatusEnum status, Pageable pageable);

    // Search and filtering for listing APIs
    @Query("{ 'deletedDate': null, $and: [ " +
           "{ $or: [ " +
           "  { 'status': { $in: ?0 } }, " +
           "  { $expr: { $eq: [{ $size: ?0 }, 0] } } " +
           "] }, " +
           "{ $or: [ " +
           "  { 'organizationType': { $in: ?1 } }, " +
           "  { $expr: { $eq: [{ $size: ?1 }, 0] } } " +
           "] }, " +
           "{ $or: [ " +
           "  { 'metadata.industry': { $in: ?2 } }, " +
           "  { $expr: { $eq: [{ $size: ?2 }, 0] } } " +
           "] }, " +
           "{ $or: [ " +
           "  { 'metadata.size': { $in: ?3 } }, " +
           "  { $expr: { $eq: [{ $size: ?3 }, 0] } } " +
           "] }, " +
           "{ $or: [ " +
           "  { 'name': { $regex: ?4, $options: 'i' } }, " +
           "  { 'displayName': { $regex: ?4, $options: 'i' } }, " +
           "  { $expr: { $eq: [?4, ''] } } " +
           "] } " +
           "] }")
    Page<Organization> findWithFilters(List<OrganizationStatusEnum> statuses, 
                                      List<String> organizationTypes,
                                      List<String> industries, 
                                      List<String> sizes, 
                                      String searchText, 
                                      Pageable pageable);

    // Autocomplete functionality
    @Query("{ 'deletedDate': null, 'status': { $in: ?0 }, " +
           "$or: [ " +
           "  { 'name': { $regex: ?1, $options: 'i' } }, " +
           "  { 'displayName': { $regex: ?1, $options: 'i' } } " +
           "] }")
    List<Organization> findForAutocomplete(List<OrganizationStatusEnum> statuses, String searchQuery, Pageable pageable);

    // Hierarchy management
    List<Organization> findByParentOrganizationIdAndDeletedOnIsNull(String parentOrganizationId);
    
    Page<Organization> findByParentOrganizationIdAndDeletedOnIsNull(String parentOrganizationId, Pageable pageable);
    
    @Query("{ 'deletedDate': null, 'parentOrganizationId': ?0, $and: [ " +
           "{ $or: [ " +
           "  { 'status': { $in: ?1 } }, " +
           "  { $expr: { $eq: [{ $size: ?1 }, 0] } } " +
           "] }, " +
           "{ $or: [ " +
           "  { 'organizationType': { $in: ?2 } }, " +
           "  { $expr: { $eq: [{ $size: ?2 }, 0] } } " +
           "] } " +
           "] }")
    Page<Organization> findChildrenWithFilters(String parentOrganizationId, 
                                              List<OrganizationStatusEnum> statuses,
                                              List<String> organizationTypes, 
                                              Pageable pageable);

    // Access control helpers - find organizations for specific users
    @Query("{ 'deletedDate': null, 'ownerUserIds': { $in: [?0] } }")
    List<Organization> findByOwnerUserId(String userId);
    
    @Query("{ 'deletedDate': null, 'ownerUserIds': { $in: [?0] } }")
    Page<Organization> findByOwnerUserId(String userId, Pageable pageable);

    // Utility methods for validation
    long countByParentOrganizationIdAndDeletedOnIsNull(String parentOrganizationId);
    
    boolean existsByParentOrganizationIdAndDeletedOnIsNull(String parentOrganizationId);

    // Domain-related queries for embedded domains
    @Query("{ 'deletedOn': null, 'domains.name': ?0 }")
    boolean existsByDomainsName(String domainName);
}