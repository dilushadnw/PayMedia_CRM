package com.paymedia.crm.repository;

import com.paymedia.crm.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Contact Repository - Data Access Layer
 * Extends JpaRepository for standard CRUD operations
 * 
 * Phase 2: Contact Management Module
 * Provides database operations for Contact entity
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    /**
     * Find all active contacts
     * @param status Status value (typically "ACTIVE")
     * @return List of contacts with matching status
     */
    List<Contact> findAllByStatus(String status);
    
    /**
     * Find all active contacts for a specific organisation
     * @param organisationId Organisation ID
     * @param status Status value (typically "ACTIVE")
     * @return List of contacts belonging to the organisation
     */
    List<Contact> findByOrganisationIdAndStatus(Long organisationId, String status);
    
    /**
     * Find all contacts (including inactive) for a specific organisation
     * Useful for admin views
     * @param organisationId Organisation ID
     * @return List of all contacts for the organisation
     */
    List<Contact> findByOrganisationId(Long organisationId);
    
    /**
     * Find primary contact for an organisation
     * Only one contact should be primary per organisation
     * @param organisationId Organisation ID
     * @param isPrimary Primary flag (true)
     * @param status Status value (typically "ACTIVE")
     * @return Optional containing primary contact if exists
     */
    Optional<Contact> findByOrganisationIdAndIsPrimaryTrueAndStatus(Long organisationId, String status);
    
    /**
     * Find contact by email (case-insensitive)
     * Email is unique, so this returns a single contact
     * @param email Email address
     * @return Optional containing contact if found
     */
    Optional<Contact> findByEmailIgnoreCase(String email);
    
    /**
     * Find active contact by email
     * @param email Email address
     * @param status Status value (typically "ACTIVE")
     * @return Optional containing active contact if found
     */
    Optional<Contact> findByEmailIgnoreCaseAndStatus(String email, String status);
    
    /**
     * Search contacts by name (first or last name)
     * Case-insensitive search across both name fields
     * @param firstName First name search term
     * @param lastName Last name search term
     * @param status Status value (typically "ACTIVE")
     * @return List of matching contacts
     */
    @Query("SELECT c FROM Contact c WHERE c.status = :status AND " +
           "(LOWER(c.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')))")
    List<Contact> searchByName(@Param("firstName") String firstName, 
                               @Param("lastName") String lastName, 
                               @Param("status") String status);
    
    /**
     * Search active contacts by any field containing search term
     * @param searchTerm Search term to match against name, email, phone, position
     * @param status Status value (typically "ACTIVE")
     * @return List of matching contacts
     */
    @Query("SELECT c FROM Contact c WHERE c.status = :status AND " +
           "(LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.position) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Contact> searchContacts(@Param("searchTerm") String searchTerm, 
                                  @Param("status") String status);
    
    /**
     * Find contacts by department within an organisation
     * @param organisationId Organisation ID
     * @param department Department name
     * @param status Status value (typically "ACTIVE")
     * @return List of contacts in the department
     */
    List<Contact> findByOrganisationIdAndDepartmentIgnoreCaseAndStatus(
        Long organisationId, String department, String status);
    
    /**
     * Count active contacts for an organisation
     * @param organisationId Organisation ID
     * @param status Status value (typically "ACTIVE")
     * @return Number of contacts
     */
    long countByOrganisationIdAndStatus(Long organisationId, String status);
    
    /**
     * Check if email already exists (for unique validation)
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmailIgnoreCase(String email);
}
