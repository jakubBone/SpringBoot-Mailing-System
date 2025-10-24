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

## 🎯 What This Project Demonstrates

**Spring Ecosystem:**
- Spring Boot 3.4 with layered architecture
- Spring Security OAuth2 Resource Server
- Spring Data JPA with Hibernate
- Spring Actuator for monitoring

**Database & Persistence:**
- PostgreSQL full-text search
- Flyway migrations for version control
- JPA/Hibernate ORM

**Security:**
- Keycloak integration (OAuth2/JWT)
- Role-based access control
- XSS protection and input validation
- Global exception handling

**Testing & DevOps:**
- JUnit 5 with Testcontainers
- Docker multi-container setup
- GitHub Actions CI/CD
- Environment-based configuration

## 🏗 Architecture
```
┌──────────────┐      ┌────────────────┐      ┌─────────────┐
│   Swagger    │─────▶│  Spring Boot   │─────▶│ PostgreSQL  │
│      UI      │      │  Application   │      │             │
└──────────────┘      └────────────────┘      └─────────────┘
                             │
                             │ OAuth2/JWT
                             ▼
                      ┌────────────────┐
                      │    Keycloak    │
                      │  (Identity     │
                      │   Provider)    │
                      └────────────────┘
```

**Key Decisions:**
- **Keycloak over custom JWT:** Production-ready, industry-standard OAuth2
- **No user table:** Users managed by Keycloak (single source of truth)
- **Profile-based config:** Separate properties for dev/prod/test environments

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
- Docker & Docker Compose

### Setup

1. **Clone and configure**
```bash
git clone https://github.com/jakubBone/SpringBoot-Mailing-System.git
cd spring-boot-mailing-system
```

2. **Create `.env` file**
```env
SPRING_PROFILES_ACTIVE=dev
POSTGRES_DB=spring_db
POSTGRES_USER=spring_user
POSTGRES_PASSWORD=spring123
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin
KEYCLOAK_REALM=mailingsystem
KEYCLOAK_ADMIN_CLIENT_ID=springboot-mailing-system
KEYCLOAK_ADMIN_CLIENT_SECRET=0HMuCpZQvov7QUl7jVASrBC9AW8nkJFZ
KEYCLOAK_BASE_URL=http://localhost:8180
OAUTH2_ISSUER_URI=http://localhost:8180/realms/mailingsystem
```

**⚠️ Note:** Project includes `mailingsystem-realm.json` with test credentials.

3. **Start the application**
```
docker-compose up --build
```

Wait for initialization (~60 seconds).

## 🌐 Access

| Service | URL |
|---------|-----|
| 📖 **API Documentation** | http://localhost:8080/swagger-ui.html |
| 🔌 **REST API** | http://localhost:8080/api/v1 |
| ❤️ **Health Check** | http://localhost:8080/actuator/health |
| 🔐 **Keycloak Admin** | http://localhost:8180/admin (admin/admin) |


## 📚 API Endpoints
```bash
# Public
POST /api/v1/auth/register  # Register user
POST /api/v1/auth/login     # Get JWT token

# Protected (USER/ADMIN)
POST   /api/v1/messages              # Send message
GET    /api/v1/messages              # Read messages
PATCH  /api/v1/messages/{id}/read    # Mark as read
GET    /api/v1/messages/search       # Full-text search

# Monitoring
GET /actuator/health  # Public
GET /actuator/**      # Admin only
```

**Complete guide with examples:** http://localhost:8080/swagger-ui.html

## 🔐 Security Flow

1. User registers → Keycloak creates account
2. User logs in → Keycloak issues JWT (5-min expiration)
3. Token sent in header: `Bearer <token>`
4. Spring Security validates JWT on each request
5. Access granted based on role (USER/ADMIN)


## 🦆 Flyway Migrations
```
src/main/resources/db/migration/postgresql/
├── V1__create_messages_postgres.sql        # Initial schema
└── V2__fts_messages.sql   # FTS support
```

Migrations run automatically on startup.

## 🧪 Testing
```bash
mvn test  # Runs all tests with Testcontainers
```

**Integration tests** use real PostgreSQL and Keycloak containers via Testcontainers.



## ✅ CI/CD

**GitHub Actions** runs tests on every push to `dev` branch. 
Merges to `master` require passing tests.

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

