# Cafe Fusion Backend

A Spring Boot backend for a modern café, engineered as a **Modular Monolith**. This project showcases clean module boundaries, data isolation, robust security, and a stateless, production-grade API.

> **Why Modular Monolith?**  
Strict module isolation (physically and in code) keeps the system easy to maintain, test, and extend—without “spaghetti code.”

---

## Architecture Overview

- **Strict Modules:** Each domain (Users, Menu, Orders, Events) is an isolated Maven module with “public API” boundaries.
- **Data Isolation:** Automatic, schema-per-module tables using Hibernate.
- **Cross-module Communication:** All business logic between modules is handled via public APIs (never direct DB access!).
- **Production-Ready:** Follows proven Spring practices (JWT security, RBAC, validations, automatic API docs).

**Diagram:**  
![modular-monolith-architecture.svg](modular-monolith-architecture.svg)
---

## 🔑 Features

- **Modular Monolith:**  
  Multi-module Maven structure, one core application, four business domains.
- **Isolation:**  
  Separate DB schemas for each domain (Hibernate).
- **Security:**  
  - JWT-based stateless auth (Spring Security 6)
  - Role-Based Access Control (RBAC):  
    - `ADMIN`: Full access to menus, events, orders  
    - `USER`: Can register, authenticate, place and view own orders  
    - Public: Can view menus, events (no login required)
- **API Docs:**  
  Fully interactive via [Swagger UI](http://localhost:8080/api-docs-ui.html), supporting JWT Bearer authentication.
- **Test Coverage:**  
  - Unit: Pure Mockito for services  
  - Integration: "Slice" @WebMvcTests for controller security and endpoints
- **CI/CD:**  
  GitHub Actions pipeline runs build + all tests for every PR.

---

## 🛠️ Tech Stack

- **Java 21** | **Spring Boot 3** | **Spring Security 6**
- **Spring Data JPA (Hibernate)** | **PostgreSQL** (Docker)
- **Maven (Multi-Module)** | **JUnit 5** | **Mockito**
- **Springdoc-OpenAPI (Swagger UI)**

---

## 🚀 Getting Started

**1. Prerequisites**
- Java 21+
- Docker Desktop
- Git

**2. Start DB Container:**  
```bash
docker run -d -p 5433:5432 --name cafe-db -e POSTGRES_PASSWORD=password -e POSTGRES_USER=postgres -e POSTGRES_DB=coffeeshop postgres:latest
```

**3. Clone & Configure:**  
```bash
git clone https://github.com/YOUR_USERNAME/cafe-backend.git
cd cafe-backend
```
Configure environment variables (IntelliJ > Edit Configurations):
- `SPRING_DATASOURCE_USERNAME=postgres`
- `SPRING_DATASOURCE_PASSWORD=password`
- `JWT_SECRET_KEY=averylongandverysecuresecretkeymustbehere12345`

**4. Run the Project:**  
Start `CafefusionBackendApplication` (IntelliJ “Run” or via Maven).

---

## 🧪 API Demo (Swagger)

1. **Visit Docs:** [http://localhost:8080/api-docs-ui.html](http://localhost:8080/api-docs-ui.html)
2. **Test Public Menus:**  
   - GET `/api/v1/menu`, Try it → sample data.
3. **Check Security:**  
   - POST `/api/v1/events` (unauth): should return `403 Forbidden`.
4. **Authenticate:**  
   - POST `/api/v1/auth/login`  
   - Body:  
     ```json
     {
       "email": "admin@cafefusion.com",
       "password": "admin"
     }
     ```
   - Copy returned JWT, click "Authorize", paste token (no `Bearer ` prefix).
5. **Access Admin Endpoints:**  
   - POST `/api/v1/events` will now succeed.

---

## 📝 License

This project is licensed under the MIT License.  
See [LICENSE](./LICENSE) for details.

---

## 📫 Contact

Interested in contributing or have questions?  
Email: <emretokluk@gmail.com>