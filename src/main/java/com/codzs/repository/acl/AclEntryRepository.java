package com.codzs.repository.acl;

import com.codzs.entity.acl.AclEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AclEntry MongoDB documents.
 * Provides methods for managing ACL entries.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Repository
public interface AclEntryRepository extends MongoRepository<AclEntry, String> {

    /**
     * Find ACL entry by object identity and ACE order.
     * 
     * @param aclObjectIdentity the ACL object identity (reference to AclObjectIdentity)
     * @param aceOrder the ACE order
     * @return Optional containing the ACL entry if found
     */
    Optional<AclEntry> findByAclObjectIdentityAndAceOrder(String aclObjectIdentity, Integer aceOrder);

    /**
     * Find all ACL entries for a specific object identity.
     * 
     * @param aclObjectIdentity the ACL object identity
     * @return list of ACL entries for the object identity ordered by ACE order
     */
    @Query(value = "{ 'aclObjectIdentity': ?0 }", sort = "{ 'aceOrder': 1 }")
    List<AclEntry> findByAclObjectIdentityOrderByAceOrder(String aclObjectIdentity);

    /**
     * Find all ACL entries for a specific SID.
     * 
     * @param sid the SID (reference to AclSid)
     * @return list of ACL entries for the SID
     */
    List<AclEntry> findBySid(String sid);

    /**
     * Find all granting ACL entries for a specific object identity and SID.
     * 
     * @param aclObjectIdentity the ACL object identity
     * @param sid the SID
     * @return list of granting ACL entries
     */
    List<AclEntry> findByAclObjectIdentityAndSidAndGrantingTrue(String aclObjectIdentity, String sid);

    /**
     * Find all denying ACL entries for a specific object identity and SID.
     * 
     * @param aclObjectIdentity the ACL object identity
     * @param sid the SID
     * @return list of denying ACL entries
     */
    List<AclEntry> findByAclObjectIdentityAndSidAndGrantingFalse(String aclObjectIdentity, String sid);

    /**
     * Find ACL entries by mask (permission).
     * 
     * @param mask the permission mask
     * @return list of ACL entries with the mask
     */
    List<AclEntry> findByMask(Integer mask);

    /**
     * Find ACL entries by mask and granting status.
     * 
     * @param mask the permission mask
     * @param granting the granting status
     * @return list of ACL entries with the mask and granting status
     */
    List<AclEntry> findByMaskAndGranting(Integer mask, Boolean granting);

    /**
     * Find all granting ACL entries.
     * 
     * @return list of granting ACL entries
     */
    List<AclEntry> findByGrantingTrue();

    /**
     * Find all denying ACL entries.
     * 
     * @return list of denying ACL entries
     */
    List<AclEntry> findByGrantingFalse();

    /**
     * Find ACL entries with audit success enabled.
     * 
     * @return list of ACL entries with audit success enabled
     */
    List<AclEntry> findByAuditSuccessTrue();

    /**
     * Find ACL entries with audit failure enabled.
     * 
     * @return list of ACL entries with audit failure enabled
     */
    List<AclEntry> findByAuditFailureTrue();

    /**
     * Check if an ACL entry exists by object identity and ACE order.
     * 
     * @param aclObjectIdentity the ACL object identity
     * @param aceOrder the ACE order
     * @return true if the ACL entry exists
     */
    boolean existsByAclObjectIdentityAndAceOrder(String aclObjectIdentity, Integer aceOrder);

    /**
     * Delete ACL entry by object identity and ACE order.
     * 
     * @param aclObjectIdentity the ACL object identity
     * @param aceOrder the ACE order
     * @return number of deleted records
     */
    long deleteByAclObjectIdentityAndAceOrder(String aclObjectIdentity, Integer aceOrder);

    /**
     * Delete all ACL entries for a specific object identity.
     * 
     * @param aclObjectIdentity the ACL object identity
     * @return number of deleted records
     */
    long deleteByAclObjectIdentity(String aclObjectIdentity);

    /**
     * Delete all ACL entries for a specific SID.
     * 
     * @param sid the SID
     * @return number of deleted records
     */
    long deleteBySid(String sid);

    /**
     * Count ACL entries for a specific object identity.
     * 
     * @param aclObjectIdentity the ACL object identity
     * @return count of ACL entries for the object identity
     */
    long countByAclObjectIdentity(String aclObjectIdentity);

    /**
     * Count ACL entries for a specific SID.
     * 
     * @param sid the SID
     * @return count of ACL entries for the SID
     */
    long countBySid(String sid);

    /**
     * Find the maximum ACE order for a specific object identity.
     * 
     * @param aclObjectIdentity the ACL object identity
     * @return the maximum ACE order, or 0 if no entries exist
     */
    @Query(value = "{ 'aclObjectIdentity': ?0 }", fields = "{ 'aceOrder': 1 }", sort = "{ 'aceOrder': -1 }")
    Optional<AclEntry> findTopByAclObjectIdentityOrderByAceOrderDesc(String aclObjectIdentity);
}