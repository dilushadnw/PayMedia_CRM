# PayMedia CRM - Complete Developer Instructions v2.0 📚

**Role:** Senior Full Stack Developer  
**Project:** PayMedia Enterprise CRM & Project Lifecycle System  
**Technology:** Java 21, Spring Boot 3.2, React 18, H2/MySQL Database  
**Architecture:** Microservices-Ready, RESTful API-First, MVC Pattern  
**Last Updated:** March 3, 2026

---

## 🎯 Table of Contents

1. [Project Overview](#1-project-overview)
2. [Technology Stack](#2-technology-stack)
3. [Critical Business Rules](#3-critical-business-rules)
4. [System Architecture](#4-system-architecture)
5. [Database Design](#5-database-design)
6. [Backend Development Guide](#6-backend-development-guide)
7. [Frontend Development Guide](#7-frontend-development-guide)
8. [API Documentation](#8-api-documentation)
9. [Security & Authentication](#9-security--authentication)
10. [Testing Strategy](#10-testing-strategy)
11. [Deployment Guide](#11-deployment-guide)
12. [Phase-wise Implementation](#12-phase-wise-implementation)
13. [Best Practices & Standards](#13-best-practices--standards)
14. [Quick Reference](#14-quick-reference)
15. [Troubleshooting](#15-troubleshooting)

---

## 1. Project Overview

### 1.1 System Purpose

PayMedia CRM is an enterprise-grade Customer Relationship Management system designed to streamline business operations across multiple modules:

**Implemented Modules:**
- ✅ **Module 4.1:** Organisation Management (ACTIVE)

**Planned Modules:**
- 📋 **Module 4.2:** Contact Management
- 📊 **Module 4.3:** Lead Management
- 💼 **Module 4.4:** Opportunity Tracking
- 🎯 **Module 4.5:** Sales Pipeline Management
- 📈 **Module 4.6:** Project Lifecycle Management
- 📧 **Module 4.7:** Email Integration
- 📄 **Module 4.8:** Document Management
- 📊 **Module 4.9:** Reports & Analytics
- 👥 **Module 4.10:** User Management & RBAC

### 1.2 Current Implementation Status

| Feature | Status | Details |
|---------|--------|---------|
| Organisation CRUD | ✅ Complete | Create,Read, Update, Soft Delete |
| H2 Database | ✅ Active | In-memory development DB |
| MySQL Support | ✅ Configured | Production-ready |
| React Frontend | ✅ Complete | Bootstrap UI implemented |
| REST API | ✅ Complete | CORS-enabled endpoints |
| No-Delete Architecture | ✅ Implemented | Soft delete across system |
| Audit Fields | ✅ Implemented | Created/Modified timestamps |
| Sample Data | ✅ Available | 3 test organisations |

### 1.3 System Benefits

- **No Data Loss:** Soft delete architecture preserves all data
- **Scalable:** Microservices-ready architecture
- **Modern Stack:** Latest Java 21 & React 18
- **Developer Friendly:** H2 for quick dev, MySQL for production
- **API-First:** RESTful design for future integrations
- **Responsive UI:** Bootstrap 5 mobile-friendly interface

---

## 2. Technology Stack

### 2.1 Backend Technologies

| Component | Technology | Version | Why? |
|-----------|-----------|---------|------|
| **Language** | Java | 21 LTS | Long-term support, modern features |
| **Framework** | Spring Boot | 3.2.0 | Rapid development, production-ready |
| **Build Tool** | Maven | 3.9+ | Dependency management, standardized builds |
| **ORM** | Hibernate/JPA | 6.3.1 | Database abstraction, object mapping |
| **Database (Dev)** | H2 | In-Memory | Fast startup, no installation needed |
| **Database (Prod)** | MySQL | 8.0+ | Reliable, widely supported |
| **API Style** | REST | - | HTTP-based, stateless, cacheable |
| **Validation** | Bean Validation | 3.0 | Declarative validation |
| **Logging** | SLF4J/Logback | - | Flexible, performant logging |
| **Data Mapper** | Lombok | 1.18.30 | Reduce boilerplate code |

**Maven Dependencies:**
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### 2.2 Frontend Technologies

| Component | Technology | Version | Why? |
|-----------|-----------|---------|------|
| **Framework** | React | 18.2+ | Component-based, virtual DOM, huge ecosystem |
| **HTTP Client** | Axios | 1.4+ | Promise-based, interceptors, request/response transformations |
| **UI Library** | Bootstrap | 5.3+ | Responsive grid, pre-built components |
| **Icons** | Bootstrap Icons | 1.10+ | Consistent icon set |
| **Build Tool** | React Scripts | 5.0+ | Zero configuration, optimized builds |
| **State Management** | React Hooks | Built-in | useState, useEffect for component state |

**Package.json:**
```json
{
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "axios": "^1.4.0",
    "bootstrap": "^5.3.0",
    "bootstrap-icons": "^1.10.5"
  }
}
```

### 2.3 Development Tools

| Tool | Purpose |
|------|---------|
| **VS Code / IntelliJ IDEA** | Code editing |
| **Postman / Thunder Client** | API testing |
| **Git** | Version control |
| **Maven / npm** | Package management |
| **H2 Console** | Database inspection |
| **Browser DevTools** | Frontend debugging |

---

## 3. Critical Business Rules

### 3.1 ⚠️ No-Delete Architecture (MANDATORY)

**Rule:** NO record can EVER be permanently deleted from the database.

**❌ NEVER DO THIS:**
```java
repository.delete(entity);
repository.deleteById(id);
entityManager.remove(entity);
```

**✅ ALWAYS DO THIS:**
```java
entity.setStatus("INACTIVE");
repository.save(entity);
```

**Why This Rule Exists:**
1. **Audit Trail:** Complete history of all data changes
2. **Data Recovery:** Ability to restore accidentally "deleted" records
3. **Relationships:** Preserve foreign key integrity
4. **Compliance:** Meet data retention regulations
5. **Analytics:** Historical data for reporting

**Implementation Pattern:**
```java
// Service Layer
public boolean softDeleteOrganisation(Long id) {
    return repository.findById(id).map(org -> {
        org.setStatus("INACTIVE");  // Soft delete
        repository.save(org);
        return true;
    }).orElse(false);
}

// Controller Layer
@DeleteMapping("/{id}")
public ResponseEntity<HttpStatus> softDelete(@PathVariable Long id) {
    return service.softDeleteOrganisation(id)
        ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
}
```

### 3.2 Single Source of Truth

**Rule:** All data must be consistent, validated, and centralized.

**Implementation:**
- Use database constraints (`NOT NULL`, `UNIQUE`, `FOREIGN KEY`)
- Validate in Service layer before persistence
- Return only `ACTIVE` records by default
- Use transactions for multi-step operations

```java
@Service
@Transactional  // Ensures data consistency
public class OrganisationService {
    
    public List<Organisation> getAllActiveOrganisations() {
        return repository.findAllByStatus("ACTIVE");  // Only active records
    }
}
```

### 3.3 Data Validation Rules

| Field | Validation | Error Message |
|-------|-----------|---------------|
| Company Name | Required, 1-255 chars | "Company name is required" |
| Email | Valid email format | "Invalid email format" |
| Phone | 10-15 digits | "Invalid phone number" |
| Registration Number | Unique across system | "Registration number already exists" |
| Industry | Must be from predefined list | "Invalid industry selection" |

**Implementation:**
```java
@Entity
public class Organisation {
    
    @Column(name = "company_name", nullable = false, length = 255)
    @NotBlank(message = "Company name is required")
    private String companyName;
    
    @Email(message = "Invalid email format")
    private String email;
    
    @Pattern(regexp = "^\\d{10,15}$", message = "Invalid phone number")
    private String phone;
}
```

### 3.4 Mandatory Audit Fields

**Every entity MUST have these fields:**

```java
@CreationTimestamp
@Column(name = "created_at", nullable = false, updatable = false)
private LocalDateTime createdAt;

@UpdateTimestamp
@Column(name = "modified_at", nullable = false)
private LocalDateTime modifiedAt;

// Future: User tracking
private String createdBy;
private String modifiedBy;
```

**Why:**
- Track when records were created
- Track when records were last modified
- Future: Track WHO made changes
- Support audit reports

---

## 4. System Architecture

### 4.1 Layered Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│              FRONTEND LAYER (React)                     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐   │
│  │ Components  │  │  Services   │  │    State    │   │
│  └─────────────┘  └─────────────┘  └─────────────┘   │
└──────────────────────┬──────────────────────────────────┘
                       │ HTTP/REST (Axios)
┌──────────────────────▼──────────────────────────────────┐
│           API LAYER (@RestController)                   │
│  ┌──────────────────────────────────────────────┐      │
│  │ Request Validation, CORS, Exception Handling │      │
│  └──────────────────────────────────────────────┘      │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│         BUSINESS LAYER (@Service)                       │
│  ┌──────────────────────────────────────────────┐      │
│  │ Business Logic, Transactions, Soft Delete    │      │
│  └──────────────────────────────────────────────┘      │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│      DATA ACCESS LAYER (@Repository)                    │
│  ┌──────────────────────────────────────────────┐      │
│  │ JPA/Hibernate, Custom Queries, CRUD          │      │
│  └──────────────────────────────────────────────┘      │
└──────────────────────┬──────────────────────────────────┘
                       │ JDBC
┌──────────────────────▼──────────────────────────────────┐
│              DATABASE (H2/MySQL)                        │
│  ┌──────────────────────────────────────────────┐      │
│  │ Tables, Indexes, Constraints, Relationships  │      │
│  └──────────────────────────────────────────────┘      │
└─────────────────────────────────────────────────────────┘
```

### 4.2 Package Structure

```
com.paymedia.crm/
├── PayMediaCrmApplication.java          # Main application entry point
│
├── config/                              # Configuration classes
│   ├── CorsConfig.java                  # CORS configuration
│   ├── SecurityConfig.java              # Security (Future)
│   └── DatabaseConfig.java              # Database settings
│
├── entity/                              # JPA Entities
│   ├── Organisation.java                # ✅ Organisation entity
│   ├── Contact.java                     # 📋 Contact entity (Planned)
│   ├── Lead.java                        # 📋 Lead entity (Planned)
│   └── BaseEntity.java                  # 📋 Base entity with audit fields
│
├── repository/                          # Data access layer
│   ├── OrganisationRepository.java      # ✅ Organisation repository
│   ├── ContactRepository.java           # 📋 (Planned)
│   └── LeadRepository.java              # 📋 (Planned)
│
├── service/                             # Business logic layer
│   ├── OrganisationService.java         # ✅ Organisation service
│   ├── ContactService.java              # 📋 (Planned)
│   └── LeadService.java                 # 📋 (Planned)
│
├── controller/                          # REST API controllers
│   ├── OrganisationController.java      # ✅ Organisation REST API
│   ├── ContactController.java           # 📋 (Planned)
│   └── LeadController.java              # 📋 (Planned)
│
├── dto/                                 # Data Transfer Objects (Future)
│   ├── OrganisationDTO.java
│   ├── ContactDTO.java
│   └── ResponseWrapper.java
│
├── exception/                           # Exception handling (Future)
│   ├── ResourceNotFoundException.java
│   ├── ValidationException.java
│   └── GlobalExceptionHandler.java
│
└── util/                                # Utility classes (Future)
    ├── ValidationUtil.java
    ├── DateUtil.java
    └── StringUtil.java
```

### 4.3 Request Flow

```
Client (Browser)
    │
    │ 1. HTTP Request (e.g., GET /api/organisations)
    ▼
OrganisationController (@RestController)
    │
    │ 2. Validate request, extract parameters
    ▼
OrganisationService (@Service)
    │
    │ 3. Apply business logic, transaction management
    ▼
OrganisationRepository (JpaRepository)
    │
    │ 4. Execute database query
    ▼
Database (H2/MySQL)
    │
    │ 5. Return results
    ▼
OrganisationController
    │
    │ 6. Format response, set HTTP status
    ▼
Client (Browser)
    │
    │ 7. Display data in UI
```

---

## 5. Database Design

### 5.1 Organisation Table (✅ Implemented)

```sql
CREATE TABLE organisation (
    -- Primary Key
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    
    -- Business Fields
    company_name VARCHAR(255) NOT NULL COMMENT 'Legal company name',
    trading_name VARCHAR(255) COMMENT 'Business/trading name',
    registration_number VARCHAR(100) COMMENT 'Company registration number',
    address TEXT COMMENT 'Company address',
    industry VARCHAR(100) COMMENT 'Industry sector',
    
    -- No-Delete Architecture
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE or INACTIVE',
    
    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes for performance
    INDEX idx_status (status),
    INDEX idx_company_name (company_name),
    INDEX idx_registration_number (registration_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Sample Data:**
```sql
INSERT INTO organisation (company_name, trading_name, registration_number, address, industry) 
VALUES 
('PayMedia Solutions Ltd', 'PayMedia', 'REG-2024-001', '123 Business St, Colombo', 'Technology'),
('Tech Innovators Pvt Ltd', 'TechInn', 'REG-2024-002', '456 Innovation Ave, Kandy', 'Software Development'),
('Global Finance Corp', 'GlobalFin', 'REG-2024-003', '789 Finance Rd, Galle', 'Finance');
```

### 5.2 Contact Table (📋 Planned - Module 4.2)

```sql
CREATE TABLE contact (
    -- Primary Key
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    
    -- Foreign Key
    organisation_id BIGINT NOT NULL COMMENT 'Link to organisation table',
    
    -- Personal Information
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL COMMENT 'Primary email address',
    phone VARCHAR(50) COMMENT 'Primary phone number',
    mobile VARCHAR(50) COMMENT 'Mobile phone number',
    
    -- Professional Information
    position VARCHAR(100) COMMENT 'Job title/position',
    department VARCHAR(100) COMMENT 'Department name',
    is_primary BOOLEAN DEFAULT FALSE COMMENT 'Primary contact for organisation',
    
    -- No-Delete Architecture
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    
    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign Key Constraint
    FOREIGN KEY (organisation_id) REFERENCES organisation(id),
    
    -- Indexes
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_organisation (organisation_id),
    INDEX idx_is_primary (is_primary),
    
    -- Unique Constraint
    UNIQUE KEY unique_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Relationship:**
- One Organisation → Many Contacts (1:N)
- Every Contact must belong to one Organisation
- Cascade behavior: Soft delete organisation → contacts remain (linked to INACTIVE org)

### 5.3 Lead Table (📋 Planned - Module 4.3)

```sql
CREATE TABLE lead (
    -- Primary Key
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    
    -- Foreign Keys
    organisation_id BIGINT COMMENT 'Optional: link to existing organisation',
    contact_id BIGINT COMMENT 'Optional: link to existing contact',
    
    -- Lead Information
    lead_name VARCHAR(255) NOT NULL COMMENT 'Lead/opportunity name',
    lead_source VARCHAR(100) COMMENT 'Website, Referral, Cold Call, etc.',
    lead_stage VARCHAR(50) NOT NULL COMMENT 'New, Qualified, Proposal, Negotiation',
    
    -- Financial Information
    estimated_value DECIMAL(15,2) COMMENT 'Estimated deal value',
    probability INT COMMENT 'Win probability (0-100)',
    expected_close_date DATE COMMENT 'Expected closing date',
    
    -- Additional Details
    description TEXT COMMENT 'Lead description',
    notes TEXT COMMENT 'Internal notes',
    
    -- No-Delete Architecture
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    
    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign Keys
    FOREIGN KEY (organisation_id) REFERENCES organisation(id),
    FOREIGN KEY (contact_id) REFERENCES contact(id),
    
    -- Indexes
    INDEX idx_stage (lead_stage),
    INDEX idx_status (status),
    INDEX idx_source (lead_source),
    INDEX idx_expected_close (expected_close_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 5.4 Database Indexing Strategy

**Why We Index:**
- Speed up SELECT queries
- Improve JOIN performance
- Optimize WHERE clause filtering
- Faster ORDER BY and GROUP BY

**Index Rules:**
1. ✅ Index ALL `status` columns (frequently filtered)
2. ✅ Index ALL foreign keys (JOIN performance)
3. ✅ Index search fields (name, email, phone)
4. ✅ Composite indexes for common query patterns
5. ❌ Don't over-index (impacts INSERT/UPDATE performance)

**Example Composite Index:**
```sql
-- For queries like: WHERE status='ACTIVE' AND industry='Technology'
CREATE INDEX idx_status_industry ON organisation(status, industry);
```

### 5.5 Entity Relationship Diagram (ERD)

```
┌─────────────────────┐
│   Organisation      │
│─────────────────────│
│ PK: id              │
│     company_name    │
│     trading_name    │
│     reg_number      │
│     address         │
│     industry        │
│     status          │
└─────────┬───────────┘
          │ 1
          │
          │ N
┌─────────▼───────────┐
│      Contact        │
│─────────────────────│
│ PK: id              │
│ FK: organisation_id │
│     first_name      │
│     last_name       │
│     email           │
│     phone           │
│     position        │
│     status          │
└─────────┬───────────┘
          │ 1
          │
          │ N
┌─────────▼───────────┐
│       Lead          │
│─────────────────────│
│ PK: id              │
│ FK: organisation_id │
│ FK: contact_id      │
│     lead_name       │
│     lead_stage      │
│     est_value       │
│     status          │
└─────────────────────┘
```

---

## 6. Backend Development Guide

### 6.1 Entity Development

**Organisation.java (✅ Implemented)**
```java
package com.paymedia.crm.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import java.time.LocalDateTime;

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
    private String status = "ACTIVE";
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;
}
```

**Contact.java (📋 Planned)**
```java
package com.paymedia.crm.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contact")
@Data
public class Contact {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Relationship to Organisation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;
    
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;
    
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(name = "phone", length = 50)
    private String phone;
    
    @Column(name = "position", length = 100)
    private String position;
    
    @Column(name = "department", length = 100)
    private String department;
    
    @Column(name = "is_primary")
    private Boolean isPrimary = false;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;
}
```

### 6.2 Repository Development

**OrganisationRepository.java (✅ Implemented)**
```java
package com.paymedia.crm.repository;

import com.paymedia.crm.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {
    
    // Find all by status (No-Delete Architecture)
    List<Organisation> findAllByStatus(String status);
    
    // Find by company name (case-insensitive)
    Optional<Organisation> findByCompanyNameIgnoreCase(String companyName);
    
    // Find by registration number
    Optional<Organisation> findByRegistrationNumber(String registrationNumber);
    
    // Custom query: Find active organisations by industry
    @Query("SELECT o FROM Organisation o WHERE o.status = 'ACTIVE' AND o.industry = :industry")
    List<Organisation> findActiveByIndustry(@Param("industry") String industry);
    
    // Search by name containing (for autocomplete)
    List<Organisation> findByCompanyNameContainingIgnoreCaseAndStatus(String name, String status);
}
```

**ContactRepository.java (📋 Planned)**
```java
package com.paymedia.crm.repository;

import com.paymedia.crm.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    // Find contacts by organisation
    List<Contact> findByOrganisationIdAndStatus(Long organisationId, String status);
    
    // Find primary contact for organisation
    Optional<Contact> findByOrganisationIdAndIsPrimaryTrueAndStatus(Long organisationId, String status);
    
    // Find by email
    Optional<Contact> findByEmailAndStatus(String email, String status);
}
```

### 6.3 Service Development

**OrganisationService.java (✅ Implemented)**
```java
package com.paymedia.crm.service;

import com.paymedia.crm.entity.Organisation;
import com.paymedia.crm.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrganisationService {
    
    @Autowired
    private OrganisationRepository repository;
    
    /**
     * Create new organisation
     */
    public Organisation createOrganisation(Organisation organisation) {
        if (organisation.getStatus() == null|| organisation.getStatus().isEmpty()) {
            organisation.setStatus("ACTIVE");
        }
        return repository.save(organisation);
    }
    
    /**
     * Get all active organisations
     */
    @Transactional(readOnly = true)
    public List<Organisation> getAllActiveOrganisations() {
        return repository.findAllByStatus("ACTIVE");
    }
    
    /**
     * Get organisation by ID
     */
    @Transactional(readOnly = true)
    public Optional<Organisation> getOrganisationById(Long id) {
        return repository.findById(id);
    }
    
    /**
     * Update organisation
     */
    public Optional<Organisation> updateOrganisation(Long id, Organisation updatedOrg) {
        return repository.findById(id).map(org -> {
            org.setCompanyName(updatedOrg.getCompanyName());
            org.setTradingName(updatedOrg.getTradingName());
            org.setRegistrationNumber(updatedOrg.getRegistrationNumber());
            org.setAddress(updatedOrg.getAddress());
            org.setIndustry(updatedOrg.getIndustry());
            return repository.save(org);
        });
    }
    
    /**
     * Soft delete organisation
     * IMPORTANT: Does NOT hard-delete from database
     */
    public boolean softDeleteOrganisation(Long id) {
        return repository.findById(id).map(org -> {
            org.setStatus("INACTIVE");
            repository.save(org);
            return true;
        }).orElse(false);
    }
    
    /**
     * Search organisations by name
     */
    @Transactional(readOnly = true)
    public List<Organisation> searchByName(String name) {
        return repository.findByCompanyNameContainingIgnoreCaseAndStatus(name, "ACTIVE");
    }
}
```

### 6.4 Controller Development

**OrganisationController.java (✅ Implemented)**
```java
package com.paymedia.crm.controller;

import com.paymedia.crm.entity.Organisation;
import com.paymedia.crm.service.OrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/organisations")
@CrossOrigin(origins = "http://localhost:3000")
public class OrganisationController {
    
    @Autowired
    private OrganisationService service;
    
    /**
     * Create new organisation
     * POST /api/organisations
     */
    @PostMapping
    public ResponseEntity<Organisation> createOrganisation(@RequestBody Organisation organisation) {
        try {
            Organisation saved = service.createOrganisation(organisation);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get all active organisations
     * GET /api/organisations
     */
    @GetMapping
    public ResponseEntity<List<Organisation>> getAllActiveOrganisations() {
        try {
            List<Organisation> organisations = service.getAllActiveOrganisations();
            
            if (organisations.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            return new ResponseEntity<>(organisations, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get organisation by ID
     * GET /api/organisations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Organisation> getOrganisationById(@PathVariable("id") Long id) {
        return service.getOrganisationById(id)
                .map(org -> new ResponseEntity<>(org, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Update organisation
     * PUT /api/organisations/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Organisation> updateOrganisation(
            @PathVariable("id") Long id,
            @RequestBody Organisation organisation) {
        
        return service.updateOrganisation(id, organisation)
                .map(updated -> new ResponseEntity<>(updated, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Soft delete organisation
     * DELETE /api/organisations/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> softDeleteOrganisation(@PathVariable("id") Long id) {
        try {
            boolean deleted = service.softDeleteOrganisation(id);
            
            if (deleted) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Search organisations by name
     * GET /api/organisations/search?name={name}
     */
    @GetMapping("/search")
    public ResponseEntity<List<Organisation>> searchOrganisations(@RequestParam String name) {
        List<Organisation> results = service.searchByName(name);
        return results.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(results, HttpStatus.OK);
    }
}
```

---

##7. Frontend Development Guide

### 7.1 Component Structure

```
frontend/src/
├── components/
│   ├── OrganisationManager.js     ✅ Organisation CRUD component
│   ├── ContactManager.js          📋 Contact CRUD (Planned)
│   ├── LeadManager.js             📋 Lead CRUD (Planned)
│   └── common/
│       ├── Header.js              📋 App header
│       ├── Navbar.js              📋 Navigation
│       ├── Footer.js              📋 App footer
│       ├── Loader.js              📋 Loading spinner
│       └── ErrorAlert.js          📋 Error display
├── services/
│   ├── organisationService.js     📋 Organisation API calls
│   ├── contactService.js          📋 Contact API calls
│   └── apiClient.js               📋 Axios configuration
├── utils/
│   ├── validation.js              📋 Form validation
│   ├── formatters.js              📋 Data formatting
│   └── constants.js               📋 App constants
├── App.js                         ✅ Main app component
├── App.css                        ✅ App styles
├── index.js                       ✅ React entry point
└── index.css                      ✅ Global styles
```

### 7.2 OrganisationManager Component (✅ Implemented)

**Full Component Code:**
```javascript
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';

const OrganisationManager = () => {
    const API_URL = 'http://localhost:8080/api/organisations';
    
    // State Management
    const [organisations, setOrganisations] = useState([]);
    const [formData, setFormData] = useState({
        companyName: '',
        tradingName: '',
        registrationNumber: '',
        address: '',
        industry: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null);
    
    // Load data on mount
    useEffect(() => {
        fetchOrganisations();
    }, []);
    
    // Fetch all organisations
    const fetchOrganisations = async () => {
        setLoading(true);
        setError(null);
        
        try {
            const response = await axios.get(API_URL);
            setOrganisations(response.data);
        } catch (err) {
            if (err.response && err.response.status === 204) {
                setOrganisations([]);
            } else {
                setError('Failed to load organisations');
                console.error('Error:', err);
            }
        } finally {
            setLoading(false);
        }
    };
    
    // Handle form input changes
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };
    
    // Handle form submission
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setSuccessMessage(null);
        
        // Validation
        if (!formData.companyName || !formData.industry) {
            setError('Company Name and Industry are required');
            return;
        }
        
        try {
            await axios.post(API_URL, formData);
            
            // Reset form
            setFormData({
                companyName: '',
                tradingName: '',
                registrationNumber: '',
                address: '',
                industry: ''
            });
            
            setSuccessMessage('Organisation created successfully!');
            fetchOrganisations();
            
            setTimeout(() => setSuccessMessage(null), 3000);
        } catch (err) {
            setError('Failed to create organisation');
            console.error('Error:', err);
        }
    };
    
    // Handle delete
    const handleDelete = async (id) => {
        if (!window.confirm('Are you sure you want to delete this organisation?')) {
            return;
        }
        
        setError(null);
        setSuccessMessage(null);
        
        try {
            await axios.delete(`${API_URL}/${id}`);
            setSuccessMessage('Organisation deleted successfully!');
            fetchOrganisations();
            
            setTimeout(() => setSuccessMessage(null), 3000);
        } catch (err) {
            setError('Failed to delete organisation');
            console.error('Error:', err);
        }
    };
    
    // Render
    return (
        <div className="container mt-5">
            <h1 className="mb-4">PayMedia CRM - Organisation Management</h1>
            
            {/* Alerts */}
            {error && (
                <div className="alert alert-danger alert-dismissible fade show">
                    {error}
                    <button type="button" className="btn-close" onClick={() => setError(null)}></button>
                </div>
            )}
            
            {successMessage && (
                <div className="alert alert-success alert-dismissible fade show">
                    {successMessage}
                    <button type="button" className="btn-close" onClick={() => setSuccessMessage(null)}></button>
                </div>
            )}
            
            {/* Add Form */}
            <div className="card mb-4">
                <div className="card-header bg-primary text-white">
                    <h5>Add New Organisation</h5>
                </div>
                <div className="card-body">
                    <form onSubmit={handleSubmit}>
                        <div className="row">
                            <div className="col-md-6 mb-3">
                                <label className="form-label">Company Name *</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    name="companyName"
                                    value={formData.companyName}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>
                            <div className="col-md-6 mb-3">
                                <label className="form-label">Trading Name</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    name="tradingName"
                                    value={formData.tradingName}
                                    onChange={handleInputChange}
                                />
                            </div>
                        </div>
                        <div className="row">
                            <div className="col-md-6 mb-3">
                                <label className="form-label">Registration Number</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    name="registrationNumber"
                                    value={formData.registrationNumber}
                                    onChange={handleInputChange}
                                />
                            </div>
                            <div className="col-md-6 mb-3">
                                <label className="form-label">Industry *</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    name="industry"
                                    value={formData.industry}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>
                        </div>
                        <div className="mb-3">
                            <label className="form-label">Address</label>
                            <textarea
                                className="form-control"
                                name="address"
                                value={formData.address}
                                onChange={handleInputChange}
                                rows="2"
                            ></textarea>
                        </div>
                        <button type="submit" className="btn btn-primary">
                            Add Organisation
                        </button>
                    </form>
                </div>
            </div>
            
            {/* Data Table */}
            <div className="card">
                <div className="card-header bg-secondary text-white">
                    <h5>Active Organisations</h5>
                </div>
                <div className="card-body">
                    {loading ? (
                        <div className="text-center">
                            <div className="spinner-border text-primary"></div>
                        </div>
                    ) : organisations.length === 0 ? (
                        <div className="alert alert-info">
                            No organisations found.
                        </div>
                    ) : (
                        <div className="table-responsive">
                            <table className="table table-striped table-hover">
                                <thead className="table-dark">
                                    <tr>
                                        <th>ID</th>
                                        <th>Company Name</th>
                                        <th>Trading Name</th>
                                        <th>Registration Number</th>
                                        <th>Industry</th>
                                        <th>Status</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {organisations.map(org => (
                                        <tr key={org.id}>
                                            <td>{org.id}</td>
                                            <td>{org.companyName}</td>
                                            <td>{org.tradingName || '-'}</td>
                                            <td>{org.registrationNumber || '-'}</td>
                                            <td>
                                                <span className="badge bg-info">
                                                    {org.industry}
                                                </span>
                                            </td>
                                            <td>
                                                <span className={`badge ${org.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'}`}>
                                                    {org.status}
                                                </span>
                                            </td>
                                            <td>
                                                <button
                                                    className="btn btn-danger btn-sm"
                                                    onClick={() => handleDelete(org.id)}
                                                >
                                                    Delete
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default OrganisationManager;
```

---

## 8. API Documentation

### 8.1 Organisation API Endpoints

| Method | Endpoint | Description | Request Body | Response Code | Response Body |
|--------|----------|-------------|--------------|---------------|---------------|
| **GET** | `/api/organisations` | Get all active organisations | None | 200 OK | Array of organisations |
| **GET** | `/api/organisations/{id}` | Get organisation by ID | None | 200 OK / 404 Not Found | Organisation object |
| **POST** | `/api/organisations` | Create new organisation | Organisation JSON | 201 Created | Created organisation |
| **PUT** | `/api/organisations/{id}` | Update organisation | Organisation JSON | 200 OK / 404 Not Found | Updated organisation |
| **DELETE** | `/api/organisations/{id}` | Soft delete organisation | None | 204 No Content / 404 Not Found | None |
| **GET** | `/api/organisations/search?name={name}` | Search by name | None | 200 OK / 204 No Content | Array of organisations |

### 8.2 Request/Response Examples

**Example 1: Create Organisation**

Request:
```http
POST /api/organisations HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "companyName": "PayMedia Solutions Ltd",
  "tradingName": "PayMedia",
  "registrationNumber": "REG-2024-001",
  "address": "123 Business Street, Colombo",
  "industry": "Technology"
}
```

Response:
```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": 1,
  "companyName": "PayMedia Solutions Ltd",
  "tradingName": "PayMedia",
  "registrationNumber": "REG-2024-001",
  "address": "123 Business Street, Colombo",
  "industry": "Technology",
  "status": "ACTIVE",
  "createdAt": "2026-03-03T18:43:46",
  "modifiedAt": "2026-03-03T18:43:46"
}
```

**Example 2: Get All Organisations**

Request:
```http
GET /api/organisations HTTP/1.1
Host: localhost:8080
```

Response:
```http
HTTP/1.1 200 OK
Content-Type: application/json

[
  {
    "id": 1,
    "companyName": "PayMedia Solutions Ltd",
    "tradingName": "PayMedia",
    "registrationNumber": "REG-2024-001",
    "address": "123 Business Street, Colombo",
    "industry": "Technology",
    "status": "ACTIVE",
    "createdAt": "2026-03-03T18:43:46",
    "modifiedAt": "2026-03-03T18:43:46"
  },
  {
    "id": 2,
    "companyName": "Tech Innovators Pvt Ltd",
    "tradingName": "TechInn",
    "registrationNumber": "REG-2024-002",
    "address": "456 Innovation Avenue, Kandy",
    "industry": "Software Development",
    "status": "ACTIVE",
    "createdAt": "2026-03-03T18:44:00",
    "modifiedAt": "2026-03-03T18:44:00"
  }
]
```

**Example 3: Soft Delete Organisation**

Request:
```http
DELETE /api/organisations/1 HTTP/1.1
Host: localhost:8080
```

Response:
```http
HTTP/1.1 204 No Content
```

Note: Record is NOT deleted from database. Status is set to 'INACTIVE'.

---

## 9. Security & Authentication

### 9.1 CORS Configuration (✅ Implemented)

```java
@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Allow React frontend
public class OrganisationController {
    // ...
}
```

### 9.2 Future: JWT Authentication (📋 Planned)

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors()
            .and()
            .authorizeHttpRequests()
                .requestMatchers("/api/auth/**").permitAll()  // Login/Register
                .requestMatchers("/api/**").authenticated()    // All other APIs
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Stateless JWT
            .and()
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

### 9.3 Role-Based Access Control (📋 Planned)

```java
public enum UserRole {
    ADMIN,       // Full access
    MANAGER,     // View & edit all records
    SALES_REP,   // View & edit own records
    VIEWER       // Read-only access
}

// Controller with role-based access
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/{id}")
public ResponseEntity<HttpStatus> softDelete(@PathVariable Long id) {
    // Only ADMIN can delete
}
```

---

## 10. Testing Strategy

### 10.1 Unit Testing

```java
@SpringBootTest
class OrganisationServiceTest {
    
    @Autowired
    private OrganisationService service;
    
    @MockBean
    private OrganisationRepository repository;
    
    @Test
    @DisplayName("Should create organisation with ACTIVE status")
    void testCreateOrganisation() {
        // Arrange
        Organisation org = new Organisation();
        org.setCompanyName("Test Company");
        
        // Act
        Organisation saved = service.createOrganisation(org);
        
        // Assert
        assertNotNull(saved);
        assertEquals("ACTIVE", saved.getStatus());
        assertNotNull(saved.getCreatedAt());
    }
    
    @Test
    @DisplayName("Should soft delete organisation")
    void testSoftDelete() {
        // Arrange
        Organisation org = new Organisation();
        org.setId(1L);
        org.setStatus("ACTIVE");
        when(repository.findById(1L)).thenReturn(Optional.of(org));
        
        // Act
        boolean deleted = service.softDeleteOrganisation(1L);
        
        // Assert
        assertTrue(deleted);
        assertEquals("INACTIVE", org.getStatus());
        verify(repository, times(1)).save(org);
    }
}
```

### 10.2 Integration Testing

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
class OrganisationControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testCreateAndRetrieveOrganisation() {
        // Create
        Organisation org = new Organisation();
        org.setCompanyName("Integration Test Company");
        org.setIndustry("Testing");
        
        ResponseEntity<Organisation> createResponse = 
            restTemplate.postForEntity("/api/organisations", org, Organisation.class);
        
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody().getId());
        
        // Retrieve
        Long id = createResponse.getBody().getId();
        ResponseEntity<Organisation> getResponse = 
            restTemplate.getForEntity("/api/organisations/" + id, Organisation.class);
        
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("Integration Test Company", getResponse.getBody().getCompanyName());
    }
}
```

---

## 11. Deployment Guide

### 11.1 Development Configuration

**application-dev.properties:**
```properties
# H2 Database (In-Memory)
spring.datasource.url=jdbc:h2:mem:paymedia_crm
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Logging
logging.level.org.springframework=DEBUG
logging.level.com.paymedia.crm=DEBUG
```

### 11.2 Production Configuration

**application-prod.properties:**
```properties
# MySQL Database
spring.datasource.url=jdbc:mysql://localhost:3306/paymedia_crm?useSSL=true
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate  # Don't auto-update schema
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Logging
logging.level.org.springframework=INFO
logging.level.com.paymedia.crm=INFO
logging.file.name=/var/log/paymedia-crm/application.log
```

### 11.3 Docker Deployment (📋 Planned)

**Dockerfile:**
```dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/crm-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
```

**docker-compose.yml:**
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_ROOT_PASSWORD}
      MYSQL_DATABASE: paymedia_crm
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
  
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      DB_USERNAME: root
      DB_PASSWORD: ${DB_ROOT_PASSWORD}
    depends_on:
      - mysql
  
  frontend:
    build: ./frontend
    ports:
      - "3000:80"
    depends_on:
      - backend

volumes:
  mysql-data:
```

---

## 12. Phase-wise Implementation

### Phase 1: Foundation ✅ COMPLETED
- [x] Project setup and configuration
- [x] H2 in-memory database integration
- [x] Organisation entity and repository
- [x] Organisation service and controller
- [x] React frontend with Bootstrap
- [x] No-Delete Architecture implementation
- [x] Audit fields (created_at, modified_at)
- [x] Sample data

### Phase 2: Contact Management 📋 NEXT
- [ ] Contact entity with Organisation relationship
- [ ] Contact repository and service
- [ ] Contact REST API endpoints
- [ ] Contact management UI component
- [ ] Primary contact designation
- [ ] Email uniqueness validation

### Phase 3: Lead Management 📋 PLANNED
- [ ] Lead entity with relationships
- [ ] Lead repository and service
- [ ] Lead REST API endpoints
- [ ] Lead tracking UI component
- [ ] Lead stage management
- [ ] Financial tracking (value, probability)

### Phase 4: Advanced Features 📋 PLANNED
- [ ] Search and filtering across modules
- [ ] Pagination and sorting
- [ ] Data export (CSV, Excel, PDF)
- [ ] Advanced dashboard with KPIs
- [ ] Activity timeline
- [ ] Email integration

### Phase 5: Security & Authentication 📋 PLANNED
- [ ] JWT authentication
- [ ] User registration and login
- [ ] Role-based access control (RBAC)
- [ ] Password encryption
- [ ] Session management
- [ ] API rate limiting

### Phase 6: Production Ready 📋 PLANNED
- [ ] Comprehensive unit tests (80%+ coverage)
- [ ] Integration tests
- [ ] Performance optimization
- [ ] Security hardening
- [ ] CI/CD pipeline
- [ ] Docker containerization
- [ ] Production deployment

---

## 13. Best Practices & Standards

### 13.1 Code Standards

**Java Naming Conventions:**
- Classes: `PascalCase` (e.g., `OrganisationService`)
- Methods: `camelCase` (e.g., `createOrganisation`)
- Variables: `camelCase` (e.g., `companyName`)
- Constants: `UPPER_SNAKE_CASE` (e.g., `MAX_RETRIES`)
- Packages: `lowercase` (e.g., `com.paymedia.crm.service`)

**JavaScript Naming Conventions:**
- Components: `PascalCase` (e.g., `OrganisationManager`)
- Functions: `camelCase` (e.g., `fetchOrganisations`)
- Variables: `camelCase` (e.g., `formData`)
- Constants: `UPPER_SNAKE_CASE` (e.g., `API_URL`)

### 13.2 Git Workflow

```bash
# Feature development
git checkout -b feature/contact-management
git add .
git commit -m "feat: Add contact CRUD operations"
git push origin feature/contact-management

# Bug fixes
git checkout -b fix/organisation-validation
git commit -m "fix: Validate company name uniqueness"
git push origin fix/organisation-validation

# Documentation
git commit -m "docs: Update API documentation"
```

**Commit Message Format:**
```
<type>: <description>

Types:
- feat: New feature
- fix: Bug fix
- docs: Documentation only
- style: Code formatting (no logic change)
- refactor: Code refactoring
- test: Adding/updating tests
- chore: Build process, dependencies
```

### 13.3 Code Review Checklist

- [ ] Code follows naming conventions
- [ ] No-Delete Architecture implemented correctly
- [ ] Audit fields present (created_at, modified_at)
- [ ] Service layer has `@Transactional`
- [ ] Controller has proper exception handling
- [ ] Unit tests written and passing
- [ ] No hardcoded values (use constants/config)
- [ ] Lombok used appropriately
- [ ] Comments for complex logic
- [ ] No console.log() in production code

---

## 14. Quick Reference

### 14.1 Backend Commands

```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run

# Run specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests
mvn test

# Package JAR
mvn package

# Skip tests during build
mvn clean install -DskipTests
```

### 14.2 Frontend Commands

```bash
# Install dependencies
npm install

# Start development server
npm start

# Build for production
npm run build

# Run tests
npm test

# Check for vulnerabilities
npm audit

# Update dependencies
npm update
```

### 14.3 Database Access

**H2 Console:**
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:paymedia_crm
Username: sa
Password: (leave empty)
```

**MySQL Commands:**
```sql
-- Create database
CREATE DATABASE paymedia_crm;

-- Use database
USE paymedia_crm;

-- Load schema
SOURCE organisation_schema.sql;

-- View tables
SHOW TABLES;

-- View data
SELECT * FROM organisation WHERE status = 'ACTIVE';
```

### 14.4 API Testing with cURL

```bash
# Get all organisations
curl -X GET http://localhost:8080/api/organisations

# Get by ID
curl -X GET http://localhost:8080/api/organisations/1

# Create organisation
curl -X POST http://localhost:8080/api/organisations \
  -H "Content-Type: application/json" \
  -d '{
    "companyName": "Test Company",
    "industry": "Technology"
  }'

# Update organisation
curl -X PUT http://localhost:8080/api/organisations/1 \
  -H "Content-Type: application/json" \
  -d '{
    "companyName": "Updated Company",
    "industry": "Technology"
  }'

# Soft delete
curl -X DELETE http://localhost:8080/api/organisations/1
```

---

## 15. Troubleshooting

### 15.1 Common Backend Issues

**Problem:** `mvn` command not recognized
```bash
Solution:
1. Install Maven from https://maven.apache.org/download.cgi
2. Add Maven bin folder to PATH environment variable
3. Restart terminal
4. Verify: mvn -version
```

**Problem:** Java version mismatch
```bash
Solution:
1. Check Java version: java -version
2. Should be Java 21
3. Update pom.xml <java.version> if needed
4. Install correct Java version from https://adoptium.net/
```

**Problem:** Port 8080 already in use
```bash
Solution:
1. Find process using port: netstat -ano | findstr :8080
2. Kill process: taskkill /PID [PID_NUMBER] /F
3. Or change port in application.properties: server.port=8081
```

**Problem:** Database connection fails
```bash
Solution for H2:
- Check spring.datasource.url in application.properties
- Ensure H2 dependency is in pom.xml

Solution for MySQL:
- Verify MySQL is running
- Check username/password
- Ensure database exists: CREATE DATABASE paymedia_crm;
```

### 15.2 Common Frontend Issues

**Problem:** `npm` command not recognized
```bash
Solution:
1. Install Node.js from https://nodejs.org/
2. Restart terminal
3. Verify: node -version && npm -version
```

**Problem:** CORS error in browser
```bash
Solution:
1. Ensure @CrossOrigin is on controller
2. Check origin matches (http://localhost:3000)
3. Clear browser cache
4. Disable browser extensions
```

**Problem:** API calls return 404
```bash
Solution:
1. Verify backend is running on port 8080
2. Check API URL in component: http://localhost:8080/api/organisations
3. Test API directly in browser or Postman
4. Check network tab in browser DevTools
```

**Problem:** Module not found errors
```bash
Solution:
1. Delete node_modules folder
2. Delete package-lock.json
3. Run: npm install
4. Restart dev server: npm start
```

---

## 16. Resources & Links

### Official Documentation
- Spring Boot: https://spring.io/projects/spring-boot
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- Hibernate: https://hibernate.org/orm/documentation/
- React: https://react.dev/
- Bootstrap: https://getbootstrap.com/docs/
- Lombok: https://projectlombok.org/
- MySQL: https://dev.mysql.com/doc/

### Tutorials & Guides
- Spring Boot REST API: https://spring.io/guides/gs/rest-service/
- React Hooks: https://react.dev/reference/react
- Axios: https://axios-http.com/docs/intro
- JPA Relationships: https://www.baeldung.com/jpa-one-to-many

### Tools
- Maven Repository: https://mvnrepository.com/
- npm Registry: https://www.npmjs.com/
- JSON Formatter: https://jsonformatter.org/
- Regex Tester: https://regex101.com/

---

## 17. Next Steps

### Immediate Tasks
1. ✅ Review this documentation
2. ✅ Ensure backend is running successfully
3. ✅ Ensure frontend is running successfully
4. ✅ Test CRUD operations
5. 📋 Plan Contact Management module

### Short-term Goals
1. Implement Contact Management (Phase 2)
2. Add search and filtering
3. Implement pagination
4. Add data export features

### Long-term Goals
1. Complete all CRM modules
2. Implement authentication & authorization
3. Build comprehensive dashboard
4. Deploy to production
5. Mobile app development

---

## 📞 Support & Contact

**Project Information:**
- Repository: [Add Git Repository URL]
- Documentation: See README.md, QUICKSTART.md
- Issue Tracker: [Add Issue Tracker URL]

**Technical Assistance:**
- For installation issues: See INSTALLATION_GUIDE.txt
- For quick start: See QUICKSTART_NO_MYSQL.md
- For troubleshooting: See section 15 above

---

**Document Version:** 2.0  
**Last Updated:** March 3, 2026  
**Status:** Active Development  
**Current Phase:** Phase 1 Complete, Phase 2 Planning  

---

*Built with ❤️ using Java 21, Spring Boot 3.2, React 18 & Bootstrap 5*

**"Excellence in Every Line of Code"** 🚀
