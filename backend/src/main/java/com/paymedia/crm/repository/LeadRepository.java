package com.paymedia.crm.repository;

import com.paymedia.crm.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Lead Repository - Data Access Layer
 * Phase 3: Lead Management Module
 * 
 * Provides database operations for Lead entity
 */
@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    
    /**
     * Find all active leads
     */
    List<Lead> findAllByStatus(String status);
    
    /**
     * Find all active leads by stage
     */
    List<Lead> findByLeadStageAndStatus(String leadStage, String status);
    
    /**
     * Find all active leads by source
     */
    List<Lead> findByLeadSourceAndStatus(String leadSource, String status);
    
    /**
     * Find all active leads for an organisation
     */
    List<Lead> findByOrganisationIdAndStatus(Long organisationId, String status);
    
    /**
     * Find all leads for an organisation (including inactive)
     */
    List<Lead> findByOrganisationId(Long organisationId);
    
    /**
     * Find all active leads for a contact
     */
    List<Lead> findByContactIdAndStatus(Long contactId, String status);
    
    /**
     * Find leads closing within a date range
     */
    @Query("SELECT l FROM Lead l WHERE l.status = :status AND " +
           "l.expectedCloseDate BETWEEN :startDate AND :endDate")
    List<Lead> findLeadsClosingBetween(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate,
                                        @Param("status") String status);
    
    /**
     * Find open leads (not closed)
     */
    @Query("SELECT l FROM Lead l WHERE l.status = :status AND " +
           "l.leadStage NOT IN ('Closed Won', 'Closed Lost')")
    List<Lead> findOpenLeads(@Param("status") String status);
    
    /**
     * Find won leads
     */
    @Query("SELECT l FROM Lead l WHERE l.status = :status AND l.leadStage = 'Closed Won'")
    List<Lead> findWonLeads(@Param("status") String status);
    
    /**
     * Find lost leads
     */
    @Query("SELECT l FROM Lead l WHERE l.status = :status AND l.leadStage = 'Closed Lost'")
    List<Lead> findLostLeads(@Param("status") String status);
    
    /**
     * Search leads by name, company name, or contact name
     */
    @Query("SELECT l FROM Lead l WHERE l.status = :status AND " +
           "(LOWER(l.leadName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(l.companyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(l.contactName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Lead> searchLeads(@Param("searchTerm") String searchTerm,
                           @Param("status") String status);
    
    /**
     * Find leads with estimated value greater than specified amount
     */
    @Query("SELECT l FROM Lead l WHERE l.status = :status AND l.estimatedValue >= :minValue")
    List<Lead> findLeadsWithValueGreaterThan(@Param("minValue") BigDecimal minValue,
                                              @Param("status") String status);
    
    /**
     * Calculate total estimated value for active leads
     */
    @Query("SELECT COALESCE(SUM(l.estimatedValue), 0) FROM Lead l WHERE l.status = 'ACTIVE'")
    BigDecimal calculateTotalEstimatedValue();
    
    /**
     * Calculate total estimated value for leads in a specific stage
     */
    @Query("SELECT COALESCE(SUM(l.estimatedValue), 0) FROM Lead l " +
           "WHERE l.status = 'ACTIVE' AND l.leadStage = :stage")
    BigDecimal calculateTotalValueByStage(@Param("stage") String stage);
    
    /**
     * Calculate total won revenue
     */
    @Query("SELECT COALESCE(SUM(l.estimatedValue), 0) FROM Lead l " +
           "WHERE l.status = 'ACTIVE' AND l.leadStage = 'Closed Won'")
    BigDecimal calculateTotalWonRevenue();
    
    /**
     * Calculate expected revenue (sum of estimated_value * probability / 100)
     */
    @Query("SELECT COALESCE(SUM(l.estimatedValue * l.probability / 100), 0) FROM Lead l " +
           "WHERE l.status = 'ACTIVE' AND l.leadStage NOT IN ('Closed Won', 'Closed Lost')")
    BigDecimal calculateExpectedRevenue();
    
    /**
     * Count leads by stage
     */
    long countByLeadStageAndStatus(String leadStage, String status);
    
    /**
     * Count total active leads
     */
    long countByStatus(String status);
    
    /**
     * Count won leads
     */
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.status = 'ACTIVE' AND l.leadStage = 'Closed Won'")
    long countWonLeads();
    
    /**
     * Count lost leads
     */
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.status = 'ACTIVE' AND l.leadStage = 'Closed Lost'")
    long countLostLeads();
    
    /**
     * Calculate win rate (won / (won + lost) * 100)
     * Returns percentage as a double
     */
    @Query("SELECT CASE WHEN (COUNT(l) FILTER (WHERE l.leadStage = 'Closed Won') + " +
           "COUNT(l) FILTER (WHERE l.leadStage = 'Closed Lost')) = 0 THEN 0 " +
           "ELSE (COUNT(l) FILTER (WHERE l.leadStage = 'Closed Won') * 100.0 / " +
           "(COUNT(l) FILTER (WHERE l.leadStage = 'Closed Won') + " +
           "COUNT(l) FILTER (WHERE l.leadStage = 'Closed Lost'))) END " +
           "FROM Lead l WHERE l.status = 'ACTIVE'")
    Double calculateWinRate();
    
    /**
     * Find overdue leads (expected close date passed, still open)
     */
    @Query("SELECT l FROM Lead l WHERE l.status = 'ACTIVE' AND " +
           "l.expectedCloseDate < :today AND " +
           "l.leadStage NOT IN ('Closed Won', 'Closed Lost')")
    List<Lead> findOverdueLeads(@Param("today") LocalDate today);
    
    /**
     * Find leads closing this month
     */
    @Query("SELECT l FROM Lead l WHERE l.status = 'ACTIVE' AND " +
           "YEAR(l.expectedCloseDate) = :year AND MONTH(l.expectedCloseDate) = :month")
    List<Lead> findLeadsClosingInMonth(@Param("year") int year, @Param("month") int month);
}
