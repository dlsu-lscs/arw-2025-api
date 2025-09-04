# ARW 2025 API

This is the backend API for the Annual Recruitment Week (ARW) 2025 of De La Salle University.

---

## Running the Application

### Prerequisites
- Java 21
- Docker & Docker Compose

### Running in Development

1.  **Start the Database:**
    The project uses a MySQL database running in a Docker container for development. Start it using Docker Compose:
    ```bash
    docker-compose up -d
    ```

2.  **Run the Application:**
    Use the Maven wrapper to run the Spring Boot application. This will use the default `application.properties` profile, which is configured to connect to the local Docker database and load the mock data from `src/main/resources/db/migration/dev`.
    ```bash
    ./mvnw spring-boot:run
    ```
    The API will be available at `http://localhost:8080`.

### Running in Production

1.  **Configure Production Properties:**
    Open `src/main/resources/application-prod.properties` and replace the placeholder values with your actual production database credentials and Google OAuth2 client secrets.

    **IMPORTANT:** It is strongly recommended to use environment variables for secrets instead of hardcoding them in the file. Spring Boot will automatically pick them up. You can set them like this:
    ```bash
    export SPRING_DATASOURCE_URL=jdbc:mysql://your-prod-db-host:3306/your-prod-db
    export SPRING_DATASOURCE_USERNAME=your-prod-user
    export SPRING_DATASOURCE_PASSWORD=your-prod-password
    export SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID=your-google-client-id
    export SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET=your-google-client-secret
    ```

2.  **Build the Application JAR:**
    Package the application into an executable JAR file. This command also runs tests to ensure code quality.
    ```bash
    ./mvnw clean install
    ```

3.  **Run the Production JAR:**
    Run the application using the JAR file from the `target/` directory. The key is to activate the `prod` profile, which will prevent the mock data from being loaded.
    ```bash
    java -jar -Dspring.profiles.active=prod target/arw-2025-api-*.jar
    ```

---

## DB Model

- `refresh_tokens`
- `org_pubs`
- `orgs`
- `users`
- `colleges`
- `clusters` (like categories to classify orgs)

## **Production API Readiness Checklist**

- [x] Use DTOs for requests and responses
- [ ] Validate all incoming data (use `@Valid`, custom validators)
- [x] Implement global error handling (`@ControllerAdvice`)
- [x] Secure endpoints (authentication, authorization)
- [ ] Document APIs (OpenAPI/Swagger)
- [ ] Write unit and integration tests
- [ ] Log requests, responses, and errors
- [ ] Monitor application health (Actuator, metrics)
- [x] Handle pagination for large lists
- [x] Set proper HTTP status codes
- [x] Use environment variables for secrets/config
- [ ] Handle CORS if needed
- [ ] Version your API if public (or better Change Management -> don't implement breaking changes for future changes)
- [ ] Rate limiting/throttling (if needed)
- [ ] Graceful shutdown and resource cleanup

