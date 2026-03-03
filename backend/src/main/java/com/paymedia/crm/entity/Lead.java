package com.paymedia.crm.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Lead Entity - Phase 3: Lead Management Module
 * Represents sales leads and opportunities
 * 
 * Business Rules:
 * - Lead can be linked to an existing Organisation (optional)
 * - Lead can be linked to an existing Contact (optional)
 * - Lead progresses through stages: New → Qualified → Proposal → Negotiation → Closed Won/Lost
 * - No-Delete Architecture: Leads are soft-deleted (status = 'INACTIVE')
 * - Estimated value and probability help track potential revenue
 */
@Entity
@Table(name = "lead", indexes = {
    @Index(name = "idx_lead_stage", columnList = "lead_stage"),
    @Index(name = "idx_lead_status", columnList = "status"),
    @Index(name = "idx_lead_source", columnList = "lead_source"),
    @Index(name = "idx_expected_close", columnList = "expected_close_date"),
    @Index(name = "idx_organisation", columnList = "organisation_id"),
    @Index(name = "idx_contact", columnList = "contact_id")
})
@Data
public class Lead {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Foreign Key: Link to Organisation entity (Optional)
     * Many Leads -> One Organisation
     * LAZY loading for performance
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;
    
    /**
     * Foreign Key: Link to Contact entity (Optional)
     * Many Leads -> One Contact
     * LAZY loading for performance
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;
    
    /**
     * Lead Information
     */
    @Column(name = "lead_name", nullable = false, length = 255)
    private String leadName;
    
    /**
     * Lead Source
     * Examples: Website, Referral, Cold Call, Email Campaign, Social Media, Trade Show
     */
    @Column(name = "lead_source", length = 100)
    private String leadSource;
    
    /**
     * Lead Stage
     * Life cycle: New → Qualified → Proposal → Negotiation → Closed Won / Closed Lost
     */
    @Column(name = "lead_stage", nullable = false, length = 50)
    private String leadStage = "New";
    
    /**
     * Financial Information
     */
    @Column(name = "estimated_value", precision = 15, scale = 2)
    private BigDecimal estimatedValue;
    
    /**
     * Win Probability (0-100)
     * Helps forecast expected revenue
     */
    @Column(name = "probability")
    private Integer probability;
    
    /**
     * Expected Close Date
     * Target date for closing the deal
     */
    @Column(name = "expected_close_date")
    private LocalDate expectedCloseDate;
    
    /**
     * Actual Close Date
     * Populated when lead is won or lost
     */
    @Column(name = "actual_close_date")
    private LocalDate actualCloseDate;
    
    /**
     * Additional Details
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    /**
     * Company Information (if not linked to existing org)
     * These fields are used when lead is from a new company not yet in the system
     */
    @Column(name = "company_name", length = 255)
    private String companyName;
    
    @Column(name = "industry", length = 100)
    private String industry;
    
    /**
     * Contact Information (if not linked to existing contact)
     */
    @Column(name = "contact_name", length = 200)
    private String contactName;
    
    @Column(name = "contact_email", length = 255)
    private String contactEmail;
    
    @Column(name = "contact_phone", length = 50)
    private String contactPhone;
    
    /**
     * No-Delete Architecture
     * Status: ACTIVE or INACTIVE
     * Records are never hard-deleted from database
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";
    
    /**
     * Audit Fields
     * Automatically populated by Hibernate
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;
    
    /**
     * Convenience method to calculate expected revenue
     * = Estimated Value × Probability
     */
    @Transient
    public BigDecimal getExpectedRevenue() {
        if (estimatedValue == null || probability == null) {
            return BigDecimal.ZERO;
        }
        return estimatedValue.multiply(BigDecimal.valueOf(probability)).divide(BigDecimal.valueOf(100));
    }
    
    /**
     * Check if lead is closed
     */
    @Transient
    public boolean isClosed() {
        return "Closed Won".equalsIgnoreCase(leadStage) || "Closed Lost".equalsIgnoreCase(leadStage);
    }
    
    /**
     * Check if lead is won
     */
    @Transient
    public boolean isWon() {
        return "Closed Won".equalsIgnoreCase(leadStage);
    }
}
