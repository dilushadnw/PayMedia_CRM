package com.paymedia.crm.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * PayMedia CRM - Organisation Entity
 * Module 4.1 - Organisation Management [BRD Page 8]
 * 
 * Implements No-Delete Architecture using status field
 */
@Entity
@Table(name = "organisation", indexes = {
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_company_name", columnList = "company_name")
})
@Data
public class Organisation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "company_name", nullable = false, length = 255)
    private String companyName;
    
    @Column(name = "trading_name", length = 255)
    private String tradingName;
    
    @Column(name = "registration_number", length = 100)
    private String registrationNumber;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "industry", length = 100)
    private String industry;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE"; // Default: ACTIVE (No-Delete Architecture)
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;
}
