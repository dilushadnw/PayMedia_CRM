package com.paymedia.crm.service;

import com.paymedia.crm.entity.Contact;
import com.paymedia.crm.entity.Lead;
import com.paymedia.crm.entity.Organisation;
import com.paymedia.crm.repository.ContactRepository;
import com.paymedia.crm.repository.LeadRepository;
import com.paymedia.crm.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Lead Service - Business Logic Layer
 * Phase 3: Lead Management Module
 * 
 * Implements business rules:
 * - No-Delete Architecture (soft delete only)
 * - Lead stage progression validation
 * - Organisation and Contact relationship management
 * - Revenue calculations and forecasting
 * 
 * @Transactional ensures data consistency
 */
@Service
@Transactional
public class LeadService {
    
    @Autowired
    private LeadRepository leadRepository;
    
    @Autowired
    private OrganisationRepository organisationRepository;
    
    @Autowired
    private ContactRepository contactRepository;
    
    /**
     * Create new lead
     * Validates organisation and contact if provided
     */
    public Lead createLead(Lead lead) {
        // Validate and set Organisation if provided
        if (lead.getOrganisation() != null && lead.getOrganisation().getId() != null) {
            Optional<Organisation> org = organisationRepository.findById(lead.getOrganisation().getId());
            if (org.isEmpty()) {
                throw new IllegalArgumentException("Organisation not found with ID: " + lead.getOrganisation().getId());
            }
            lead.setOrganisation(org.get());
        }
        
        // Validate and set Contact if provided
        if (lead.getContact() != null && lead.getContact().getId() != null) {
            Optional<Contact> contact = contactRepository.findById(lead.getContact().getId());
            if (contact.isEmpty()) {
                throw new IllegalArgumentException("Contact not found with ID: " + lead.getContact().getId());
            }
            lead.setContact(contact.get());
        }
        
        // Set default values
        if (lead.getStatus() == null || lead.getStatus().isEmpty()) {
            lead.setStatus("ACTIVE");
        }
        
        if (lead.getLeadStage() == null || lead.getLeadStage().isEmpty()) {
            lead.setLeadStage("New");
        }
        
        // Validate probability range
        if (lead.getProbability() != null && (lead.getProbability() < 0 || lead.getProbability() > 100)) {
            throw new IllegalArgumentException("Probability must be between 0 and 100");
        }
        
        return leadRepository.save(lead);
    }
    
    /**
     * Get all active leads
     */
    @Transactional(readOnly = true)
    public List<Lead> getAllActiveLeads() {
        return leadRepository.findAllByStatus("ACTIVE");
    }
    
    /**
     * Get lead by ID
     */
    @Transactional(readOnly = true)
    public Optional<Lead> getLeadById(Long id) {
        return leadRepository.findById(id);
    }
    
    /**
     * Get leads by stage
     */
    @Transactional(readOnly = true)
    public List<Lead> getLeadsByStage(String stage) {
        return leadRepository.findByLeadStageAndStatus(stage, "ACTIVE");
    }
    
    /**
     * Get leads by source
     */
    @Transactional(readOnly = true)
    public List<Lead> getLeadsBySource(String source) {
        return leadRepository.findByLeadSourceAndStatus(source, "ACTIVE");
    }
    
    /**
     * Get leads by organisation
     */
    @Transactional(readOnly = true)
    public List<Lead> getLeadsByOrganisation(Long organisationId) {
        return leadRepository.findByOrganisationIdAndStatus(organisationId, "ACTIVE");
    }
    
    /**
     * Get leads by contact
     */
    @Transactional(readOnly = true)
    public List<Lead> getLeadsByContact(Long contactId) {
        return leadRepository.findByContactIdAndStatus(contactId, "ACTIVE");
    }
    
    /**
     * Get open leads (not closed)
     */
    @Transactional(readOnly = true)
    public List<Lead> getOpenLeads() {
        return leadRepository.findOpenLeads("ACTIVE");
    }
    
    /**
     * Get won leads
     */
    @Transactional(readOnly = true)
    public List<Lead> getWonLeads() {
        return leadRepository.findWonLeads("ACTIVE");
    }
    
    /**
     * Get lost leads
     */
    @Transactional(readOnly = true)
    public List<Lead> getLostLeads() {
        return leadRepository.findLostLeads("ACTIVE");
    }
    
    /**
     * Update existing lead
     */
    public Optional<Lead> updateLead(Long id, Lead updatedLead) {
        return leadRepository.findById(id).map(lead -> {
            // Update basic fields
            lead.setLeadName(updatedLead.getLeadName());
            lead.setLeadSource(updatedLead.getLeadSource());
            lead.setLeadStage(updatedLead.getLeadStage());
            lead.setEstimatedValue(updatedLead.getEstimatedValue());
            lead.setProbability(updatedLead.getProbability());
            lead.setExpectedCloseDate(updatedLead.getExpectedCloseDate());
            lead.setDescription(updatedLead.getDescription());
            lead.setNotes(updatedLead.getNotes());
            
            // Update company info
            lead.setCompanyName(updatedLead.getCompanyName());
            lead.setIndustry(updatedLead.getIndustry());
            
            // Update contact info
            lead.setContactName(updatedLead.getContactName());
            lead.setContactEmail(updatedLead.getContactEmail());
            lead.setContactPhone(updatedLead.getContactPhone());
            
            // Update relationships if changed
            if (updatedLead.getOrganisation() != null && updatedLead.getOrganisation().getId() != null) {
                Optional<Organisation> org = organisationRepository.findById(updatedLead.getOrganisation().getId());
                org.ifPresent(lead::setOrganisation);
            }
            
            if (updatedLead.getContact() != null && updatedLead.getContact().getId() != null) {
                Optional<Contact> contact = contactRepository.findById(updatedLead.getContact().getId());
                contact.ifPresent(lead::setContact);
            }
            
            // Set actual close date if lead is being closed
            if (lead.isClosed() && lead.getActualCloseDate() == null) {
                lead.setActualCloseDate(LocalDate.now());
            }
            
            return leadRepository.save(lead);
        });
    }
    
    /**
     * Soft delete lead
     * IMPORTANT: Does NOT hard-delete from database
     */
    public boolean softDeleteLead(Long id) {
        return leadRepository.findById(id).map(lead -> {
            lead.setStatus("INACTIVE");
            leadRepository.save(lead);
            return true;
        }).orElse(false);
    }
    
    /**
     * Search leads
     */
    @Transactional(readOnly = true)
    public List<Lead> searchLeads(String searchTerm) {
        return leadRepository.searchLeads(searchTerm, "ACTIVE");
    }
    
    /**
     * Change lead stage
     * Validates stage transitions
     */
    public boolean changeLeadStage(Long id, String newStage) {
        return leadRepository.findById(id).map(lead -> {
            String oldStage = lead.getLeadStage();
            
            // Prevent changing closed leads
            if (lead.isClosed()) {
                throw new IllegalArgumentException("Cannot change stage of closed lead");
            }
            
            lead.setLeadStage(newStage);
            
            // Set actual close date if closing
            if ("Closed Won".equalsIgnoreCase(newStage) || "Closed Lost".equalsIgnoreCase(newStage)) {
                lead.setActualCloseDate(LocalDate.now());
            }
            
            leadRepository.save(lead);
            return true;
        }).orElse(false);
    }
    
    /**
     * Get leads closing within date range
     */
    @Transactional(readOnly = true)
    public List<Lead> getLeadsClosingBetween(LocalDate startDate, LocalDate endDate) {
        return leadRepository.findLeadsClosingBetween(startDate, endDate, "ACTIVE");
    }
    
    /**
     * Get overdue leads
     */
    @Transactional(readOnly = true)
    public List<Lead> getOverdueLeads() {
        return leadRepository.findOverdueLeads(LocalDate.now());
    }
    
    /**
     * Get leads closing this month
     */
    @Transactional(readOnly = true)
    public List<Lead> getLeadsClosingThisMonth() {
        LocalDate now = LocalDate.now();
        return leadRepository.findLeadsClosingInMonth(now.getYear(), now.getMonthValue());
    }
    
    /**
     * Get dashboard statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        long totalLeads = leadRepository.countByStatus("ACTIVE");
        long wonLeads = leadRepository.countWonLeads();
        long lostLeads = leadRepository.countLostLeads();
        long openLeads = totalLeads - wonLeads - lostLeads;
        
        BigDecimal totalEstimatedValue = leadRepository.calculateTotalEstimatedValue();
        BigDecimal totalWonRevenue = leadRepository.calculateTotalWonRevenue();
        BigDecimal expectedRevenue = leadRepository.calculateExpectedRevenue();
        
        Double winRate = leadRepository.calculateWinRate();
        
        return Map.of(
            "totalLeads", totalLeads,
            "openLeads", openLeads,
            "wonLeads", wonLeads,
            "lostLeads", lostLeads,
            "totalEstimatedValue", totalEstimatedValue,
            "totalWonRevenue", totalWonRevenue,
            "expectedRevenue", expectedRevenue,
            "winRate", winRate != null ? winRate : 0.0
        );
    }
    
    /**
     * Get leads by stage count
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getLeadCountByStage() {
        return Map.of(
            "New", leadRepository.countByLeadStageAndStatus("New", "ACTIVE"),
            "Qualified", leadRepository.countByLeadStageAndStatus("Qualified", "ACTIVE"),
            "Proposal", leadRepository.countByLeadStageAndStatus("Proposal", "ACTIVE"),
            "Negotiation", leadRepository.countByLeadStageAndStatus("Negotiation", "ACTIVE"),
            "Closed Won", leadRepository.countByLeadStageAndStatus("Closed Won", "ACTIVE"),
            "Closed Lost", leadRepository.countByLeadStageAndStatus("Closed Lost", "ACTIVE")
        );
    }
    
    /**
     * Convert lead to organisation and contact
     * Creates new organisation and contact from lead data
     */
    public Map<String, Long> convertLead(Long leadId) {
        return leadRepository.findById(leadId).map(lead -> {
            // Create organisation if company info provided
            Long orgId = null;
            if (lead.getCompanyName() != null && !lead.getCompanyName().isEmpty()) {
                Organisation org = new Organisation();
                org.setCompanyName(lead.getCompanyName());
                org.setIndustry(lead.getIndustry());
                org.setStatus("ACTIVE");
                org = organisationRepository.save(org);
                orgId = org.getId();
                lead.setOrganisation(org);
            }
            
            // Create contact if contact info provided
            Long contactId = null;
            if (lead.getContactName() != null && !lead.getContactName().isEmpty() && orgId != null) {
                Contact contact = new Contact();
                String[] nameParts = lead.getContactName().split(" ", 2);
                contact.setFirstName(nameParts[0]);
                contact.setLastName(nameParts.length > 1 ? nameParts[1] : "");
                contact.setEmail(lead.getContactEmail());
                contact.setPhone(lead.getContactPhone());
                contact.setOrganisation(lead.getOrganisation());
                contact.setStatus("ACTIVE");
                contact = contactRepository.save(contact);
                contactId = contact.getId();
                lead.setContact(contact);
            }
            
            leadRepository.save(lead);
            
            return Map.of(
                "organisationId", orgId != null ? orgId : 0L,
                "contactId", contactId != null ? contactId : 0L
            );
        }).orElse(Map.of("organisationId", 0L, "contactId", 0L));
    }
}
