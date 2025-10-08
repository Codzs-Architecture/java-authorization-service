package com.codzs.repository.acl;

import com.codzs.entity.acl.AclObjectIdentity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AclObjectIdentity MongoDB documents.
 * Provides methods for managing ACL object identities.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Repository
public interface AclObjectIdentityRepository extends MongoRepository<AclObjectIdentity, String> {

    /**
     * Find ACL object identity by object class and identity.
     * 
     * @param objectIdClass the object ID class (reference to AclClass)
     * @param objectIdIdentity the object ID identity
     * @return Optional containing the ACL object identity if found
     */
    Optional<AclObjectIdentity> findByObjectIdClassAndObjectIdIdentity(String objectIdClass, String objectIdIdentity);

    /**
     * Find all ACL object identities for a specific class.
     * 
     * @param objectIdClass the object ID class (reference to AclClass)
     * @return list of ACL object identities for the class
     */
    List<AclObjectIdentity> findByObjectIdClass(String objectIdClass);

    /**
     * Find all ACL object identities owned by a specific SID.
     * 
     * @param ownerSid the owner SID (reference to AclSid)
     * @return list of ACL object identities owned by the SID
     */
    List<AclObjectIdentity> findByOwnerSid(String ownerSid);

    /**
     * Find all child ACL object identities for a parent.
     * 
     * @param parentObject the parent object (reference to AclObjectIdentity)
     * @return list of child ACL object identities
     */
    List<AclObjectIdentity> findByParentObject(String parentObject);

    /**
     * Find all root ACL object identities (no parent).
     * 
     * @return list of root ACL object identities
     */
    List<AclObjectIdentity> findByParentObjectIsNull();

    /**
     * Find ACL object identities with entries inheriting enabled.
     * 
     * @return list of ACL object identities with inheritance enabled
     */
    List<AclObjectIdentity> findByEntriesInheritingTrue();

    /**
     * Find ACL object identities with entries inheriting disabled.
     * 
     * @return list of ACL object identities with inheritance disabled
     */
    List<AclObjectIdentity> findByEntriesInheritingFalse();

    /**
     * Check if an ACL object identity exists by object class and identity.
     * 
     * @param objectIdClass the object ID class
     * @param objectIdIdentity the object ID identity
     * @return true if the ACL object identity exists
     */
    boolean existsByObjectIdClassAndObjectIdIdentity(String objectIdClass, String objectIdIdentity);

    /**
     * Delete ACL object identity by object class and identity.
     * 
     * @param objectIdClass the object ID class
     * @param objectIdIdentity the object ID identity
     * @return number of deleted records
     */
    long deleteByObjectIdClassAndObjectIdIdentity(String objectIdClass, String objectIdIdentity);

    /**
     * Delete all ACL object identities for a specific class.
     * 
     * @param objectIdClass the object ID class
     * @return number of deleted records
     */
    long deleteByObjectIdClass(String objectIdClass);

    /**
     * Delete all ACL object identities owned by a specific SID.
     * 
     * @param ownerSid the owner SID
     * @return number of deleted records
     */
    long deleteByOwnerSid(String ownerSid);

    /**
     * Count ACL object identities for a specific class.
     * 
     * @param objectIdClass the object ID class
     * @return count of ACL object identities for the class
     */
    long countByObjectIdClass(String objectIdClass);

    /**
     * Count ACL object identities owned by a specific SID.
     * 
     * @param ownerSid the owner SID
     * @return count of ACL object identities owned by the SID
     */
    long countByOwnerSid(String ownerSid);
}