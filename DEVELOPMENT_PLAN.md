# Production-Ready Spring Boot API: Development Plan

This document outlines a step-by-step plan to build the ARW 2025 API, focusing on production-readiness, security, and best practices.

---

## Phase 0: Database Setup & Configuration

Goal: Configure the connection to the MySQL database and establish a clear process for schema management.

1.  **Database Container (Docker):**
    - The project uses a MySQL database running inside a Docker container, defined in the `compose.yaml` file.
    - The database is started by running `docker-compose up -d`. This ensures a consistent and isolated database environment for development.

2.  **Spring Boot Datasource Configuration:**
    - The connection details are configured in `src/main/resources/application.properties`.
    - Key properties include:
        - `spring.datasource.url`: The JDBC connection string for the MySQL database.
        - `spring.datasource.username`: The database username.
        - `spring.datasource.password`: The database password.
    - Spring Boot automatically uses these properties to create a `DataSource` bean, which is used by Spring Data JPA.

3.  **Schema Management (Flyway):**
    - This project uses **Flyway** to manage all database schema changes.
    - On application startup, Flyway automatically checks for new SQL migration scripts in the `src/main/resources/db/migration/` directory.
    - Migration files **must** follow the naming convention `V<VERSION>__<DESCRIPTION>.sql` (e.g., `V2__Add_new_table.sql`).
    - This approach ensures that schema changes are version-controlled, repeatable, and automated. The initial schema is defined in `V1__Initial_Schema.sql`.

---

## Phase 1: Project Structure & Core Domain

Goal: Establish a clean, domain-driven project structure and create the core JPA entities and repositories.

1.  **Create Domain Packages:** Based on your schema, create the following package structure inside `src/main/java/org/dlsulscs/arw/`:
    - `cluster`
    - `college`
    - `organization`
    - `publication`
    - `user`
    - `auth` (for authentication-specific logic)
    - `config` (for configuration files like `SecurityConfig`)
    - `common` (for shared utilities, exceptions, etc.)

2.  **Implement JPA Entities (Models):**
    - Inside each domain package, create a `model` sub-package (e.g., `organization/model`).
    - Create the JPA entity classes corresponding to the database schema (`Cluster.java`, `College.java`, `Organization.java`, `Publication.java`, `User.java`).
    - Use Lombok annotations (`@Data`, `@Entity`, etc.) and JPA annotations (`@Id`, `@GeneratedValue`, `@ManyToOne`, etc.) to map the entities to the tables.

3.  **Create JPA Repositories:**
    - Inside each domain package, create a `repository` sub-package (e.g., `organization/repository`).
    - Create a repository interface for each entity that extends `JpaRepository` (e.g., `OrganizationRepository extends JpaRepository<Organization, Integer>`).

4.  **Target Project Structure:**
    Once complete, the project structure will look like this, promoting scalability and clear separation of concerns within each feature:

    '''
    src/main/java/org/dlsulscs/arw/
    ├── auth/
    │   ├── controller/
    │   │   └── AuthController.java
    │   ├── service/
    │   │   └── JwtService.java
    │   └── ... (DTOs, etc.)
    ├── cluster/
    │   ├── controller/
    │   │   └── ClusterController.java
    │   ├── model/
    │   │   └── Cluster.java
    │   ├── repository/
    │   │   └── ClusterRepository.java
    │   └── service/
    │       └── ClusterService.java
    ├── common/
    │   └── exception/
    │       └── GlobalExceptionHandler.java
    ├── config/
    │   └── SecurityConfig.java
    ├── organization/
    │   ├── controller/
    │   │   └── OrganizationController.java
    │   ├── model/
    │   │   └── Organization.java
    │   ├── repository/
    │   │   └── OrganizationRepository.java
    │   └── service/
    │       └── OrganizationService.java
    ├── ... (other domains like college, publication, user)
    └── Arw2025ApiApplication.java
    '''

---

## Phase 2: Authentication (OAuth2 + JWTs + Refresh Tokens)

Goal: Implement a secure, stateless authentication flow using a short-lived JWT access token and a long-lived refresh token, both transported via secure, http-only cookies.

1.  **JWT Service:**
    - Create a `JwtService` in the `auth` package to handle the creation and validation of JWTs.

2.  **Custom OAuth2 Success Handler:**
    - This component is triggered after a successful Google login.
    - Its job is to:
        a. Fetch user details from Google.
        b. Use a `UserService` to find or create a user in your database (just-in-time provisioning).
        c. Generate a short-lived JWT **access token** (e.g., 15-30 minutes).
        d. Generate a long-lived **refresh token** (e.g., 7 days) and store a record of it in the database for security.
        e. Create secure, http-only cookies for both tokens and add them to the response.
        f. Redirect the user to your frontend application.

3.  **JWT Authentication Filter:**
    - This custom filter runs on every request.
    - It reads the `access_token` cookie, validates the JWT, and if valid, sets the user's authentication context in Spring Security.
    - If the access token is expired, it rejects the request, leading the frontend to use the refresh token.

4.  **Create Auth Controller (`/api/auth/...`):**
    - **`/refresh` Endpoint:** This endpoint will be called by the frontend when the access token expires. It expects the `refresh_token` cookie. It validates the refresh token against the database, and if valid, issues a new access token cookie.
    - **`/logout` Endpoint:** This endpoint clears the `access_token` and `refresh_token` cookies by re-setting them with a `Max-Age` of 0. It should also invalidate the refresh token in the database so it cannot be used again.

5.  **Update Security Configuration:**
    - In `SecurityConfig.java`, configure the full filter chain: OAuth2 login, the JWT Authentication Filter, and public/protected routes.
    - Public routes will include `/`, `/oauth2/**`, and `/api/auth/refresh`.
    - All other API routes (e.g., `/api/orgs/**`) will be protected.

---

## Phase 3: CRUD Operations

Goal: Expose the core business logic through a well-defined service layer and REST controllers.

1.  **Create Service Layer:**
    - Inside each domain package, create a `service` sub-package (e.g., `organization/service`).
    - Create service classes (e.g., `OrganizationService`) that use dependency injection (`@Autowired`) to get the required repositories.
    - Implement the core business logic here (e.g., `createOrganization`, `getOrganizationById`).

2.  **Create Controllers (Endpoints):**
    - Inside each domain package, create a `controller` sub-package (e.g., `organization/controller`).
    - Create REST controllers (e.g., `OrganizationController`) that expose the CRUD endpoints (`@GetMapping`, `@PostMapping`, etc.).
    - Controllers should call the service layer to perform actions. They should not contain business logic.

---

## Phase 4: Production-Ready Enhancements

Goal: Add features that are essential for a production environment.

1.  **Security - CORS:**
    - In `SecurityConfig`, configure CORS (Cross-Origin Resource Sharing) to allow requests from your Next.js frontend's domain.

2.  **API Documentation - OpenAPI:**
    - Add the `springdoc-openapi-starter-webmvc-ui` dependency to your `pom.xml`.
    - This will automatically generate OpenAPI 3 documentation for your API.
    - Access it at `/swagger-ui.html`.
    - Use annotations (`@Operation`, `@ApiResponse`) on your controller methods for more detailed documentation.

3.  **Observability - Logging & Metrics:**
    - **Logging:** Your `application.properties` already has good logging levels configured. Ensure you use a structured logging framework like SLF4J throughout the application.
    - **Metrics:** Add the `spring-boot-starter-actuator` dependency. This exposes production-ready endpoints (`/actuator/health`, `/actuator/metrics`, `/actuator/prometheus`). These are critical for monitoring the application's health and performance.

4.  **Configuration Management:**
    - Create a separate profile for production: `application-prod.properties`.
    - This file will override settings from `application.properties` and should contain the actual production database credentials and Google client secrets. **Never commit secrets to Git.** Use environment variables or a secret management system in production.

5.  **Global Exception Handling:**
    - Create a class annotated with `@ControllerAdvice` to handle exceptions globally. This ensures that if an error occurs (e.g., resource not found), the API returns a consistent and well-formatted JSON error response.

---

## Phase 5: Developer Workflow

Goal: Document the development process for current and future developers.

1.  **Database Migrations (Flyway):**
    - Flyway runs automatically on application startup.
    - It checks for new migration scripts in `src/main/resources/db/migration` that haven't been run and executes them.
    - **There are no manual commands needed during development.**

2.  **Typical Feature Workflow (e.g., Adding a New Feature to Orgs):**
    - **Step 1 (Model/DB):** If the database schema needs to change, create a new Flyway migration script first (e.g., `V2__Add_is_active_to_orgs.sql`). Then, update the JPA entity (`Organization.java`).
    - **Step 2 (Repository):** If you need a custom query, add a new method to the `OrganizationRepository`.
    - **Step 3 (Service):** Implement the new business logic in `OrganizationService`.
    - **Step 4 (Controller):** Expose the new feature via a new endpoint in `OrganizationController`.
    - **Step 5 (Test):** Write unit or integration tests for the new feature.

This workflow is often called "bottom-up" (from the database layer up to the controller layer) and is a very common and effective way to develop Spring Boot applications.

## Essential Endpoints

- `/api/orgs`
  - get all orgs -> randomized on client-side

- get all orgs by a specific cluster (category) -> also randomized on client-side
  

- `/api/orgs/search?q=someOrg`
  - search orgs -> can search by `name`, `cluster.name`, or `short_name`
