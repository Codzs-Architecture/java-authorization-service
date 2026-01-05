package com.codzs.repository.acl;

import com.codzs.entity.acl.AclClass;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AclClass MongoDB documents.
 * Provides methods for managing ACL object classes.
 * 
 * @author Nitin Khaitan
 * @since 1.2
 */
@Repository
public interface AclClassRepository extends MongoRepository<AclClass, String> {

    /**
     * Find ACL class by class name.
     * 
     * @param className the class name
     * @return Optional containing the ACL class if found
     */
    Optional<AclClass> findByClassName(String className);

    /**
     * Check if an ACL class exists by class name.
     * 
     * @param className the class name
     * @return true if the ACL class exists
     */
    boolean existsByClassName(String className);

    /**
     * Delete ACL class by class name.
     * 
     * @param className the class name
     * @return number of deleted records
     */
    long deleteByClassName(String className);

    /**
     * Find ACL classes by class name pattern (case-insensitive partial match).
     * 
     * @param className the class name pattern to search for
     * @return list of matching ACL classes
     */
    @Query("{ 'className': { $regex: ?0, $options: 'i' } }")
    List<AclClass> findByClassNameContainingIgnoreCase(String className);

    /**
     * Find all distinct class names.
     * 
     * @return list of distinct class names
     */
    @Query(value = "{}", fields = "{ 'className': 1 }")
    List<String> findDistinctClassNames();
}