package com.paymedia.crm.controller;

import com.paymedia.crm.entity.Organisation;
import com.paymedia.crm.service.OrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * PayMedia CRM - Organisation REST Controller
 * Module 4.1 - Organisation Management [BRD Page 8]
 * 
 * REST API endpoints for Organisation CRUD operations
 * Implements No-Delete Architecture [BRD Page 6]
 */
@RestController
@RequestMapping("/api/organisations")
@CrossOrigin(origins = "http://localhost:3000") // Allow React frontend access
public class OrganisationController {
    
    @Autowired
    private OrganisationService organisationService;
    
    /**
     * Create a new organisation
     * POST /api/organisations
     * 
     * @param organisation The organisation data from request body
     * @return Created organisation with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Organisation> createOrganisation(@RequestBody Organisation organisation) {
        try {
            Organisation savedOrganisation = organisationService.createOrganisation(organisation);
            return new ResponseEntity<>(savedOrganisation, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get all active organisations
     * GET /api/organisations
     * 
     * @return List of all active organisations
     */
    @GetMapping
    public ResponseEntity<List<Organisation>> getAllActiveOrganisations() {
        try {
            List<Organisation> organisations = organisationService.getAllActiveOrganisations();
            
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
     * 
     * @param id The organisation ID
     * @return Organisation data if found, 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Organisation> getOrganisationById(@PathVariable("id") Long id) {
        Optional<Organisation> organisation = organisationService.getOrganisationById(id);
        
        return organisation
                .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Soft delete an organisation
     * DELETE /api/organisations/{id}
     * 
     * IMPORTANT: This does NOT hard-delete the record [BRD Page 6]
     * It only sets the status to 'INACTIVE' (No-Delete Architecture)
     * 
     * @param id The organisation ID to soft delete
     * @return HTTP 204 if successful, 404 if organisation not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> softDeleteOrganisation(@PathVariable("id") Long id) {
        try {
            boolean deleted = organisationService.softDeleteOrganisation(id);
            
            if (deleted) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Successfully soft-deleted
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Organisation not found
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Update an existing organisation
     * PUT /api/organisations/{id}
     * 
     * @param id The organisation ID to update
     * @param organisation The updated organisation data
     * @return Updated organisation if found, 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Organisation> updateOrganisation(
            @PathVariable("id") Long id,
            @RequestBody Organisation organisation) {
        
        Optional<Organisation> updatedOrganisation = organisationService.updateOrganisation(id, organisation);
        
        return updatedOrganisation
                .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
