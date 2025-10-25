package com.codzs.repository.organization;

import com.codzs.entity.organization.Organization;
import com.codzs.entity.organization.OrganizationSettings;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
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
    void updateAllSettings(String organizationId, OrganizationSettings settings, Instant lastModifiedDate, String lastModifiedBy);

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

}