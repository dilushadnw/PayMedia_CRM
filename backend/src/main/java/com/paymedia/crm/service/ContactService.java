package com.paymedia.crm.service;

import com.paymedia.crm.entity.Contact;
import com.paymedia.crm.entity.Organisation;
import com.paymedia.crm.repository.ContactRepository;
import com.paymedia.crm.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Contact Service - Business Logic Layer
 * Phase 2: Contact Management Module
 * 
 * Implements business rules:
 * - No-Delete Architecture (soft delete only)
 * - Email uniqueness validation
 * - Primary contact management
 * - Organisation relationship validation
 * 
 * @Transactional ensures data consistency
 */
@Service
@Transactional
public class ContactService {
    
    @Autowired
    private ContactRepository contactRepository;
    
    @Autowired
    private OrganisationRepository organisationRepository;
    
    /**
     * Create new contact
     * Validates organisation exists and email is unique
     * 
     * @param contact Contact entity to create
     * @return Saved contact
     * @throws IllegalArgumentException if validation fails
     */
    public Contact createContact(Contact contact) {
        // Validation: Organisation must exist
        if (contact.getOrganisation() == null || contact.getOrganisation().getId() == null) {
            throw new IllegalArgumentException("Organisation is required");
        }
        
        Optional<Organisation> org = organisationRepository.findById(contact.getOrganisation().getId());
        if (org.isEmpty()) {
            throw new IllegalArgumentException("Organisation not found with ID: " + contact.getOrganisation().getId());
        }
        
        // Set the full organisation entity
        contact.setOrganisation(org.get());
        
        // Validation: Email must be unique
        if (contactRepository.existsByEmailIgnoreCase(contact.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + contact.getEmail());
        }
        
        // Set default status if not provided
        if (contact.getStatus() == null || contact.getStatus().isEmpty()) {
            contact.setStatus("ACTIVE");
        }
        
        // Set default isPrimary if not provided
        if (contact.getIsPrimary() == null) {
            contact.setIsPrimary(false);
        }
        
        // If this is primary contact, unset other primary contacts for this org
        if (Boolean.TRUE.equals(contact.getIsPrimary())) {
            unsetOtherPrimaryContacts(contact.getOrganisation().getId(), null);
        }
        
        return contactRepository.save(contact);
    }
    
    /**
     * Get all active contacts
     * 
     * @return List of active contacts
     */
    @Transactional(readOnly = true)
    public List<Contact> getAllActiveContacts() {
        return contactRepository.findAllByStatus("ACTIVE");
    }
    
    /**
     * Get contact by ID
     * 
     * @param id Contact ID
     * @return Optional containing contact if found
     */
    @Transactional(readOnly = true)
    public Optional<Contact> getContactById(Long id) {
        return contactRepository.findById(id);
    }
    
    /**
     * Get all active contacts for an organisation
     * 
     * @param organisationId Organisation ID
     * @return List of active contacts
     */
    @Transactional(readOnly = true)
    public List<Contact> getContactsByOrganisation(Long organisationId) {
        return contactRepository.findByOrganisationIdAndStatus(organisationId, "ACTIVE");
    }
    
    /**
     * Get primary contact for an organisation
     * 
     * @param organisationId Organisation ID
     * @return Optional containing primary contact if exists
     */
    @Transactional(readOnly = true)
    public Optional<Contact> getPrimaryContact(Long organisationId) {
        return contactRepository.findByOrganisationIdAndIsPrimaryTrueAndStatus(organisationId, "ACTIVE");
    }
    
    /**
     * Update existing contact
     * Validates email uniqueness (excluding current contact)
     * 
     * @param id Contact ID
     * @param updatedContact Updated contact data
     * @return Optional containing updated contact if found
     * @throws IllegalArgumentException if validation fails
     */
    public Optional<Contact> updateContact(Long id, Contact updatedContact) {
        return contactRepository.findById(id).map(contact -> {
            // Validate email uniqueness (if changed)
            if (!contact.getEmail().equalsIgnoreCase(updatedContact.getEmail())) {
                if (contactRepository.existsByEmailIgnoreCase(updatedContact.getEmail())) {
                    throw new IllegalArgumentException("Email already exists: " + updatedContact.getEmail());
                }
            }
            
            // Update fields
            contact.setFirstName(updatedContact.getFirstName());
            contact.setLastName(updatedContact.getLastName());
            contact.setEmail(updatedContact.getEmail());
            contact.setPhone(updatedContact.getPhone());
            contact.setMobile(updatedContact.getMobile());
            contact.setPosition(updatedContact.getPosition());
            contact.setDepartment(updatedContact.getDepartment());
            
            // Handle primary contact flag change
            if (!contact.getIsPrimary().equals(updatedContact.getIsPrimary())) {
                if (Boolean.TRUE.equals(updatedContact.getIsPrimary())) {
                    // Setting this as primary - unset others
                    unsetOtherPrimaryContacts(contact.getOrganisation().getId(), id);
                }
                contact.setIsPrimary(updatedContact.getIsPrimary());
            }
            
            return contactRepository.save(contact);
        });
    }
    
    /**
     * Soft delete contact
     * IMPORTANT: Does NOT hard-delete from database
     * Sets status to 'INACTIVE'
     * 
     * @param id Contact ID
     * @return true if deleted, false if not found
     */
    public boolean softDeleteContact(Long id) {
        return contactRepository.findById(id).map(contact -> {
            contact.setStatus("INACTIVE");
            contact.setIsPrimary(false);  // Remove primary flag when deleting
            contactRepository.save(contact);
            return true;
        }).orElse(false);
    }
    
    /**
     * Search contacts by name or email
     * 
     * @param searchTerm Search term
     * @return List of matching contacts
     */
    @Transactional(readOnly = true)
    public List<Contact> searchContacts(String searchTerm) {
        return contactRepository.searchContacts(searchTerm, "ACTIVE");
    }
    
    /**
     * Get contact by email
     * 
     * @param email Email address
     * @return Optional containing contact if found
     */
    @Transactional(readOnly = true)
    public Optional<Contact> getContactByEmail(String email) {
        return contactRepository.findByEmailIgnoreCaseAndStatus(email, "ACTIVE");
    }
    
    /**
     * Count contacts for an organisation
     * 
     * @param organisationId Organisation ID
     * @return Number of active contacts
     */
    @Transactional(readOnly = true)
    public long countContactsByOrganisation(Long organisationId) {
        return contactRepository.countByOrganisationIdAndStatus(organisationId, "ACTIVE");
    }
    
    /**
     * Set a contact as primary for its organisation
     * Automatically unsets other primary contacts
     * 
     * @param contactId Contact ID
     * @return true if successful, false if contact not found
     */
    public boolean setPrimaryContact(Long contactId) {
        return contactRepository.findById(contactId).map(contact -> {
            if (!contact.getStatus().equals("ACTIVE")) {
                throw new IllegalArgumentException("Cannot set inactive contact as primary");
            }
            
            // Unset other primary contacts
            unsetOtherPrimaryContacts(contact.getOrganisation().getId(), contactId);
            
            // Set this as primary
            contact.setIsPrimary(true);
            contactRepository.save(contact);
            return true;
        }).orElse(false);
    }
    
    /**
     * Internal helper: Unset all other primary contacts for an organisation
     * 
     * @param organisationId Organisation ID
     * @param excludeContactId Contact ID to exclude (the one being set as primary)
     */
    private void unsetOtherPrimaryContacts(Long organisationId, Long excludeContactId) {
        List<Contact> orgContacts = contactRepository.findByOrganisationId(organisationId);
        
        for (Contact contact : orgContacts) {
            if (Boolean.TRUE.equals(contact.getIsPrimary())) {
                if (excludeContactId == null || !contact.getId().equals(excludeContactId)) {
                    contact.setIsPrimary(false);
                    contactRepository.save(contact);
                }
            }
        }
    }
    
    /**
     * Restore soft-deleted contact
     * Sets status back to ACTIVE
     * 
     * @param id Contact ID
     * @return true if restored, false if not found
     */
    public boolean restoreContact(Long id) {
        return contactRepository.findById(id).map(contact -> {
            contact.setStatus("ACTIVE");
            contactRepository.save(contact);
            return true;
        }).orElse(false);
    }
}
