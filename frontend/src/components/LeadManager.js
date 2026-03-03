import React, { useState, useEffect } from 'react';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';

/**
 * LeadManager Component
 * Phase 3: Lead Management Module
 * 
 * Features:
 * - Track sales leads and opportunities
 * - Link leads to existing organisations/contacts
 * - Manage lead stages and pipeline
 * - Revenue forecasting
 * - Dashboard statistics
 */
const LeadManager = () => {
    const API_URL = 'http://localhost:8080/api/leads';
    const ORG_API_URL = 'http://localhost:8080/api/organisations';
    const CONTACT_API_URL = 'http://localhost:8080/api/contacts';
    
    // Lead Stages
    const LEAD_STAGES = ['New', 'Qualified', 'Proposal', 'Negotiation', 'Closed Won', 'Closed Lost'];
    const LEAD_SOURCES = ['Website', 'Referral', 'Cold Call', 'Email Campaign', 'Social Media', 'Trade Show', 'Other'];
    
    // State Management
    const [leads, setLeads] = useState([]);
    const [organisations, setOrganisations] = useState([]);
    const [contacts, setContacts] = useState([]);
    const [formData, setFormData] = useState({
        leadName: '',
        leadSource: '',
        leadStage: 'New',
        estimatedValue: '',
        probability: '',
        expectedCloseDate: '',
        description: '',
        companyName: '',
        industry: '',
        contactName: '',
        contactEmail: '',
        contactPhone: '',
        organisation: { id: '' },
        contact: { id: '' }
    });
    const [editingId, setEditingId] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null);
    const [filterStage, setFilterStage] = useState('');
    const [stats, setStats] = useState(null);
    const [showStats, setShowStats] = useState(true);
    
    // Load data on mount
    useEffect(() => {
        fetchLeads();
        fetchOrganisations();
        fetchContacts();
        fetchStats();
    }, []);
    
    // Fetch all leads
    const fetchLeads = async () => {
        setLoading(true);
        setError(null);
        
        try {
            const response = await axios.get(API_URL);
            setLeads(response.data);
        } catch (err) {
            if (err.response && err.response.status === 204) {
                setLeads([]);
            } else {
                setError('Failed to load leads');
                console.error('Error:', err);
            }
        } finally {
            setLoading(false);
        }
    };
    
    // Fetch organisations
    const fetchOrganisations = async () => {
        try {
            const response = await axios.get(ORG_API_URL);
            setOrganisations(response.data || []);
        } catch (err) {
            console.error('Error loading organisations:', err);
        }
    };
    
    // Fetch contacts
    const fetchContacts = async () => {
        try {
            const response = await axios.get(CONTACT_API_URL);
            setContacts(response.data || []);
        } catch (err) {
            console.error('Error loading contacts:', err);
        }
    };
    
    // Fetch dashboard stats
    const fetchStats = async () => {
        try {
            const response = await axios.get(`${API_URL}/dashboard/stats`);
            setStats(response.data);
        } catch (err) {
            console.error('Error loading stats:', err);
        }
    };
    
    // Handle form input changes
    const handleInputChange = (e) => {
        const { name, value, type } = e.target;
        
        if (name === 'organisationId') {
            setFormData({
                ...formData,
                organisation: { id: value }
            });
        } else if (name === 'contactId') {
            setFormData({
                ...formData,
                contact: { id: value }
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
            leadName: '',
            leadSource: '',
            leadStage: 'New',
            estimatedValue: '',
            probability: '',
            expectedCloseDate: '',
            description: '',
            companyName: '',
            industry: '',
            contactName: '',
            contactEmail: '',
            contactPhone: '',
            organisation: { id: '' },
            contact: { id: '' }
        });
        setEditingId(null);
    };
    
    // Handle form submission
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setSuccessMessage(null);
        
        // Validation
        if (!formData.leadName) {
            setError('Lead name is required');
            return;
        }
        
        try {
            if (editingId) {
                // Update existing lead
                await axios.put(`${API_URL}/${editingId}`, formData);
                setSuccessMessage('Lead updated successfully!');
            } else {
                // Create new lead
                await axios.post(API_URL, formData);
                setSuccessMessage('Lead created successfully!');
            }
            
            resetForm();
            fetchLeads();
            fetchStats();
            
            setTimeout(() => setSuccessMessage(null), 3000);
        } catch (err) {
            if (err.response && err.response.data && err.response.data.error) {
                setError(err.response.data.error);
            } else {
                setError(editingId ? 'Failed to update lead' : 'Failed to create lead');
            }
            console.error('Error:', err);
        }
    };
    
    // Handle edit
    const handleEdit = (lead) => {
        setFormData({
            leadName: lead.leadName,
            leadSource: lead.leadSource || '',
            leadStage: lead.leadStage,
            estimatedValue: lead.estimatedValue || '',
            probability: lead.probability || '',
            expectedCloseDate: lead.expectedCloseDate || '',
            description: lead.description || '',
            companyName: lead.companyName || '',
            industry: lead.industry || '',
            contactName: lead.contactName || '',
            contactEmail: lead.contactEmail || '',
            contactPhone: lead.contactPhone || '',
            organisation: { id: lead.organisation?.id || '' },
            contact: { id: lead.contact?.id || '' }
        });
        setEditingId(lead.id);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };
    
    // Handle delete
    const handleDelete = async (id) => {
        if (!window.confirm('Are you sure you want to delete this lead?')) {
            return;
        }
        
        setError(null);
        setSuccessMessage(null);
        
        try {
            await axios.delete(`${API_URL}/${id}`);
            setSuccessMessage('Lead deleted successfully!');
            fetchLeads();
            fetchStats();
            
            setTimeout(() => setSuccessMessage(null), 3000);
        } catch (err) {
            setError('Failed to delete lead');
            console.error('Error:', err);
        }
    };
    
    // Change lead stage
    const handleStageChange = async (id, newStage) => {
        try {
            await axios.put(`${API_URL}/${id}/stage`, { stage: newStage });
            setSuccessMessage('Stage updated!');
            fetchLeads();
            fetchStats();
            setTimeout(() => setSuccessMessage(null), 2000);
        } catch (err) {
            setError('Failed to update stage');
            console.error('Error:', err);
        }
    };
    
    // Get stage badge color
    const getStageBadgeColor = (stage) => {
        const colors = {
            'New': 'secondary',
            'Qualified': 'info',
            'Proposal': 'primary',
            'Negotiation': 'warning',
            'Closed Won': 'success',
            'Closed Lost': 'danger'
        };
        return colors[stage] || 'secondary';
    };
    
    // Format currency
    const formatCurrency = (value) => {
        if (!value) return '-';
        return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);
    };
    
    // Filter leads
    const filteredLeads = filterStage 
        ? leads.filter(l => l.leadStage === filterStage)
        : leads;
    
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
                        <i className="bi bi-graph-up text-success"></i> Lead Management & Pipeline
                    </h1>
                </div>
            </div>
            
            {/* Dashboard Stats */}
            {showStats && stats && (
                <div className="row mb-4">
                    <div className="col-md-3">
                        <div className="card bg-primary text-white">
                            <div className="card-body">
                                <h6><i className="bi bi-clipboard-data"></i> Total Leads</h6>
                                <h3>{stats.totalLeads}</h3>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-3">
                        <div className="card bg-info text-white">
                            <div className="card-body">
                                <h6><i className="bi bi-hourglass-split"></i> Open Leads</h6>
                                <h3>{stats.openLeads}</h3>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-3">
                        <div className="card bg-success text-white">
                            <div className="card-body">
                                <h6><i className="bi bi-trophy"></i> Won / Win Rate</h6>
                                <h3>{stats.wonLeads} / {stats.winRate.toFixed(1)}%</h3>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-3">
                        <div className="card bg-warning text-dark">
                            <div className="card-body">
                                <h6><i className="bi bi-currency-dollar"></i> Expected Revenue</h6>
                                <h3>{formatCurrency(stats.expectedRevenue)}</h3>
                            </div>
                        </div>
                    </div>
                </div>
            )}
            
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
                        <div className="card-header bg-success text-white">
                            <h5 className="mb-0">
                                <i className="bi bi-plus-circle-fill"></i> {editingId ? 'Edit Lead' : 'Add New Lead'}
                            </h5>
                        </div>
                        <div className="card-body" style={{maxHeight: '800px', overflowY: 'auto'}}>
                            <form onSubmit={handleSubmit}>
                                {/* Lead Name */}
                                <div className="mb-3">
                                    <label className="form-label">Lead Name *</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        name="leadName"
                                        value={formData.leadName}
                                        onChange={handleInputChange}
                                        placeholder="e.g., Website Redesign Project"
                                        required
                                    />
                                </div>
                                
                                {/* Stage & Source */}
                                <div className="row">
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label">Stage</label>
                                        <select
                                            className="form-select"
                                            name="leadStage"
                                            value={formData.leadStage}
                                            onChange={handleInputChange}
                                        >
                                            {LEAD_STAGES.map(stage => (
                                                <option key={stage} value={stage}>{stage}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label">Source</label>
                                        <select
                                            className="form-select"
                                            name="leadSource"
                                            value={formData.leadSource}
                                            onChange={handleInputChange}
                                        >
                                            <option value="">Select Source</option>
                                            {LEAD_SOURCES.map(source => (
                                                <option key={source} value={source}>{source}</option>
                                            ))}
                                        </select>
                                    </div>
                                </div>
                                
                                {/* Financial Info */}
                                <div className="row">
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label">Est. Value ($)</label>
                                        <input
                                            type="number"
                                            className="form-control"
                                            name="estimatedValue"
                                            value={formData.estimatedValue}
                                            onChange={handleInputChange}
                                            step="0.01"
                                        />
                                    </div>
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label">Probability (%)</label>
                                        <input
                                            type="number"
                                            className="form-control"
                                            name="probability"
                                            value={formData.probability}
                                            onChange={handleInputChange}
                                            min="0"
                                            max="100"
                                        />
                                    </div>
                                </div>
                                
                                {/* Close Date */}
                                <div className="mb-3">
                                    <label className="form-label">Expected Close Date</label>
                                    <input
                                        type="date"
                                        className="form-control"
                                        name="expectedCloseDate"
                                        value={formData.expectedCloseDate}
                                        onChange={handleInputChange}
                                    />
                                </div>
                                
                                {/* Link to Existing */}
                                <div className="mb-3">
                                    <label className="form-label">Link to Organisation</label>
                                    <select
                                        className="form-select"
                                        name="organisationId"
                                        value={formData.organisation.id}
                                        onChange={handleInputChange}
                                    >
                                        <option value="">None (New Company)</option>
                                        {organisations.map(org => (
                                            <option key={org.id} value={org.id}>{org.companyName}</option>
                                        ))}
                                    </select>
                                </div>
                                
                                <div className="mb-3">
                                    <label className="form-label">Link to Contact</label>
                                    <select
                                        className="form-select"
                                        name="contactId"
                                        value={formData.contact.id}
                                        onChange={handleInputChange}
                                    >
                                        <option value="">None (New Contact)</option>
                                        {contacts.map(contact => (
                                            <option key={contact.id} value={contact.id}>
                                                {contact.firstName} {contact.lastName}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                                
                                <hr />
                                <p className="text-muted small">Or enter new company/contact info:</p>
                                
                                {/* Company Info */}
                                <div className="mb-3">
                                    <label className="form-label">Company Name</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        name="companyName"
                                        value={formData.companyName}
                                        onChange={handleInputChange}
                                    />
                                </div>
                                
                                <div className="mb-3">
                                    <label className="form-label">Industry</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        name="industry"
                                        value={formData.industry}
                                        onChange={handleInputChange}
                                    />
                                </div>
                                
                                {/* Contact Info */}
                                <div className="mb-3">
                                    <label className="form-label">Contact Name</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        name="contactName"
                                        value={formData.contactName}
                                        onChange={handleInputChange}
                                    />
                                </div>
                                
                                <div className="mb-3">
                                    <label className="form-label">Contact Email</label>
                                    <input
                                        type="email"
                                        className="form-control"
                                        name="contactEmail"
                                        value={formData.contactEmail}
                                        onChange={handleInputChange}
                                    />
                                </div>
                                
                                <div className="mb-3">
                                    <label className="form-label">Contact Phone</label>
                                    <input
                                        type="tel"
                                        className="form-control"
                                        name="contactPhone"
                                        value={formData.contactPhone}
                                        onChange={handleInputChange}
                                    />
                                </div>
                                
                                {/* Description */}
                                <div className="mb-3">
                                    <label className="form-label">Description</label>
                                    <textarea
                                        className="form-control"
                                        name="description"
                                        value={formData.description}
                                        onChange={handleInputChange}
                                        rows="3"
                                    ></textarea>
                                </div>
                                
                                {/* Buttons */}
                                <div className="d-grid gap-2">
                                    <button type="submit" className="btn btn-success">
                                        <i className="bi bi-save"></i> {editingId ? 'Update Lead' : 'Add Lead'}
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
                                        <i className="bi bi-list-columns"></i> Leads Pipeline ({filteredLeads.length})
                                    </h5>
                                </div>
                                <div className="col-auto">
                                    <button 
                                        className="btn btn-sm btn-light"
                                        onClick={() => setShowStats(!showStats)}
                                    >
                                        <i className={`bi bi-${showStats ? 'eye-slash' : 'eye'}`}></i> Stats
                                    </button>
                                </div>
                            </div>
                        </div>
                        <div className="card-body">
                            {/* Filter */}
                            <div className="mb-3">
                                <select
                                    className="form-select"
                                    value={filterStage}
                                    onChange={(e) => setFilterStage(e.target.value)}
                                >
                                    <option value="">All Stages</option>
                                    {LEAD_STAGES.map(stage => (
                                        <option key={stage} value={stage}>{stage}</option>
                                    ))}
                                </select>
                            </div>
                            
                            {/* Table */}
                            {loading ? (
                                <div className="text-center py-5">
                                    <div className="spinner-border text-success"></div>
                                </div>
                            ) : filteredLeads.length === 0 ? (
                                <div className="alert alert-info">
                                    <i className="bi bi-info-circle"></i> No leads found.
                                </div>
                            ) : (
                                <div className="table-responsive" style={{maxHeight: '700px', overflowY: 'auto'}}>
                                    <table className="table table-hover table-sm">
                                        <thead className="table-dark sticky-top">
                                            <tr>
                                                <th>Lead Name</th>
                                                <th>Company</th>
                                                <th>Value</th>
                                                <th>Stage</th>
                                                <th>Source</th>
                                                <th>Close Date</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {filteredLeads.map(lead => (
                                                <tr key={lead.id}>
                                                    <td>
                                                        <strong>{lead.leadName}</strong>
                                                        {lead.probability && (
                                                            <div className="text-muted small">{lead.probability}%</div>
                                                        )}
                                                    </td>
                                                    <td>
                                                        {lead.organisation?.id ? (
                                                            <span className="badge bg-info">
                                                                {getOrganisationName(lead.organisation.id)}
                                                            </span>
                                                        ) : (
                                                            lead.companyName || '-'
                                                        )}
                                                    </td>
                                                    <td>{formatCurrency(lead.estimatedValue)}</td>
                                                    <td>
                                                        <select
                                                            className={`form-select form-select-sm badge bg-${getStageBadgeColor(lead.leadStage)}`}
                                                            value={lead.leadStage}
                                                            onChange={(e) => handleStageChange(lead.id, e.target.value)}
                                                            style={{border: 'none', color: 'white'}}
                                                        >
                                                            {LEAD_STAGES.map(stage => (
                                                                <option key={stage} value={stage}>{stage}</option>
                                                            ))}
                                                        </select>
                                                    </td>
                                                    <td>
                                                        <small>{lead.leadSource || '-'}</small>
                                                    </td>
                                                    <td>
                                                        <small>{lead.expectedCloseDate || '-'}</small>
                                                    </td>
                                                    <td>
                                                        <div className="btn-group btn-group-sm">
                                                            <button
                                                                className="btn btn-outline-primary btn-sm"
                                                                onClick={() => handleEdit(lead)}
                                                                title="Edit"
                                                            >
                                                                <i className="bi bi-pencil"></i>
                                                            </button>
                                                            <button
                                                                className="btn btn-outline-danger btn-sm"
                                                                onClick={() => handleDelete(lead.id)}
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

export default LeadManager;
