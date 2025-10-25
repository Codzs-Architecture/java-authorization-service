package com.codzs.repository.organization;

import com.codzs.constant.organization.OrganizationStatusEnum;
import com.codzs.entity.organization.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Organization MongoDB documents.
 * Provides methods for managing organizations with root-level attribute operations only.
 * Nested sub-objects (settings, metadata, domains, database) are handled by their respective repositories.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Repository
public interface OrganizationRepository extends MongoRepository<Organization, String> {

    // ========== BASIC CRUD OPERATIONS ==========
    
    Optional<Organization> findByIdAndDeletedOnIsNull(String id);
    
    Optional<Organization> findByNameAndDeletedOnIsNull(String name);
    
    Optional<Organization> findByAbbrAndDeletedOnIsNull(String abbr);
    
    boolean existsByNameAndDeletedOnIsNull(String name);
    
    boolean existsByAbbrAndDeletedOnIsNull(String abbr);

    // ========== ROOT-LEVEL ATTRIBUTE UPDATES ==========
    
    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'name': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateOrganizationName(String organizationId, String name, Instant lastModifiedDate, String lastModifiedBy);
    
    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'abbr': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateOrganizationAbbr(String organizationId, String abbr, Instant lastModifiedDate, String lastModifiedBy);
    
    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'displayName': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateOrganizationDisplayName(String organizationId, String displayName, Instant lastModifiedDate, String lastModifiedBy);
    
    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'description': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateOrganizationDescription(String organizationId, String description, Instant lastModifiedDate, String lastModifiedBy);
    
    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'status': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateOrganizationStatus(String organizationId, OrganizationStatusEnum status, Instant lastModifiedDate, String lastModifiedBy);
    
    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'organizationType': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateOrganizationType(String organizationId, String organizationType, Instant lastModifiedDate, String lastModifiedBy);
    
    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'billingEmail': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateOrganizationBillingEmail(String organizationId, String billingEmail, Instant lastModifiedDate, String lastModifiedBy);
    
    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'expiresDate': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateOrganizationExpiresDate(String organizationId, Instant expiresDate, Instant lastModifiedDate, String lastModifiedBy);
    
    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'parentOrganizationId': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateOrganizationParent(String organizationId, String parentOrganizationId, Instant lastModifiedDate, String lastModifiedBy);

    // ========== STATUS-BASED QUERIES ==========
    
    List<Organization> findByStatusAndDeletedOnIsNull(OrganizationStatusEnum status);
    
    Page<Organization> findByStatusAndDeletedOnIsNull(OrganizationStatusEnum status, Pageable pageable);

    // ========== SEARCH AND FILTERING ==========
    
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

    // ========== AUTOCOMPLETE FUNCTIONALITY ==========
    
    @Query("{ 'deletedDate': null, 'status': { $in: ?0 }, " +
           "$or: [ " +
           "  { 'name': { $regex: ?1, $options: 'i' } }, " +
           "  { 'displayName': { $regex: ?1, $options: 'i' } } " +
           "] }")
    List<Organization> findForAutocomplete(List<OrganizationStatusEnum> statuses, String searchQuery, Pageable pageable);

    // ========== HIERARCHY MANAGEMENT ==========
    
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

    // ========== ACCESS CONTROL HELPERS ==========
    
    @Query("{ 'deletedDate': null, 'ownerUserIds': { $in: [?0] } }")
    List<Organization> findByOwnerUserId(String userId);
    
    @Query("{ 'deletedDate': null, 'ownerUserIds': { $in: [?0] } }")
    Page<Organization> findByOwnerUserId(String userId, Pageable pageable);

    // ========== UTILITY METHODS FOR VALIDATION ==========
    
    long countByParentOrganizationIdAndDeletedOnIsNull(String parentOrganizationId);
    
    boolean existsByParentOrganizationIdAndDeletedOnIsNull(String parentOrganizationId);
}