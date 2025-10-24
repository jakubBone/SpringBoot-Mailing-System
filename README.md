# ✉️ Spring Boot Mailing System

![](src/main/resources/image/logo.png)

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg)](https://www.docker.com/)


A RESTful messaging API demonstrating proficiency in **Spring Boot ecosystem**, 
**OAuth2 authentication**, **database management**, and **modern DevOps practices**.

## 🎯 Project Purpose

This project showcases hands-on experience with core enterprise Java technologies

## 📊 Technical Highlights

### What This Project Demonstrates

**Spring Framework:**
- Dependency injection and IoC
- Layered architecture (Controller/Service/Repository)
- Spring Security OAuth2 Resource Server
- Spring Data JPA with custom queries

**Database:**
- JPA/Hibernate ORM
- Flyway migrations
- PostgreSQL full-text search
- Database indexing for performance

**Security:**
- External identity provider integration
- OAuth2/JWT implementation
- Role-based access control
- Input validation and sanitization

**Testing:**
- Integration tests with Testcontainers
- Unit tests with mocks

**DevOps:**
- Docker multi-container setup
- Environment-based configuration
- CI/CD with GitHub Actions

## 🏗 Architecture
```
┌──────────────┐      ┌────────────────┐      ┌─────────────┐
│   Swagger    │─────▶│  Spring Boot   │── ──▶│ PostgreSQL  │
│      UI      │      │  Application   │      │             │
└──────────────┘      └────────────────┘      └─────────────┘
                             │
                             │ OAuth2/JWT
                             ▼
                      ┌────────────────┐
                      │    Keycloak    │
                      │   (External    │
                      │   Identity     │
                      │   Provider)    │
                      └────────────────┘
```

### 🏗 Architectural Decisions

**Layered Architecture:**
```
Controller → Service → Repository → Database
```

**Why Keycloak instead of custom JWT implementation?**
- Production-ready identity management solution
- Industry-standard OAuth2/OpenID Connect
- Demonstrates ability to integrate external services

**Why no user table in the database?**
- Users are managed entirely by Keycloak

**Profile-based configuration:**
- `application.properties` - Base configuration
- `application-dev.properties` - Development (debug logs, Flyway clean enabled)
- `application-prod.properties` - Production (restricted endpoints, Flyway clean disabled)
- `application-test.properties` - Testing (Testcontainers, in-memory setup)

## ✨ Key Features

- **User Management:** Registration via Keycloak, OAuth2 authentication
- **Messaging:** Send/receive messages, automatic read tracking
- **Search:** PostgreSQL full-text search 
- **Security:** JWT tokens, role-based access (USER, ADMIN)
- **Validation:** Bean Validation, XSS protection
- **Error Handling:** Global exception handler with structured responses
- **Monitoring:** Spring Actuator endpoints
- **Documentation:** Interactive Swagger UI

## 🚀 Quick Start

### Prerequisites

- Docker
- Docker Compose

### Setup

1. **Clone the repository**
```
git clone https://github.com/jakubBone/SpringBoot-Mailing-System.git
cd spring-boot-mailing-system
```

2. **Configure environment variables**

Create a `.env` file in the project root:
```env
# ----- Profile (dev/prod)
SPRING_PROFILES_ACTIVE=dev

# ----- PostgreSQL
POSTGRES_DB=spring_db
POSTGRES_USER=spring_user
POSTGRES_PASSWORD=spring123

# ----- Keycloak
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin
KEYCLOAK_REALM=mailingsystem
KEYCLOAK_ADMIN_CLIENT_ID=springboot-mailing-system
KEYCLOAK_ADMIN_CLIENT_SECRET=0HMuCpZQvov7QUl7jVASrBC9AW8nkJFZ

KEYCLOAK_BASE_URL=http://localhost:8180
OAUTH2_ISSUER_URI=http://localhost:8180/realms/mailingsystem
```

**⚠️ Important:**
- The project includes `mailingsystem-realm.json` with pre-configured realm settings
- For testing purposes, the provided client secret is: `0HMuCpZQvov7QUl7jVASrBC9AW8nkJFZ`
- `test-realm.json` is used by integration tests only

3. **Start the application**
```
docker-compose up --build
```

Wait for initialization (~60 seconds).

## 🌐 Access Points

| Service | URL | Notes |
|---------|-----|-------|
| **API Documentation** | http://localhost:8080/swagger-ui.html | Complete testing guide |
| REST API | http://localhost:8080/api/v1 | Base endpoint |
| Health Check | http://localhost:8080/actuator/health | Monitoring |
| Keycloak Admin | http://localhost:8180/admin | admin / admin |


## 📚 API Overview
```bash
# Authentication (Public)
POST /api/v1/auth/register  # Register new user
POST /api/v1/auth/login     # Get JWT token

# Messages (Protected - USER/ADMIN)
POST   /api/v1/messages              # Send message
GET    /api/v1/messages              # Read messages (paginated)
PATCH  /api/v1/messages/{id}/read    # Mark as read
GET    /api/v1/messages/search       # Full-text search

# Monitoring
GET /actuator/health        # Health status (public)
GET /actuator/info          # App info (public)
GET /actuator/**            # Metrics (admin only)
```

**📖 Full testing guide:** http://localhost:8080/swagger-ui.html

## 🔐 Security Implementation

### OAuth2 Flow

1. User registers via `/api/v1/auth/register`
2. User authenticates through Keycloak (OAuth2 Password Grant)
3. Keycloak issues JWT token (5-minute expiration)
4. Token included in Authorization header: `Bearer <token>`
5. Spring Security validates token on each request
6. Access granted based on role (USER, ADMIN)

### Security Features

- **OAuth2 Resource Server** - JWT token validation
- **Keycloak Integration** - External identity provider
- **Role-Based Access Control** - Method-level security
- **Global Exception Handler** - Consistent error responses
- **Input Validation** - Bean Validation (@Valid)
- **XSS Protection** - HTML sanitization (OWASP Java HTML Sanitizer)
- **SQL Injection Prevention** - JPA/Hibernate parameterized queries

### Flyway Migrations
```
src/main/resources/db/migration/postgresql/
├── V1__create_messages.sql        # Initial schema
└── V2__add_full_text_search.sql   # FTS support
```

Migrations run automatically on startup.

## 🧪 Testing

### Running Tests
```
mvn test
```

### Testcontainers

Integration tests use **Testcontainers** for:
- PostgreSQL (real database)
- Keycloak (real OAuth2 provider)


## ✅ Continuous Integration
This project uses **GitHub Actions**. 
Every push triggers automated build, test, and integration checks.

## 📁 Project Structure
```
spring-boot-mailing-system/
├── src/
│   ├── main/
│   │   ├── java/com/jakubbone/
│   │   │   ├── config/              # Security, Keycloak, OpenAPI
│   │   │   ├── controller/          # REST endpoints
│   │   │   ├── service/             # Business logic
│   │   │   ├── repository/          # JPA repositories
│   │   │   ├── model/               # Entities
│   │   │   ├── dto/                 # Request/Response objects
│   │   │   ├── exception/           # Global exception handler
│   │   │   └── utils/               # Helpers (JWT converter)
│   │   └── resources/
│   │       ├── application.properties           # Base config
│   │       ├── application-dev.properties       # Development
│   │       ├── application-prod.properties      # Production
│   │       ├── application-test.properties      # Testing
│   │       └── db/migration/postgresql/         # Flyway migrations
│   └── test/
│       ├── java/                    # JUnit tests
│       └── resources/
│           └── test-realm.json      # Keycloak test config
├── mailingsystem-realm.json         # Keycloak realm (dev/prod)
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── .env                             # Environment variables
└── README.md
```



## 📄 License

This project is for educational and portfolio purposes.

## 👤 Author

**Jakub Bone**

- GitHub: [jakubBone](https://github.com/jakubbone)
- LinkedIn: [Jakub Bone](https://linkedin.com/in/jakubbone)
- Email: jakub.bone1990@gmail.com

