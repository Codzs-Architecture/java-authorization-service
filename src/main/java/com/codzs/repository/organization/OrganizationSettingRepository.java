package com.codzs.repository.organization;

import com.codzs.entity.organization.Organization;
import com.codzs.entity.organization.OrganizationSetting;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

/**
 * Repository interface for Organization setting operations.
 * Provides specialized methods for managing setting within organizations.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@Repository
public interface OrganizationSettingRepository extends MongoRepository<Organization, String> {

    // Setting retrieval
    @Query(value = "{ '_id': ?0, 'deletedDate': null }", fields = "{ 'setting': 1 }")
    Optional<Organization> findSettingById(String organizationId);

    // Bulk setting update using MongoDB update operators
    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'setting': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateAllSetting(String organizationId, OrganizationSetting setting, Instant lastModifiedDate, String lastModifiedBy);

    // Individual setting field updates
    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'setting.timezone': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateTimezone(String organizationId, String timezone, Instant lastModifiedDate, String lastModifiedBy);

    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'setting.currency': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateCurrency(String organizationId, String currency, Instant lastModifiedDate, String lastModifiedBy);

    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'setting.language': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateLanguage(String organizationId, String language, Instant lastModifiedDate, String lastModifiedBy);

    @Query("{ '_id': ?0, 'deletedDate': null }")
    @Update("{ '$set': { 'setting.country': ?1, 'lastModifiedDate': ?2, 'lastModifiedBy': ?3 } }")
    void updateCountry(String organizationId, String country, Instant lastModifiedDate, String lastModifiedBy);

}