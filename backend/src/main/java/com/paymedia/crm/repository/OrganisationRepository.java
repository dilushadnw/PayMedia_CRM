package com.paymedia.crm.repository;

import com.paymedia.crm.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PayMedia CRM - Organisation Repository
 * Module 4.1 - Organisation Management [BRD Page 8]
 * 
 * Provides data access methods for Organisation entity
 */
@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {
    
    /**
     * Find all organisations by status
     * Used to retrieve only ACTIVE organisations (No-Delete Architecture)
     * 
     * @param status The status to filter by (e.g., "ACTIVE", "INACTIVE")
     * @return List of organisations with the specified status
     */
    List<Organisation> findAllByStatus(String status);
}
