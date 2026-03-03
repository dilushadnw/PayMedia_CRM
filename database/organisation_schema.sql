-- PayMedia CRM - Organisation Table Schema
-- Module 4.1 - Organisation Management [BRD Page 8]
-- Database: MySQL

-- Drop table if exists (for development purposes)
DROP TABLE IF EXISTS `organisation`;

-- Create organisation table
CREATE TABLE `organisation` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `company_name` VARCHAR(255) NOT NULL COMMENT 'Legal name of the organisation',
    `trading_name` VARCHAR(255) COMMENT 'Trading/Business name',
    `registration_number` VARCHAR(100) COMMENT 'Company registration number',
    `address` TEXT COMMENT 'Organisation address',
    `industry` VARCHAR(100) COMMENT 'Industry sector',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Record status: ACTIVE or INACTIVE (No-Delete Architecture)',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation timestamp',
    `modified_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Record last modification timestamp',
    INDEX `idx_status` (`status`),
    INDEX `idx_company_name` (`company_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sample data for testing (optional)
INSERT INTO `organisation` (`company_name`, `trading_name`, `registration_number`, `address`, `industry`) VALUES
('PayMedia Solutions Ltd', 'PayMedia', 'REG-2024-001', '123 Business Street, Colombo', 'Technology'),
('Tech Innovators (Pvt) Ltd', 'TechInn', 'REG-2024-002', '456 Innovation Avenue, Kandy', 'Software Development'),
('Global Finance Corp', 'GlobalFin', 'REG-2024-003', '789 Finance Road, Galle', 'Finance');
