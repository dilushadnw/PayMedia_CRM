# PayMedia CRM - Organisation Management System

**Enterprise CRM & Project Lifecycle System**  
**Module 4.1 - Organisation Management** [BRD Page 8]

---

## 📋 Overview

A complete full-stack CRM system for managing organisations with CRUD operations. This application implements **No-Delete Architecture** where records are never hard-deleted from the database, only marked as INACTIVE.

### Technology Stack

- **Database:** MySQL
- **Backend:** Java Spring Boot (Maven, Spring Data JPA, Lombok)
- **Frontend:** React.js (Axios, Bootstrap)
- **Architecture:** REST API-First, Controller-Service-Repository pattern

---

## 📁 Project Structure

```
crm test 2/
├── database/
│   └── organisation_schema.sql       # MySQL database schema
├── backend/
│   ├── pom.xml                        # Maven configuration
│   └── src/main/
│       ├── java/com/paymedia/crm/
│       │   ├── PayMediaCrmApplication.java  # Main Spring Boot application
│       │   ├── entity/
│       │   │   └── Organisation.java         # JPA Entity
│       │   ├── repository/
│       │   │   └── OrganisationRepository.java  # Data access layer
│       │   ├── service/
│       │   │   └── OrganisationService.java     # Business logic layer
│       │   └── controller/
│       │       └── OrganisationController.java  # REST API endpoints
│       └── resources/
│           └── application.properties         # Spring Boot configuration
└── frontend/
    ├── package.json                   # NPM dependencies
    ├── public/
    │   └── index.html                 # HTML template
    └── src/
        ├── index.js                   # React entry point
        ├── App.js                     # Main App component
        └── components/
            └── OrganisationManager.js # Organisation CRUD component
```

---

## 🚀 Setup Instructions

### 🎉 OPTION 1: H2 In-Memory Database (NO INSTALLATION NEEDED!)

**✅ Recommended for Quick Testing - No MySQL Required!**

The application is **pre-configured to use H2 Database** - just run it!
- No database installation needed
- Sample data automatically loads
- Perfect for development and testing
- Web console at: http://localhost:8080/h2-console

**See: [QUICKSTART_NO_MYSQL.md](QUICKSTART_NO_MYSQL.md) for instant setup!**

---

### OPTION 2: MySQL Database (For Production)

#### Step 1: Install MySQL
- Download and install MySQL Server from [https://dev.mysql.com/downloads/mysql/](https://dev.mysql.com/downloads/mysql/)
- Make sure MySQL is running on port `3306`

#### Step 2: Create Database
```sql
CREATE DATABASE paymedia_crm;
USE paymedia_crm;
```

#### Step 3: Run Schema Script
```bash
# Navigate to database folder
cd "c:\dnw\incubationPayMedia\crm test 2\database"

# Import schema (from MySQL command line or MySQL Workbench)
SOURCE organisation_schema.sql;
```

Or use MySQL Workbench to execute the SQL file.

#### Step 4: Update Configuration
Edit `backend/src/main/resources/application.properties` and switch to MySQL:
```properties
# Comment out H2 configuration
# spring.datasource.url=jdbc:h2:mem:paymedia_crm

# Uncomment MySQL configuration
spring.datasource.url=jdbc:mysql://localhost:3306/paymedia_crm
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

Also update `pom.xml` to use MySQL instead of H2.

---

### 2. Backend Setup (Spring Boot)

#### Prerequisites
- Java JDK 11 or higher
- Maven 3.6 or higher

#### Step 1: Configure Database Connection (Optional - H2 is pre-configured)
By default, the app uses **H2 in-memory database** - no configuration needed!

If you want to use MySQL instead, edit `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/paymedia_crm?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

#### Step 2: Build and Run
```bash
# Navigate to backend folder
cd "c:\dnw\incubationPayMedia\crm test 2\backend"

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

Backend will start on: **http://localhost:8080**

#### Verify Backend
Open browser and visit: **http://localhost:8080/api/organisations**

---

### 3. Frontend Setup (React)

#### Prerequisites
- Node.js 14 or higher
- npm or yarn

#### Step 1: Install Dependencies
```bash
# Navigate to frontend folder
cd "c:\dnw\incubationPayMedia\crm test 2\frontend"

# Install dependencies
npm install
```

#### Step 2: Run Development Server
```bash
npm start
```

Frontend will start on: **http://localhost:3000**

---

## 📡 API Endpoints

### Base URL: `http://localhost:8080/api/organisations`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/organisations` | Get all active organisations |
| GET | `/api/organisations/{id}` | Get organisation by ID |
| POST | `/api/organisations` | Create new organisation |
| PUT | `/api/organisations/{id}` | Update organisation |
| DELETE | `/api/organisations/{id}` | Soft delete (sets status to INACTIVE) |

### Example Request (POST)
```json
{
  "companyName": "PayMedia Solutions Ltd",
  "tradingName": "PayMedia",
  "registrationNumber": "REG-2024-001",
  "address": "123 Business Street, Colombo",
  "industry": "Technology"
}
```

---

## 🔒 Business Rules (CRITICAL)

### 1. No-Delete Architecture [BRD Page 6]
- **No record can ever be hard-deleted from the database**
- All "delete" operations are soft deletes (status changes to 'INACTIVE')
- The `DELETE /api/organisations/{id}` endpoint only updates the status field

### 2. Single Source of Truth [BRD Page 6]
- Data must be consistent and validated
- Only ACTIVE organisations are returned by default

---

## 🎨 Frontend Features

1. **View List:** Table displaying all active organisations
2. **Add New:** Form to create new organisations
3. **Soft Delete:** Delete button that marks organisations as INACTIVE
4. **Real-time Updates:** List refreshes after create/delete operations
5. **Bootstrap UI:** Professional, responsive design

---

## 🧪 Testing the Application

### 1. Create Organisation
1. Open **http://localhost:3000**
2. Fill in the form fields:
   - Company Name (required)
   - Industry (required)
   - Other fields (optional)
3. Click "Add Organisation"

### 2. View Organisations
The table will automatically display all active organisations.

### 3. Delete Organisation (Soft Delete)
1. Click the "Delete" button next to any organisation
2. Confirm the deletion
3. The organisation will be removed from the view (status set to INACTIVE)
4. The record still exists in the database with `status = 'INACTIVE'`

---

## 🛠 Troubleshooting

### Backend Issues

**Problem:** Backend fails to start
- **Solution:** Check if MySQL is running and database credentials are correct in `application.properties`

**Problem:** "Table doesn't exist" error
- **Solution:** Run the `organisation_schema.sql` script in MySQL

### Frontend Issues

**Problem:** API calls fail with CORS error
- **Solution:** Verify `@CrossOrigin(origins = "http://localhost:3000")` is present in the controller

**Problem:** "Network Error" when calling API
- **Solution:** Make sure backend is running on port 8080

---

## 📝 Development Notes

- Backend uses **Lombok** for reducing boilerplate code
- Frontend uses **Axios** for HTTP requests
- Bootstrap 5 is used for UI styling
- Database timestamps are automatically managed by JPA annotations

---

## 🎯 Next Steps (Future Modules)

- Module 4.2: Contact Management
- Module 4.3: Lead Management
- Module 4.4: Opportunity Management
- User Authentication & Authorization
- Role-based Access Control

---

## 👨‍💻 Developer Information

**Project:** PayMedia Enterprise CRM  
**Current Phase:** Phase 1  
**Module:** 4.1 - Organisation Management  
**Reference:** Business Requirements Document (BRD) Page 8

---

## 📄 License

Internal Use - PayMedia Solutions Ltd

---

**Built with ❤️ using Spring Boot & React**
