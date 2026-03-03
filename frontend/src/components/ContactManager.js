import React, { useState, useEffect } from 'react';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';

/**
 * ContactManager Component
 * Phase 2: Contact Management Module
 * 
 * Features:
 * - View all active contacts
 * - Create new contacts
 * - Update existing contacts
 * - Soft delete contacts
 * - Link contacts to organisations
 * - Set primary contact
 * - Search contacts
 */
const ContactManager = () => {
    const API_URL = 'http://localhost:8080/api/contacts';
    const ORG_API_URL = 'http://localhost:8080/api/organisations';
    
    // State Management
    const [contacts, setContacts] = useState([]);
    const [organisations, setOrganisations] = useState([]);
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        mobile: '',
        position: '',
        department: '',
        isPrimary: false,
        organisation: { id: '' }
    });
    const [editingId, setEditingId] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [filterOrgId, setFilterOrgId] = useState('');
    
    // Load data on mount
    useEffect(() => {
        fetchContacts();
        fetchOrganisations();
    }, []);
    
    // Fetch all contacts
    const fetchContacts = async () => {
        setLoading(true);
        setError(null);
        
        try {
            const response = await axios.get(API_URL);
            setContacts(response.data);
        } catch (err) {
            if (err.response && err.response.status === 204) {
                setContacts([]);
            } else {
                setError('Failed to load contacts');
                console.error('Error:', err);
            }
        } finally {
            setLoading(false);
        }
    };
    
    // Fetch all organisations for dropdown
    const fetchOrganisations = async () => {
        try {
            const response = await axios.get(ORG_API_URL);
            setOrganisations(response.data || []);
        } catch (err) {
            console.error('Error loading organisations:', err);
        }
    };
    
    // Handle form input changes
    const handleInputChange = (e) => {
        const { name, value, type, checked } = e.target;
        
        if (name === 'organisationId') {
            setFormData({
                ...formData,
                organisation: { id: value }
            });
        } else if (type === 'checkbox') {
            setFormData({
                ...formData,
                [name]: checked
            });
        } else {
            setFormData({
                ...formData,
                [name]: value
            });
        }
    };
    
    // Reset form
    const resetForm = () => {
        setFormData({
            firstName: '',
            lastName: '',
            email: '',
            phone: '',
            mobile: '',
            position: '',
            department: '',
            isPrimary: false,
            organisation: { id: '' }
        });
        setEditingId(null);
    };
    
    // Handle form submission
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setSuccessMessage(null);
        
        // Validation
        if (!formData.firstName || !formData.lastName || !formData.email || !formData.organisation.id) {
            setError('First Name, Last Name, Email and Organisation are required');
            return;
        }
        
        try {
            if (editingId) {
                // Update existing contact
                await axios.put(`${API_URL}/${editingId}`, formData);
                setSuccessMessage('Contact updated successfully!');
            } else {
                // Create new contact
                await axios.post(API_URL, formData);
                setSuccessMessage('Contact created successfully!');
            }
            
            resetForm();
            fetchContacts();
            
            setTimeout(() => setSuccessMessage(null), 3000);
        } catch (err) {
            if (err.response && err.response.data && err.response.data.error) {
                setError(err.response.data.error);
            } else {
                setError(editingId ? 'Failed to update contact' : 'Failed to create contact');
            }
            console.error('Error:', err);
        }
    };
    
    // Handle edit
    const handleEdit = (contact) => {
        setFormData({
            firstName: contact.firstName,
            lastName: contact.lastName,
            email: contact.email,
            phone: contact.phone || '',
            mobile: contact.mobile || '',
            position: contact.position || '',
            department: contact.department || '',
            isPrimary: contact.isPrimary || false,
            organisation: { id: contact.organisation.id }
        });
        setEditingId(contact.id);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };
    
    // Handle delete
    const handleDelete = async (id) => {
        if (!window.confirm('Are you sure you want to delete this contact?')) {
            return;
        }
        
        setError(null);
        setSuccessMessage(null);
        
        try {
            await axios.delete(`${API_URL}/${id}`);
            setSuccessMessage('Contact deleted successfully!');
            fetchContacts();
            
            setTimeout(() => setSuccessMessage(null), 3000);
        } catch (err) {
            setError('Failed to delete contact');
            console.error('Error:', err);
        }
    };
    
    // Set contact as primary
    const handleSetPrimary = async (id) => {
        try {
            await axios.put(`${API_URL}/${id}/set-primary`);
            setSuccessMessage('Primary contact updated!');
            fetchContacts();
            setTimeout(() => setSuccessMessage(null), 3000);
        } catch (err) {
            setError('Failed to set primary contact');
            console.error('Error:', err);
        }
    };
    
    // Search contacts
    const handleSearch = async () => {
        if (!searchTerm) {
            fetchContacts();
            return;
        }
        
        setLoading(true);
        try {
            const response = await axios.get(`${API_URL}/search?term=${searchTerm}`);
            setContacts(response.data || []);
        } catch (err) {
            if (err.response && err.response.status === 204) {
                setContacts([]);
            }
        } finally {
            setLoading(false);
        }
    };
    
    // Filter by organisation
    const filteredContacts = filterOrgId 
        ? contacts.filter(c => c.organisation.id === parseInt(filterOrgId))
        : contacts;
    
    // Get organisation name
    const getOrganisationName = (orgId) => {
        const org = organisations.find(o => o.id === orgId);
        return org ? org.companyName : 'Unknown';
    };
    
    return (
        <div className="container-fluid mt-4">
            <div className="row">
                <div className="col-12">
                    <h1 className="mb-4">
                        <i className="bi bi-people-fill text-primary"></i> Contact Management
                    </h1>
                </div>
            </div>
            
            {/* Alerts */}
            {error && (
                <div className="alert alert-danger alert-dismissible fade show">
                    <i className="bi bi-exclamation-triangle-fill"></i> {error}
                    <button type="button" className="btn-close" onClick={() => setError(null)}></button>
                </div>
            )}
            
            {successMessage && (
                <div className="alert alert-success alert-dismissible fade show">
                    <i className="bi bi-check-circle-fill"></i> {successMessage}
                    <button type="button" className="btn-close" onClick={() => setSuccessMessage(null)}></button>
                </div>
            )}
            
            <div className="row">
                {/* Form Column */}
                <div className="col-lg-4 mb-4">
                    <div className="card shadow-sm">
                        <div className="card-header bg-primary text-white">
                            <h5 className="mb-0">
                                <i className="bi bi-person-plus-fill"></i> {editingId ? 'Edit Contact' : 'Add New Contact'}
                            </h5>
                        </div>
                        <div className="card-body">
                            <form onSubmit={handleSubmit}>
                                {/* Organisation Selection */}
                                <div className="mb-3">
                                    <label className="form-label">Organisation *</label>
                                    <select
                                        className="form-select"
                                        name="organisationId"
                                        value={formData.organisation.id}
                                        onChange={handleInputChange}
                                        required
                                    >
                                        <option value="">Select Organisation</option>
                                        {organisations.map(org => (
                                            <option key={org.id} value={org.id}>
                                                {org.companyName}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                                
                                {/* Name Fields */}
                                <div className="row">
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label">First Name *</label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            name="firstName"
                                            value={formData.firstName}
                                            onChange={handleInputChange}
                                            required
                                        />
                                    </div>
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label">Last Name *</label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            name="lastName"
                                            value={formData.lastName}
                                            onChange={handleInputChange}
                                            required
                                        />
                                    </div>
                                </div>
                                
                                {/* Email */}
                                <div className="mb-3">
                                    <label className="form-label">Email *</label>
                                    <input
                                        type="email"
                                        className="form-control"
                                        name="email"
                                        value={formData.email}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>
                                
                                {/* Phone Fields */}
                                <div className="row">
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label">Phone</label>
                                        <input
                                            type="tel"
                                            className="form-control"
                                            name="phone"
                                            value={formData.phone}
                                            onChange={handleInputChange}
                                        />
                                    </div>
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label">Mobile</label>
                                        <input
                                            type="tel"
                                            className="form-control"
                                            name="mobile"
                                            value={formData.mobile}
                                            onChange={handleInputChange}
                                        />
                                    </div>
                                </div>
                                
                                {/* Professional Info */}
                                <div className="mb-3">
                                    <label className="form-label">Position</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        name="position"
                                        value={formData.position}
                                        onChange={handleInputChange}
                                    />
                                </div>
                                
                                <div className="mb-3">
                                    <label className="form-label">Department</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        name="department"
                                        value={formData.department}
                                        onChange={handleInputChange}
                                    />
                                </div>
                                
                                {/* Primary Contact Checkbox */}
                                <div className="mb-3 form-check">
                                    <input
                                        type="checkbox"
                                        className="form-check-input"
                                        id="isPrimary"
                                        name="isPrimary"
                                        checked={formData.isPrimary}
                                        onChange={handleInputChange}
                                    />
                                    <label className="form-check-label" htmlFor="isPrimary">
                                        Primary Contact
                                    </label>
                                </div>
                                
                                {/* Buttons */}
                                <div className="d-grid gap-2">
                                    <button type="submit" className="btn btn-primary">
                                        <i className="bi bi-save"></i> {editingId ? 'Update Contact' : 'Add Contact'}
                                    </button>
                                    {editingId && (
                                        <button type="button" className="btn btn-secondary" onClick={resetForm}>
                                            <i className="bi bi-x-circle"></i> Cancel Edit
                                        </button>
                                    )}
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
                
                {/* Table Column */}
                <div className="col-lg-8">
                    <div className="card shadow-sm">
                        <div className="card-header bg-secondary text-white">
                            <div className="row align-items-center">
                                <div className="col">
                                    <h5 className="mb-0">
                                        <i className="bi bi-list-ul"></i> Active Contacts ({filteredContacts.length})
                                    </h5>
                                </div>
                            </div>
                        </div>
                        <div className="card-body">
                            {/* Search and Filter */}
                            <div className="row mb-3">
                                <div className="col-md-6">
                                    <div className="input-group">
                                        <input
                                            type="text"
                                            className="form-control"
                                            placeholder="Search contacts..."
                                            value={searchTerm}
                                            onChange={(e) => setSearchTerm(e.target.value)}
                                            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                                        />
                                        <button className="btn btn-outline-primary" onClick={handleSearch}>
                                            <i className="bi bi-search"></i> Search
                                        </button>
                                        {searchTerm && (
                                            <button className="btn btn-outline-secondary" onClick={() => { setSearchTerm(''); fetchContacts(); }}>
                                                <i className="bi bi-x"></i>
                                            </button>
                                        )}
                                    </div>
                                </div>
                                <div className="col-md-6">
                                    <select
                                        className="form-select"
                                        value={filterOrgId}
                                        onChange={(e) => setFilterOrgId(e.target.value)}
                                    >
                                        <option value="">All Organisations</option>
                                        {organisations.map(org => (
                                            <option key={org.id} value={org.id}>
                                                {org.companyName}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                            </div>
                            
                            {/* Table */}
                            {loading ? (
                                <div className="text-center py-5">
                                    <div className="spinner-border text-primary" role="status">
                                        <span className="visually-hidden">Loading...</span>
                                    </div>
                                </div>
                            ) : filteredContacts.length === 0 ? (
                                <div className="alert alert-info">
                                    <i className="bi bi-info-circle"></i> No contacts found.
                                </div>
                            ) : (
                                <div className="table-responsive">
                                    <table className="table table-hover table-striped">
                                        <thead className="table-dark">
                                            <tr>
                                                <th>Name</th>
                                                <th>Email</th>
                                                <th>Phone</th>
                                                <th>Position</th>
                                                <th>Organisation</th>
                                                <th>Primary</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {filteredContacts.map(contact => (
                                                <tr key={contact.id}>
                                                    <td>
                                                        <strong>{contact.firstName} {contact.lastName}</strong>
                                                        {contact.isPrimary && (
                                                            <span className="badge bg-warning text-dark ms-2">
                                                                <i className="bi bi-star-fill"></i> Primary
                                                            </span>
                                                        )}
                                                    </td>
                                                    <td>
                                                        <a href={`mailto:${contact.email}`}>
                                                            <i className="bi bi-envelope"></i> {contact.email}
                                                        </a>
                                                    </td>
                                                    <td>
                                                        {contact.phone && (
                                                            <div><i className="bi bi-telephone"></i> {contact.phone}</div>
                                                        )}
                                                        {contact.mobile && (
                                                            <div><i className="bi bi-phone"></i> {contact.mobile}</div>
                                                        )}
                                                        {!contact.phone && !contact.mobile && '-'}
                                                    </td>
                                                    <td>
                                                        {contact.position || '-'}
                                                        {contact.department && (
                                                            <div className="text-muted small">{contact.department}</div>
                                                        )}
                                                    </td>
                                                    <td>
                                                        <span className="badge bg-info">
                                                            {getOrganisationName(contact.organisation.id)}
                                                        </span>
                                                    </td>
                                                    <td>
                                                        {!contact.isPrimary && (
                                                            <button
                                                                className="btn btn-sm btn-outline-warning"
                                                                onClick={() => handleSetPrimary(contact.id)}
                                                                title="Set as primary contact"
                                                            >
                                                                <i className="bi bi-star"></i>
                                                            </button>
                                                        )}
                                                    </td>
                                                    <td>
                                                        <div className="btn-group btn-group-sm">
                                                            <button
                                                                className="btn btn-outline-primary"
                                                                onClick={() => handleEdit(contact)}
                                                                title="Edit"
                                                            >
                                                                <i className="bi bi-pencil"></i>
                                                            </button>
                                                            <button
                                                                className="btn btn-outline-danger"
                                                                onClick={() => handleDelete(contact.id)}
                                                                title="Delete"
                                                            >
                                                                <i className="bi bi-trash"></i>
                                                            </button>
                                                        </div>
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
            </div>
        </div>
    );
};

export default ContactManager;
