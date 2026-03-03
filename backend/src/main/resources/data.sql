-- PayMedia CRM - Sample Data for H2 Database
-- This file automatically loads when the application starts

-- Insert sample organisations
INSERT INTO organisation (company_name, trading_name, registration_number, address, industry, status, created_at, modified_at) 
VALUES 
('PayMedia Solutions Ltd', 'PayMedia', 'REG-2024-001', '123 Business Street, Colombo', 'Technology', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Tech Innovators (Pvt) Ltd', 'TechInn', 'REG-2024-002', '456 Innovation Avenue, Kandy', 'Software Development', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Global Finance Corp', 'GlobalFin', 'REG-2024-003', '789 Finance Road, Galle', 'Finance', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
