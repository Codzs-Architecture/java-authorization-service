package com.codzs.repository.acl;

import com.codzs.entity.acl.AclSid;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AclSid MongoDB documents.
 * Provides methods for managing ACL security identities.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Repository
public interface AclSidRepository extends MongoRepository<AclSid, String> {

    /**
     * Find ACL SID by SID value and principal flag.
     * 
     * @param sid the SID value
     * @param principal the principal flag
     * @return Optional containing the ACL SID if found
     */
    Optional<AclSid> findBySidAndPrincipal(String sid, Boolean principal);

    /**
     * Find ACL SID by SID value.
     * 
     * @param sid the SID value
     * @return list of ACL SIDs with the SID value
     */
    List<AclSid> findBySid(String sid);

    /**
     * Find all principal SIDs.
     * 
     * @return list of principal ACL SIDs
     */
    List<AclSid> findByPrincipalTrue();

    /**
     * Find all authority SIDs (non-principal).
     * 
     * @return list of authority ACL SIDs
     */
    List<AclSid> findByPrincipalFalse();

    /**
     * Check if an ACL SID exists by SID and principal flag.
     * 
     * @param sid the SID value
     * @param principal the principal flag
     * @return true if the ACL SID exists
     */
    boolean existsBySidAndPrincipal(String sid, Boolean principal);

    /**
     * Delete ACL SID by SID and principal flag.
     * 
     * @param sid the SID value
     * @param principal the principal flag
     * @return number of deleted records
     */
    long deleteBySidAndPrincipal(String sid, Boolean principal);

    /**
     * Delete all ACL SIDs by SID value.
     * 
     * @param sid the SID value
     * @return number of deleted records
     */
    long deleteBySid(String sid);

    /**
     * Count principal SIDs.
     * 
     * @return count of principal ACL SIDs
     */
    long countByPrincipalTrue();

    /**
     * Count authority SIDs.
     * 
     * @return count of authority ACL SIDs
     */
    long countByPrincipalFalse();
}