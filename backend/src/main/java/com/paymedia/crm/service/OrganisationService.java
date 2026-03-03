package com.paymedia.crm.service;

import com.paymedia.crm.entity.Organisation;
import com.paymedia.crm.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * PayMedia CRM - Organisation Service
 * Module 4.1 - Organisation Management [BRD Page 8]
 * 
 * Business logic layer for Organisation management
 * Implements No-Delete Architecture [BRD Page 6]
 */
@Service
public class OrganisationService {
    
    @Autowired
    private OrganisationRepository organisationRepository;
    
    /**
     * Create a new organisation
     * 
     * @param organisation The organisation entity to create
     * @return The saved organisation entity
     */
    public Organisation createOrganisation(Organisation organisation) {
        // Set default status to ACTIVE if not provided
        if (organisation.getStatus() == null || organisation.getStatus().isEmpty()) {
            organisation.setStatus("ACTIVE");
        }
        return organisationRepository.save(organisation);
    }
    
    /**
     * Get all active organisations
     * Implements Single Source of Truth principle [BRD Page 6]
     * 
     * @return List of all active organisations
     */
    public List<Organisation> getAllActiveOrganisations() {
        return organisationRepository.findAllByStatus("ACTIVE");
    }
    
    /**
     * Get organisation by ID
     * 
     * @param id The organisation ID
     * @return Optional containing the organisation if found
     */
    public Optional<Organisation> getOrganisationById(Long id) {
        return organisationRepository.findById(id);
    }
    
    /**
     * Soft delete an organisation
     * IMPORTANT: This method does NOT hard-delete the record from database
     * It only changes the status to 'INACTIVE' [BRD Page 6 - No-Delete Architecture]
     * 
     * @param id The organisation ID to soft delete
     * @return true if soft deleted successfully, false if organisation not found
     */
    public boolean softDeleteOrganisation(Long id) {
        Optional<Organisation> organisationOptional = organisationRepository.findById(id);
        
        if (organisationOptional.isPresent()) {
            Organisation organisation = organisationOptional.get();
            organisation.setStatus("INACTIVE"); // Soft delete: Change status to INACTIVE
            organisationRepository.save(organisation);
            return true;
        }
        
        return false; // Organisation not found
    }
    
    /**
     * Update an existing organisation
     * 
     * @param id The organisation ID to update
     * @param updatedOrganisation The organisation data to update
     * @return Optional containing the updated organisation if found
     */
    public Optional<Organisation> updateOrganisation(Long id, Organisation updatedOrganisation) {
        Optional<Organisation> existingOrganisation = organisationRepository.findById(id);
        
        if (existingOrganisation.isPresent()) {
            Organisation organisation = existingOrganisation.get();
            organisation.setCompanyName(updatedOrganisation.getCompanyName());
            organisation.setTradingName(updatedOrganisation.getTradingName());
            organisation.setRegistrationNumber(updatedOrganisation.getRegistrationNumber());
            organisation.setAddress(updatedOrganisation.getAddress());
            organisation.setIndustry(updatedOrganisation.getIndustry());
            
            Organisation savedOrganisation = organisationRepository.save(organisation);
            return Optional.of(savedOrganisation);
        }
        
        return Optional.empty();
    }
}
