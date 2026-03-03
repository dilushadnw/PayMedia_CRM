# PayMedia CRM - Quick Start (NO MySQL NEEDED!)

## 🎉 දැන් MySQL නැතිව Run කරන්න පුළුවන්!

H2 In-Memory Database එක use කරනවා - කිසිම database install කරන්න එපා!

---

## 🚀 Start කරන්නේ කොහොමද? (2 Steps විතරයි!)

### Step 1: Backend Start කරන්න (1 minute)
```bash
cd "c:\dnw\incubationPayMedia\crm test 2\backend"
mvn spring-boot:run
```

⏳ Wait කරන්න "PayMedia CRM Application Started!" message එක එනකන්

### Step 2: Frontend Start කරන්න (1.5 minutes)
```bash
# New terminal එකක
cd "c:\dnw\incubationPayMedia\crm test 2\frontend"
npm install
npm start
```

### Step 3: Browser Open කරන්න
🌐 **http://localhost:3000**

---

## ✨ විශේෂාංග

✅ **MySQL install කරන්න එපා** - H2 database එක built-in!  
✅ **Sample data automatically load වෙනවා** - 3 organisations දැනටමත්!  
✅ **Database Console තියෙනවා** - http://localhost:8080/h2-console  
✅ **All data memory එකේ** - Application restart කළාම reset වෙනවා  

---

## 🗄️ Database Console Access කරන්න

Backend run වෙද්දී database එක බලන්න:

1. Browser එකෙන්: **http://localhost:8080/h2-console**
2. Settings:
   - JDBC URL: `jdbc:h2:mem:paymedia_crm`
   - Username: `sa`
   - Password: (empty - just press Connect)
3. Click "Connect"

දැන් ඔයාට SQL queries run කරන්න පුළුවන්:
```sql
SELECT * FROM organisation;
```

---

## 🎯 Try කරන්න:

### ✏️ Add New Organisation
- Company Name: "My New Company"
- Industry: "Technology"
- Click "Add"

### 🗑️ Delete Organisation
- Click "Delete" button
- Record එක INACTIVE වෙනවා (soft delete)

---

## 📌 Important Notes

⚠️ **Data එක temporary!**
- Application restart කළාම data reset වෙනවා
- Development testing වලට perfect
- Production එකට MySQL use කරන්න ඕනේ

💡 **MySQL එකට switch කරන්න ඕනේ නම්:**
- `application.properties` එක edit කරන්න
- MySQL configuration එක uncomment කරන්න
- H2 configuration එක comment කරන්න

---

## ⚠️ Troubleshooting

### Backend won't start?
```bash
# Maven install කරලා නැද්ද check කරන්න
mvn --version

# නැත්තං Maven install කරන්න:
# https://maven.apache.org/download.cgi
```

### Frontend won't start?
```bash
# Node.js install කරලා නැද්ද check කරන්න
node --version
npm --version

# නැත්තං Node.js install කරන්න:
# https://nodejs.org/
```

### Port 8080 already in use?
Backend port එක වෙනස් කරන්න:
- Edit: `application.properties`
- Change: `server.port=8081`

---

## 🎊 Ready to Go!

**No MySQL! No Database Setup! Just Run & Enjoy!** 🚀

---

For detailed docs: [README.md](README.md)
