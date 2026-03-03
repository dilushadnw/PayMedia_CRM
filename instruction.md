# PayMedia CRM - Developer Instructions (Phase 1)

**Role:** You are a Senior Full Stack Developer assisting me in building an Enterprise CRM.
**Project:** PayMedia Enterprise CRM & Project Lifecycle System.
**Current Focus:** Module 4.1 - Organisation Management [Source: BRD Page 8].

---

## 1. Technology Stack
* **Database:** MySQL
* **Backend:** Java Spring Boot (Maven, Spring Data JPA, Lombok)
* **Frontend:** React.js (Axios, Bootstrap for UI)
* **Architecture:** REST API-First, Controller-Service-Repository pattern.

---

## 2. Business Rules (CRITICAL)
Based on the Business Requirements Document (BRD), you must strictly follow these rules:
1.  **No-Delete Architecture:** No record can ever be hard-deleted from the database [Source: BRD Page 6].
    * *Implementation:* Use a `status` field (Values: 'ACTIVE', 'INACTIVE'). The "Delete" API should only update this status.
2.  **Single Source of Truth:** Data must be consistent and validated [Source: BRD Page 6].

---

## 3. Database Requirements (MySQL)

Please generate the SQL script or JPA Entity to create the `organisation` table with the following core fields as per BRD Page 8:

* `id` (Primary Key, Auto Increment)
* `company_name` (String, Not Null) - *Legal name*
* `trading_name` (String)
* `registration_number` (String)
* `address` (String)
* `industry` (String)
* `status` (String, Default: 'ACTIVE')
* `created_at` (Timestamp)
* `modified_at` (Timestamp)

---

## 4. Backend Requirements (Spring Boot)

Generate the following files:

1.  **Entity (`Organisation.java`):** Map the fields above. Use `@Data` (Lombok).
2.  **Repository (`OrganisationRepository.java`):** Extend `JpaRepository`. Add a custom query to find only 'ACTIVE' organisations: `findAllByStatus(String status)`.
3.  **Service (`OrganisationService.java`):**
    * `createOrganisation()`: Save new data.
    * `getAllActiveOrganisations()`: Return only active records.
    * `softDeleteOrganisation(Long id)`: **DO NOT DELETE.** Fetch the record, set `status` to 'INACTIVE', and save it.
4.  **Controller (`OrganisationController.java`):**
    * `POST /api/organisations`: Create.
    * `GET /api/organisations`: Get All Active.
    * `DELETE /api/organisations/{id}`: Trigger Soft Delete.
    * **CORS:** Add `@CrossOrigin(origins = "http://localhost:3000")` to allow React access.

---

## 5. Frontend Requirements (React)

Generate a functional React component `OrganisationManager.js` with the following:

1.  **Setup:** Use `axios` for API calls and `Bootstrap` for styling.
2.  **View List:** A table displaying Company Name, Industry, and Status.
3.  **Add New:** A form at the top to input Name, Reg Number, and Industry.
    * On Submit: Call `POST /api/organisations`.
4.  **Delete Action:** A "Delete" button in the table row.
    * On Click: Call `DELETE /api/organisations/{id}` and refresh the list.
5.  **State Management:** Use `useState` for storing the list and form data, and `useEffect` to load data on page mount.