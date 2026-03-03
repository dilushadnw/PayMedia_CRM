package com.paymedia.crm.controller;

import com.paymedia.crm.entity.Contact;
import com.paymedia.crm.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Contact REST Controller - API Layer
 * Phase 2: Contact Management Module
 * 
 * Provides RESTful endpoints for Contact CRUD operations
 * Base URL: /api/contacts
 * 
 * CORS enabled for React frontend (http://localhost:3000 and :3001)
 */
@RestController
@RequestMapping("/api/contacts")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class ContactController {
    
    @Autowired
    private ContactService contactService;
    
    /**
     * Create new contact
     * POST /api/contacts
     * 
     * Request Body: Contact JSON with organisation.id
     * Example:
     * {
     *   "firstName": "John",
     *   "lastName": "Doe",
     *   "email": "john.doe@example.com",
     *   "phone": "0771234567",
     *   "organisation": { "id": 1 },
     *   "position": "Sales Manager"
     * }
     * 
     * @param contact Contact to create
     * @return 201 Created with contact data, or 400 Bad Request if validation fails
     */
    @PostMapping
    public ResponseEntity<?> createContact(@RequestBody Contact contact) {
        try {
            Contact saved = contactService.createContact(contact);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create contact: " + e.getMessage()));
        }
    }
    
    /**
     * Get all active contacts
     * GET /api/contacts
     * 
     * @return 200 OK with list of contacts, or 204 No Content if empty
     */
    @GetMapping
    public ResponseEntity<List<Contact>> getAllActiveContacts() {
        try {
            List<Contact> contacts = contactService.getAllActiveContacts();
            
            if (contacts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            return new ResponseEntity<>(contacts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get contact by ID
     * GET /api/contacts/{id}
     * 
     * @param id Contact ID
     * @return 200 OK with contact data, or 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable("id") Long id) {
        return contactService.getContactById(id)
                .map(contact -> new ResponseEntity<>(contact, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Get all contacts for an organisation
     * GET /api/contacts/organisation/{organisationId}
     * 
     * @param organisationId Organisation ID
     * @return 200 OK with list of contacts, or 204 No Content if empty
     */
    @GetMapping("/organisation/{organisationId}")
    public ResponseEntity<List<Contact>> getContactsByOrganisation(@PathVariable("organisationId") Long organisationId) {
        try {
            List<Contact> contacts = contactService.getContactsByOrganisation(organisationId);
            
            if (contacts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            return new ResponseEntity<>(contacts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get primary contact for an organisation
     * GET /api/contacts/organisation/{organisationId}/primary
     * 
     * @param organisationId Organisation ID
     * @return 200 OK with contact data, or 404 Not Found if no primary contact
     */
    @GetMapping("/organisation/{organisationId}/primary")
    public ResponseEntity<Contact> getPrimaryContact(@PathVariable("organisationId") Long organisationId) {
        return contactService.getPrimaryContact(organisationId)
                .map(contact -> new ResponseEntity<>(contact, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Update existing contact
     * PUT /api/contacts/{id}
     * 
     * @param id Contact ID
     * @param contact Updated contact data
     * @return 200 OK with updated contact, or 404 Not Found, or 400 Bad Request
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateContact(@PathVariable("id") Long id, @RequestBody Contact contact) {
        try {
            return contactService.updateContact(id, contact)
                    .map(updated -> new ResponseEntity<>(updated, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update contact: " + e.getMessage()));
        }
    }
    
    /**
     * Soft delete contact
     * DELETE /api/contacts/{id}
     * 
     * IMPORTANT: This does NOT permanently delete from database
     * Sets status to 'INACTIVE'
     * 
     * @param id Contact ID
     * @return 204 No Content if successful, or 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> softDeleteContact(@PathVariable("id") Long id) {
        try {
            boolean deleted = contactService.softDeleteContact(id);
            
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
     * Search contacts
     * GET /api/contacts/search?term={searchTerm}
     * 
     * Searches across: firstName, lastName, email, position
     * 
     * @param term Search term
     * @return 200 OK with matching contacts, or 204 No Content if none found
     */
    @GetMapping("/search")
    public ResponseEntity<List<Contact>> searchContacts(@RequestParam("term") String term) {
        try {
            List<Contact> results = contactService.searchContacts(term);
            
            return results.isEmpty()
                    ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                    : new ResponseEntity<>(results, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get contact by email
     * GET /api/contacts/email/{email}
     * 
     * @param email Email address
     * @return 200 OK with contact data, or 404 Not Found
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Contact> getContactByEmail(@PathVariable("email") String email) {
        return contactService.getContactByEmail(email)
                .map(contact -> new ResponseEntity<>(contact, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Count contacts for an organisation
     * GET /api/contacts/organisation/{organisationId}/count
     * 
     * @param organisationId Organisation ID
     * @return 200 OK with count
     */
    @GetMapping("/organisation/{organisationId}/count")
    public ResponseEntity<Map<String, Long>> countContacts(@PathVariable("organisationId") Long organisationId) {
        try {
            long count = contactService.countContactsByOrganisation(organisationId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Set contact as primary for its organisation
     * PUT /api/contacts/{id}/set-primary
     * 
     * Automatically unsets other primary contacts for the organisation
     * 
     * @param id Contact ID
     * @return 200 OK if successful, or 404 Not Found, or 400 Bad Request
     */
    @PutMapping("/{id}/set-primary")
    public ResponseEntity<?> setPrimaryContact(@PathVariable("id") Long id) {
        try {
            boolean success = contactService.setPrimaryContact(id);
            
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Contact set as primary"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to set primary contact: " + e.getMessage()));
        }
    }
    
    /**
     * Restore soft-deleted contact
     * PUT /api/contacts/{id}/restore
     * 
     * Sets status back to ACTIVE
     * 
     * @param id Contact ID
     * @return 200 OK if successful, or 404 Not Found
     */
    @PutMapping("/{id}/restore")
    public ResponseEntity<?> restoreContact(@PathVariable("id") Long id) {
        try {
            boolean success = contactService.restoreContact(id);
            
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Contact restored"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to restore contact: " + e.getMessage()));
        }
    }
}
