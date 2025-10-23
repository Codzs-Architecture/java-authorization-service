package com.codzs.repository.organization;

import com.codzs.entity.organization.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Organization settings operations.
 * Provides specialized methods for managing settings within organizations.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Repository
public interface OrganizationSettingsRepository extends MongoRepository<Organization, String> {

    // Settings retrieval
    @Query(value = "{ '_id': ?0, 'deletedDate': null }", fields = "{ 'settings': 1 }")
    Optional<Organization> findSettingsById(String organizationId);

    // Bulk settings update using MongoDB update operators
    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'settings': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateAllSettings(String organizationId, Object settings, Instant lastModifiedDate, String lastModifiedBy);

    // Individual settings field updates
    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'settings.timezone': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateTimezone(String organizationId, String timezone, Instant lastModifiedDate, String lastModifiedBy);

    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'settings.currency': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateCurrency(String organizationId, String currency, Instant lastModifiedDate, String lastModifiedBy);

    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'settings.language': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateLanguage(String organizationId, String language, Instant lastModifiedDate, String lastModifiedBy);

    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'settings.country': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateCountry(String organizationId, String country, Instant lastModifiedDate, String lastModifiedBy);

    // Settings analytics and reporting
    @Query("{ 'deletedDate': null, 'settings.timezone': ?0 }")
    List<Organization> findByTimezone(String timezone);
    
    @Query("{ 'deletedDate': null, 'settings.currency': ?0 }")
    List<Organization> findByCurrency(String currency);
    
    @Query("{ 'deletedDate': null, 'settings.country': ?0 }")
    List<Organization> findByCountry(String country);

    // Settings statistics
    @Query("{ 'deletedDate': null, 'settings.timezone': ?0 }")
    long countByTimezone(String timezone);
    
    @Query("{ 'deletedDate': null, 'settings.currency': ?0 }")
    long countByCurrency(String currency);
    
    @Query("{ 'deletedDate': null, 'settings.country': ?0 }")
    long countByCountry(String country);

    // Organizations missing certain settings
    @Query("{ 'deletedDate': null, 'settings.timezone': { '$exists': false } }")
    List<Organization> findWithoutTimezone();
    
    @Query("{ 'deletedDate': null, 'settings.currency': { '$exists': false } }")
    List<Organization> findWithoutCurrency();
    
    @Query("{ 'deletedDate': null, 'settings.language': { '$exists': false } }")
    List<Organization> findWithoutLanguage();
}