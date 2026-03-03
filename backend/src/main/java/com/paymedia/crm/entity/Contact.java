package com.paymedia.crm.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Contact Entity - Phase 2: Contact Management Module
 * Represents contact persons associated with organisations
 * 
 * Business Rules:
 * - One Organisation can have Many Contacts (1:N relationship)
 * - Every Contact must belong to one Organisation
 * - Email must be unique across all contacts
 * - One contact can be designated as Primary Contact per organisation
 * - No-Delete Architecture: Contacts are soft-deleted (status = 'INACTIVE')
 */
@Entity
@Table(name = "contact", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_organisation", columnList = "organisation_id"),
    @Index(name = "idx_is_primary", columnList = "is_primary")
}, uniqueConstraints = {
    @UniqueConstraint(name = "unique_email", columnNames = "email")
})
@Data
public class Contact {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Foreign Key: Link to Organisation entity
     * Many Contacts -> One Organisation
     * LAZY loading for performance optimization
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;
    
    /**
     * Personal Information
     */
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;
    
    /**
     * Contact Information
     * Email is unique and required
     */
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(name = "phone", length = 50)
    private String phone;
    
    @Column(name = "mobile", length = 50)
    private String mobile;
    
    /**
     * Professional Information
     */
    @Column(name = "position", length = 100)
    private String position;
    
    @Column(name = "department", length = 100)
    private String department;
    
    /**
     * Primary Contact Flag
     * Only one contact per organisation should be marked as primary
     */
    @Column(name = "is_primary")
    private Boolean isPrimary = false;
    
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
     * Convenience method to get full name
     */
    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
