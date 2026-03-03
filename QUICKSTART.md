# PayMedia CRM - Quick Start Guide

## 🚀 Get Started in 5 Minutes!

### Step 1: Setup Database (2 minutes)
```bash
# Start MySQL and run these commands:
CREATE DATABASE paymedia_crm;
USE paymedia_crm;
SOURCE "c:\dnw\incubationPayMedia\crm test 2\database\organisation_schema.sql";
```

### Step 2: Configure Backend (30 seconds)
Edit `backend/src/main/resources/application.properties`:
- Set your MySQL username (default: `root`)
- Set your MySQL password (default: `root`)

### Step 3: Start Backend (1 minute)
```bash
cd "c:\dnw\incubationPayMedia\crm test 2\backend"
mvn spring-boot:run
```
Wait for: "PayMedia CRM Application Started!" message

### Step 4: Start Frontend (1.5 minutes)
```bash
# In a new terminal
cd "c:\dnw\incubationPayMedia\crm test 2\frontend"
npm install
npm start
```

### Step 5: Open Browser
Visit: **http://localhost:3000**

---

## ✅ What You Should See

1. A form to add new organisations
2. A table showing existing organisations
3. Sample data (3 organisations) already loaded

---

## 🎯 Try These:

### Add a New Organisation
- Fill in Company Name: "Test Company Ltd"
- Fill in Industry: "Software"
- Click "Add Organisation"

### Delete an Organisation
- Click the "Delete" button on any row
- The organisation disappears (soft deleted - status changed to INACTIVE)

---

## ⚠️ Common Issues

### Backend won't start?
- Check MySQL is running
- Verify database credentials in `application.properties`

### Frontend shows "Network Error"?
- Make sure backend is running on port 8080
- Check `http://localhost:8080/api/organisations` in browser

### Can't connect to database?
- Verify MySQL service is running
- Check username/password in `application.properties`

---

## 📞 Support

For detailed documentation, see: `README.md`

---

**Happy Coding! 🎉**
