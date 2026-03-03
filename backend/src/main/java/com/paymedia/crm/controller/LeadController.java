package com.paymedia.crm.controller;

import com.paymedia.crm.entity.Lead;
import com.paymedia.crm.service.LeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Lead REST Controller - API Layer
 * Phase 3: Lead Management Module
 * 
 * Provides RESTful endpoints for Lead CRUD operations
 * Base URL: /api/leads
 * 
 * CORS enabled for React frontend
 */
@RestController
@RequestMapping("/api/leads")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class LeadController {
    
    @Autowired
    private LeadService leadService;
    
    /**
     * Create new lead
     * POST /api/leads
     */
    @PostMapping
    public ResponseEntity<?> createLead(@RequestBody Lead lead) {
        try {
            Lead saved = leadService.createLead(lead);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create lead: " + e.getMessage()));
        }
    }
    
    /**
     * Get all active leads
     * GET /api/leads
     */
    @GetMapping
    public ResponseEntity<List<Lead>> getAllActiveLeads() {
        try {
            List<Lead> leads = leadService.getAllActiveLeads();
            
            if (leads.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            return new ResponseEntity<>(leads, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get lead by ID
     * GET /api/leads/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Lead> getLeadById(@PathVariable("id") Long id) {
        return leadService.getLeadById(id)
                .map(lead -> new ResponseEntity<>(lead, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Update lead
     * PUT /api/leads/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLead(@PathVariable("id") Long id, @RequestBody Lead lead) {
        try {
            return leadService.updateLead(id, lead)
                    .map(updated -> new ResponseEntity<>(updated, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update lead: " + e.getMessage()));
        }
    }
    
    /**
     * Soft delete lead
     * DELETE /api/leads/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> softDeleteLead(@PathVariable("id") Long id) {
        try {
            boolean deleted = leadService.softDeleteLead(id);
            
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
     * Get leads by stage
     * GET /api/leads/stage/{stage}
     */
    @GetMapping("/stage/{stage}")
    public ResponseEntity<List<Lead>> getLeadsByStage(@PathVariable("stage") String stage) {
        try {
            List<Lead> leads = leadService.getLeadsByStage(stage);
            
            if (leads.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            return new ResponseEntity<>(leads, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get leads by source
     * GET /api/leads/source/{source}
     */
    @GetMapping("/source/{source}")
    public ResponseEntity<List<Lead>> getLeadsBySource(@PathVariable("source") String source) {
        try {
            List<Lead> leads = leadService.getLeadsBySource(source);
            
            if (leads.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            return new ResponseEntity<>(leads, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get leads by organisation
     * GET /api/leads/organisation/{organisationId}
     */
    @GetMapping("/organisation/{organisationId}")
    public ResponseEntity<List<Lead>> getLeadsByOrganisation(@PathVariable("organisationId") Long organisationId) {
        try {
            List<Lead> leads = leadService.getLeadsByOrganisation(organisationId);
            
            if (leads.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            return new ResponseEntity<>(leads, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get leads by contact
     * GET /api/leads/contact/{contactId}
     */
    @GetMapping("/contact/{contactId}")
    public ResponseEntity<List<Lead>> getLeadsByContact(@PathVariable("contactId") Long contactId) {
        try {
            List<Lead> leads = leadService.getLeadsByContact(contactId);
            
            if (leads.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            return new ResponseEntity<>(leads, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get open leads (not closed)
     * GET /api/leads/open
     */
    @GetMapping("/open")
    public ResponseEntity<List<Lead>> getOpenLeads() {
        try {
            List<Lead> leads = leadService.getOpenLeads();
            
            if (leads.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            return new ResponseEntity<>(leads, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get won leads
     * GET /api/leads/won
     */
    @GetMapping("/won")
    public ResponseEntity<List<Lead>> getWonLeads() {
        try {
            List<Lead> leads = leadService.getWonLeads();
            
            if (leads.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            return new ResponseEntity<>(leads, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get lost leads
     * GET /api/leads/lost
     */
    @GetMapping("/lost")
    public ResponseEntity<List<Lead>> getLostLeads() {
        try {
            List<Lead> leads = leadService.getLostLeads();
            
            if (leads.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            return new ResponseEntity<>(leads, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Search leads
     * GET /api/leads/search?term={searchTerm}
     */
    @GetMapping("/search")
    public ResponseEntity<List<Lead>> searchLeads(@RequestParam("term") String term) {
        try {
            List<Lead> results = leadService.searchLeads(term);
            
            return results.isEmpty()
                    ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                    : new ResponseEntity<>(results, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Change lead stage
     * PUT /api/leads/{id}/stage
     */
    @PutMapping("/{id}/stage")
    public ResponseEntity<?> changeLeadStage(@PathVariable("id") Long id, @RequestBody Map<String, String> payload) {
        try {
            String newStage = payload.get("stage");
            boolean success = leadService.changeLeadStage(id, newStage);
            
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Stage updated successfully"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to change stage: " + e.getMessage()));
        }
    }
    
    /**
     * Get leads closing between dates
     * GET /api/leads/closing?start={startDate}&end={endDate}
     */
    @GetMapping("/closing")
    public ResponseEntity<List<Lead>> getLeadsClosingBetween(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<Lead> leads = leadService.getLeadsClosingBetween(startDate, endDate);
            
            if (leads.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            return new ResponseEntity<>(leads, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get overdue leads
     * GET /api/leads/overdue
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<Lead>> getOverdueLeads() {
        try {
            List<Lead> leads = leadService.getOverdueLeads();
            
            if (leads.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            return new ResponseEntity<>(leads, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get leads closing this month
     * GET /api/leads/closing-this-month
     */
    @GetMapping("/closing-this-month")
    public ResponseEntity<List<Lead>> getLeadsClosingThisMonth() {
        try {
            List<Lead> leads = leadService.getLeadsClosingThisMonth();
            
            if (leads.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            return new ResponseEntity<>(leads, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get dashboard statistics
     * GET /api/leads/dashboard/stats
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = leadService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get lead count by stage
     * GET /api/leads/dashboard/stage-count
     */
    @GetMapping("/dashboard/stage-count")
    public ResponseEntity<Map<String, Long>> getLeadCountByStage() {
        try {
            Map<String, Long> stageCount = leadService.getLeadCountByStage();
            return ResponseEntity.ok(stageCount);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Convert lead to organisation and contact
     * POST /api/leads/{id}/convert
     */
    @PostMapping("/{id}/convert")
    public ResponseEntity<?> convertLead(@PathVariable("id") Long id) {
        try {
            Map<String, Long> result = leadService.convertLead(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to convert lead: " + e.getMessage()));
        }
    }
}
