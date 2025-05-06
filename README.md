# ✉️ Spring Boot Mailing System

This project is currently being developed to strengthen my skills in Spring Boot, Hibernate, RESTful API design, and dependency injection. 
It demonstrates building a RESTful web application simulating an email system, utilizing Spring Boot, managing databases with Flyway, 
and implementing user authentication using JWT. The application leverages PostgreSQL for production environments and H2 for testing.


## 📖 Features

- **User Management**: Creating users with role-based access (USER, ADMIN)
- **JWT Authentication**: Secure login with token generation
- **Role-Based Access Control**: Filters enforce ADMIN-only access where needed
- **Message Sending System**: Sending a message from the logged-in user to another users
- **RESTful Endpoints**: For login, message sending, etc.
- **Database Integration**: PostgreSQL for production, H2 for testing
- **Automatic Database Migrations**: Managing database schema changes using Flyway
- **Environment Configuration**: Utilization of .env file

## 🚀 Technologies & Libraries Used

- Java 21
- Spring Boot 3.4.4.
- Spring MVC (Web)
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL & H2 Database
- JWT
- Flyway
- Spring Dotenv
- Lombok
- BCrypt (password encryption)
- Docker & Docker Compose
- Maven

## 🧪 Testing Stack
- JUnit 5
- Spring Boot Test 
- MockMvc 


## 📂 Project Structure

```
.
├── src
│   ├── main
│   │   ├── java/com/jakubbone
│   │   │   ├── config         # Flyway and security configuration
│   │   │   ├── controller     # REST API controllers
│   │   │   ├── dto            # Data transfer objects
│   │   │   ├── exception      # Global exception handling
│   │   │   ├── model          # JPA entity models
│   │   │   ├── repository     # Spring Data JPA repositories
│   │   │   ├── service        # Business logic and service interfaces
│   │   │   └── utils          # JWT token provider and filter
│   │   └── resources          
│   │       ├── db/migration   # Flyway migrations scripts (PostgreSQL and H2)
│   │       ├── application.properties
│   │       └── application-test.properties
│   └── test                    # Unit and integration tests
├── Dockerfile                  # Builds application Docker image
├── docker-compose.yml          # Container orchestration
├── pom.xml                     # Maven dependency management
└── .env                        # Secrets and environment variables
```

## 🛠️ Environment Configuration

The application uses an `.env` file for storing sensitive data, e.g.:

```bash
SPRING_DATASOURCE_USERNAME=spring_user
SPRING_DATASOURCE_PASSWORD=spring123
JWT_SECRET=secret_jwt_key
```

### 🐳 Running the Application

**Production environment (PostgreSQL with Docker):**

```bash
docker-compose up -d
mvn spring-boot:run
```

**Testing environment (H2):**

```bash
mvn test
```

### 🗃️ Database Management with Flyway

Database migrations are automatically applied at application startup. Flyway configuration enabling "clean migrate":

```java
@Bean
public FlywayMigrationStrategy cleanMigrateStrategy() {
    return flyway -> {
        flyway.clean();   // removes existing schema
        flyway.migrate(); // applies migrations (V1, V2...)
    };
}
```


## 🔑 Security and Authentication

The application uses JWT and Spring Security:

- Passwords are encrypted using BCrypt
- JWT tokens are generated upon successful login and secure REST endpoint access


## ✅ Automated Testing

The project includes integration and unit tests using JUnit:

- Uses H2 database
- Validates login functionality, token generation, and endpoint responses

Example to run tests:

```bash
mvn clean test
```

## 📈 Application Endpoints

- `/api/login` – Authenticates user and returns a JWT token
- `/api/messages` – Sends a message from the logged-in user to another users
- `/api/info` – Returns the current application version
- `/api/uptime` – Returns application uptime in seconds


## 📦 Building with Docker

Build nad run the application:

```bash
docker-compose up --build
```