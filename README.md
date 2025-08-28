# ✉️ Spring Boot Mailing System

![](src/main/resources/image/logo.png)

Currently, I’m reengineering my original Java-based 'Client-Server Mailing System' into a Spring Boot 3 REST API to to master the Spring framework
and Hibernate (JPA) via Spring Data JPA. The new application features OAuth2/JWT authentication through Keycloak, role-based task management endpoints,
and robust data persistence.

This project is under active development and will be continuously updated with new features and improvements.

## 📖 Tech stack & highlights

- **Designing and building a Spring Boot 3 REST API**
- **Hibernate (JPA) with Spring Data JPA for ORM and transactional operations**
- **OAuth2/JWT security with Spring Security & Keycloak**
- **Keycloak Admin REST client for user & role management**
- **REST controllers, DTO validation, global exception handling**
- **Docker containerization (Dockerfile + Docker Compose) for the application and Keycloak**
- **Flyway automatic database migrations**
- **HTML sanitization (OWASP Java HTML Sanitizer) before storing message content**
- **Testcontainers (Keycloak + PostgreSQL integration tests)** 


## API (short overview)
All endpoints require `Authorization: Bearer <JWT>`. Roles: `USER` / `ADMIN`.

- `POST /api/v1/messages` – send a message  
  (recipient: 3–10 letters, content: 1–256 chars; HTML sanitized)
- `GET /api/v1/messages` – fetch your own messages (paginated)
- `PATCH /api/v1/messages/{id}/read` – mark as read (only recipient can)
- `GET /api/v1/messages/search?phrase=...` – full-text search in your messages (≥2 chars)

**Business rules examples:**
- Cannot message yourself → `400`
- Recipient must exist in Keycloak
- Mailbox limit (`mailbox.limit`) enforced → `409` if exceeded


## Quick start (local)
1. Run **Keycloak** and **PostgreSQL** (e.g. via Docker Compose) 
   Provide Keycloak admin client credentials:  
   `keycloak.base-url`, `keycloak.realm`, `keycloak.admin-client-id`, `keycloak.admin-client-secret`.
2. Set `SPRING_PROFILES_ACTIVE=dev` if you want Flyway to drop & recreate schema (dev only).
3. Start the app:  
   ```bash
   ./mvnw spring-boot:run

