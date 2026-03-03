import React, { useState, useEffect } from 'react';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';

/**
 * PayMedia CRM - Organisation Manager Component
 * Module 4.1 - Organisation Management [BRD Page 8]
 * 
 * Frontend component for managing organisations with CRUD operations
 */
const OrganisationManager = () => {
    // Base URL for API calls
    const API_URL = 'http://localhost:8080/api/organisations';
    
    // State management
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
    
    // Load organisations on component mount
    useEffect(() => {
        fetchOrganisations();
    }, []);
    
    /**
     * Fetch all active organisations from the backend
     */
    const fetchOrganisations = async () => {
        setLoading(true);
        setError(null);
        
        try {
            const response = await axios.get(API_URL);
            setOrganisations(response.data);
        } catch (err) {
            if (err.response && err.response.status === 204) {
                // No content - empty list
                setOrganisations([]);
            } else {
                setError('Failed to load organisations. Please try again.');
                console.error('Error fetching organisations:', err);
            }
        } finally {
            setLoading(false);
        }
    };
    
    /**
     * Handle form input changes
     */
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };
    
    /**
     * Handle form submission to create a new organisation
     */
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setSuccessMessage(null);
        
        // Basic validation
        if (!formData.companyName || !formData.industry) {
            setError('Company Name and Industry are required fields.');
            return;
        }
        
        try {
            await axios.post(API_URL, formData);
            
            // Clear form
            setFormData({
                companyName: '',
                tradingName: '',
                registrationNumber: '',
                address: '',
                industry: ''
            });
            
            // Show success message
            setSuccessMessage('Organisation created successfully!');
            
            // Refresh the list
            fetchOrganisations();
            
            // Clear success message after 3 seconds
            setTimeout(() => setSuccessMessage(null), 3000);
        } catch (err) {
            setError('Failed to create organisation. Please try again.');
            console.error('Error creating organisation:', err);
        }
    };
    
    /**
     * Handle soft delete operation
     * IMPORTANT: This does NOT hard-delete the record [BRD Page 6]
     * It only sets the status to 'INACTIVE' (No-Delete Architecture)
     */
    const handleDelete = async (id) => {
        if (!window.confirm('Are you sure you want to delete this organisation?')) {
            return;
        }
        
        setError(null);
        setSuccessMessage(null);
        
        try {
            await axios.delete(`${API_URL}/${id}`);
            setSuccessMessage('Organisation deleted successfully!');
            
            // Refresh the list
            fetchOrganisations();
            
            // Clear success message after 3 seconds
            setTimeout(() => setSuccessMessage(null), 3000);
        } catch (err) {
            setError('Failed to delete organisation. Please try again.');
            console.error('Error deleting organisation:', err);
        }
    };
    
    return (
        <div className="container mt-5">
            <h1 className="mb-4">PayMedia CRM - Organisation Management</h1>
            
            {/* Alert Messages */}
            {error && (
                <div className="alert alert-danger alert-dismissible fade show" role="alert">
                    {error}
                    <button type="button" className="btn-close" onClick={() => setError(null)}></button>
                </div>
            )}
            
            {successMessage && (
                <div className="alert alert-success alert-dismissible fade show" role="alert">
                    {successMessage}
                    <button type="button" className="btn-close" onClick={() => setSuccessMessage(null)}></button>
                </div>
            )}
            
            {/* Add New Organisation Form */}
            <div className="card mb-4">
                <div className="card-header bg-primary text-white">
                    <h5 className="mb-0">Add New Organisation</h5>
                </div>
                <div className="card-body">
                    <form onSubmit={handleSubmit}>
                        <div className="row">
                            <div className="col-md-6 mb-3">
                                <label htmlFor="companyName" className="form-label">
                                    Company Name <span className="text-danger">*</span>
                                </label>
                                <input
                                    type="text"
                                    className="form-control"
                                    id="companyName"
                                    name="companyName"
                                    value={formData.companyName}
                                    onChange={handleInputChange}
                                    required
                                    placeholder="Enter legal company name"
                                />
                            </div>
                            
                            <div className="col-md-6 mb-3">
                                <label htmlFor="tradingName" className="form-label">
                                    Trading Name
                                </label>
                                <input
                                    type="text"
                                    className="form-control"
                                    id="tradingName"
                                    name="tradingName"
                                    value={formData.tradingName}
                                    onChange={handleInputChange}
                                    placeholder="Enter trading name"
                                />
                            </div>
                        </div>
                        
                        <div className="row">
                            <div className="col-md-6 mb-3">
                                <label htmlFor="registrationNumber" className="form-label">
                                    Registration Number
                                </label>
                                <input
                                    type="text"
                                    className="form-control"
                                    id="registrationNumber"
                                    name="registrationNumber"
                                    value={formData.registrationNumber}
                                    onChange={handleInputChange}
                                    placeholder="Enter registration number"
                                />
                            </div>
                            
                            <div className="col-md-6 mb-3">
                                <label htmlFor="industry" className="form-label">
                                    Industry <span className="text-danger">*</span>
                                </label>
                                <input
                                    type="text"
                                    className="form-control"
                                    id="industry"
                                    name="industry"
                                    value={formData.industry}
                                    onChange={handleInputChange}
                                    required
                                    placeholder="Enter industry sector"
                                />
                            </div>
                        </div>
                        
                        <div className="mb-3">
                            <label htmlFor="address" className="form-label">
                                Address
                            </label>
                            <textarea
                                className="form-control"
                                id="address"
                                name="address"
                                value={formData.address}
                                onChange={handleInputChange}
                                rows="2"
                                placeholder="Enter organisation address"
                            ></textarea>
                        </div>
                        
                        <button type="submit" className="btn btn-primary">
                            <i className="bi bi-plus-circle"></i> Add Organisation
                        </button>
                    </form>
                </div>
            </div>
            
            {/* Organisation List */}
            <div className="card">
                <div className="card-header bg-secondary text-white">
                    <h5 className="mb-0">Active Organisations</h5>
                </div>
                <div className="card-body">
                    {loading ? (
                        <div className="text-center">
                            <div className="spinner-border text-primary" role="status">
                                <span className="visually-hidden">Loading...</span>
                            </div>
                        </div>
                    ) : organisations.length === 0 ? (
                        <div className="alert alert-info" role="alert">
                            No organisations found. Add your first organisation using the form above.
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
                                    {organisations.map((org) => (
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
                                                    title="Soft Delete (sets status to INACTIVE)"
                                                >
                                                    <i className="bi bi-trash"></i> Delete
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
