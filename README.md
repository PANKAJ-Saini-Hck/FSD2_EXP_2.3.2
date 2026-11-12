# Encryption and Token Lifecycle Management

**Experiment 2.3.2**

## Overview

This Spring Boot project demonstrates a basic authentication-token lifecycle. It issues short-lived JWT access tokens, creates refresh tokens, encrypts refresh tokens before storing them in a MySQL database, validates refresh tokens, and supports logout by revoking the stored refresh token.

## Objectives

- Generate and sign JWT access tokens.
- Create refresh tokens and define their expiry.
- Encrypt refresh-token values before database storage and decrypt them for validation.
- Validate and rotate refresh tokens.
- Revoke a user's stored refresh token during logout.

## Technology Stack

- Java 25 (as configured in the supplied `pom.xml`)
- Spring Boot 3.5.5
- Spring Web
- Spring Security
- Spring Data JPA / Hibernate
- MySQL
- JJWT 0.12.6
- Maven

> **Java compatibility:** The supplied Maven project sets `java.version` to 25. Use JDK 25, or change the project version deliberately to a JDK supported by the dependencies and your environment before building.

## Project Structure

```text
FSD2_EXP_2.3.2/
├── src/main/java/com/example/securitydemo/
│   ├── config/          # Spring Security configuration
│   ├── controller/      # Authentication endpoints
│   ├── dto/              # Login and token response objects
│   ├── entity/           # Refresh-token entity
│   ├── repository/       # Refresh-token repository
│   ├── service/          # JWT and AES services
│   └── SecurityDemoApplication.java
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

## Prerequisites

- JDK 25 (per the current `pom.xml`)
- Maven 3.8 or later
- MySQL Server

## Database Setup

Create the database in MySQL:

```sql
CREATE DATABASE security_demo;
```

Update `src/main/resources/application.properties` with your local database username and password. Do not commit real credentials to Git. Prefer environment variables or an untracked local configuration file.

Example configuration using environment variables:

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/security_demo}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}
```

Set `DB_PASSWORD` in your local environment before starting the application.

## Run the Application

From the project root:

```bash
mvn clean spring-boot:run
```

Or build and run the JAR:

```bash
mvn clean package
java -jar target/securitydemo-0.0.1-SNAPSHOT.jar
```

The application uses port `8080` by default.

## Authentication Endpoints

All endpoints below are under `/auth`.

| Method | Endpoint | Parameters / Body | Purpose |
|---|---|---|---|
| POST | `/auth/login` | JSON body with `username` and `password` | Validate demo credentials and issue access and refresh tokens |
| POST | `/auth/refresh` | Request parameters `username` and `refreshToken` | Validate the refresh token and issue a new token pair |
| POST | `/auth/logout` | Request parameter `username` | Revoke the stored refresh token |

### Example login request

```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

A successful login returns an access token and a refresh token. The current implementation uses these demonstration credentials in `AuthController`; change this before any real use.

### Example refresh request

```http
POST http://localhost:8080/auth/refresh?username=admin&refreshToken=YOUR_REFRESH_TOKEN
```

### Example logout request

```http
POST http://localhost:8080/auth/logout?username=admin
```

## Token Lifecycle (Current Implementation)

- Access token validity: 15 minutes.
- Refresh token validity: 7 days.
- Refresh tokens are encrypted before being persisted.
- A refresh request validates the supplied token and then issues a new access/refresh token pair.
- Logout deletes the stored refresh token for the username.

## Important Security Limitations

This repository is a classroom demonstration, not production-ready authentication. The supplied source currently contains hard-coded demo login credentials and a hard-coded JWT signing secret. Its security configuration also permits all requests, and the login check is not backed by a user database or password hashing. Move secrets to environment variables or a secret manager, use properly hashed user passwords, restrict authorization rules, validate inputs, and review token rotation/revocation behavior before using this design in a real system.

## Tests

Run the available Maven tests with:

```bash
mvn test
```
