# Gemini Project Context: arw-2025-api

## Project Overview

This is the backend API for ARW 2025, a Java and Spring Boot application.

- **Core Technologies:** Java 21, Spring Boot 3.5.5, Maven
- **Database:** MySQL, managed with Docker Compose and Flyway for migrations.
- **API:** Provides RESTful endpoints.
- **Authentication:** Integrated with Google OAuth2 for user authentication.
- **Dependencies:** Spring Web, Spring Data JPA, Spring Security, MySQL Connector, Lombok.

## Building and Running

### Prerequisites

- Java 21
- Docker and Docker Compose

### 1. Setup

- **Google OAuth2 Credentials:** Open `src/main/resources/application.properties` and replace `YOUR_GOOGLE_CLIENT_ID` and `YOUR_GOOGLE_CLIENT_SECRET` with your actual Google API credentials.

- **Start the Database:** Run the following command to start the MySQL database in the background.
  ```bash
  docker-compose up -d
  ```

### 2. Running the Application

Once the database is running, you can start the Spring Boot application using the Maven wrapper:

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

### 3. Building the Project

To build the project into a JAR file, run:

```bash
./mvnw clean install
```

The resulting JAR file will be in the `target/` directory.

### 4. Running Tests

To run the test suite, use:

```bash
./mvnw test
```

## Development Conventions

### Database Migrations

- This project uses **Flyway** to manage database schema changes.
- All new SQL schema changes (e.g., `CREATE TABLE`, `ALTER TABLE`) **must** be placed in a new file in the `src/main/resources/db/migration/` directory.
- Migration files must follow the naming convention `V<VERSION>__<DESCRIPTION>.sql` (e.g., `V2__Add_new_table.sql`).
