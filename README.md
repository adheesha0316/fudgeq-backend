# FudgeQ Backend 🍪

Java Spring Boot backend for FudgeQ chocolate and cookies business.

### 🚀 Tech Stack
- **Java 17** | **Spring Boot 3.4.3**
- **Security:** Spring Security + JWT
- **Database:** MySQL + Hibernate

### 🛠 Quick Setup
1. **DB:** Create `fudgeq_db` in MySQL.
2. **Config:** Update `application.properties` with your MySQL username/password.
3. **Run:** `mvn spring-boot:run`

### 🛣 Main Endpoints
- `POST /api/v1/auth/register` - User Registration
- `POST /api/v1/auth/authenticate` - Login (Get Token)
- `GET /api/v1/products` - View items
- `POST /api/v1/admin/products` - Manage items (Admin Only)

---
*Developed for FudgeQ Startup.*
