<<<<<<< HEAD
import React from 'react';
import './App.css';
import OrganisationManager from './components/OrganisationManager';
=======
import React, { useState } from 'react';
import './App.css';
import OrganisationManager from './components/OrganisationManager';
import ContactManager from './components/ContactManager';
import LeadManager from './components/LeadManager';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';
>>>>>>> ae3bcba

/**
 * PayMedia CRM - Main App Component
 * Enterprise CRM & Project Lifecycle System
<<<<<<< HEAD
 */
function App() {
  return (
    <div className="App">
      <OrganisationManager />
=======
 * 
 * Phase 1: Organisation Management ✅
 * Phase 2: Contact Management ✅
 * Phase 3: Lead Management ✅
 */
function App() {
  const [activeTab, setActiveTab] = useState('organisations');

  return (
    <div className="App">
      {/* Header */}
      <nav className="navbar navbar-dark bg-dark shadow-sm">
        <div className="container-fluid">
          <span className="navbar-brand mb-0 h1">
            <i className="bi bi-building"></i> PayMedia CRM System
          </span>
          <span className="text-white">
            <i className="bi bi-calendar3"></i> {new Date().toLocaleDateString()}
          </span>
        </div>
      </nav>

      {/* Navigation Tabs */}
      <div className="container-fluid mt-3">
        <ul className="nav nav-tabs">
          <li className="nav-item">
            <button
              className={`nav-link ${activeTab === 'organisations' ? 'active' : ''}`}
              onClick={() => setActiveTab('organisations')}
            >
              <i className="bi bi-building"></i> Organisations
            </button>
          </li>
          <li className="nav-item">
            <button
              className={`nav-link ${activeTab === 'contacts' ? 'active' : ''}`}
              onClick={() => setActiveTab('contacts')}
            >
              <i className="bi bi-people-fill"></i> Contacts
            </button>
          </li>
          <li className="nav-item">
            <button
              className={`nav-link ${activeTab === 'leads' ? 'active' : ''}`}
              onClick={() => setActiveTab('leads')}
            >
              <i className="bi bi-graph-up"></i> Leads
            </button>
          </li>
        </ul>

        {/* Tab Content */}
        <div className="tab-content mt-3">
          {activeTab === 'organisations' && <OrganisationManager />}
          {activeTab === 'contacts' && <ContactManager />}
          {activeTab === 'leads' && <LeadManager />}
        </div>
      </div>

      {/* Footer */}
      <footer className="mt-5 py-3 bg-light text-center text-muted">
        <div className="container">
          <p className="mb-0">
            PayMedia CRM v2.0 | Built with <i className="bi bi-heart-fill text-danger"></i> using Java 21, Spring Boot 3.2 & React 18
          </p>
        </div>
      </footer>
>>>>>>> ae3bcba
    </div>
  );
}

export default App;
