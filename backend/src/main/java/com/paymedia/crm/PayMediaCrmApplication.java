package com.paymedia.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * PayMedia CRM - Main Application Class
 * Enterprise CRM & Project Lifecycle System
 * 
 * Module 4.1 - Organisation Management [BRD Page 8]
 */
@SpringBootApplication
public class PayMediaCrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayMediaCrmApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("PayMedia CRM Application Started!");
        System.out.println("API Base URL: http://localhost:8080/api");
        System.out.println("========================================\n");
    }
}
