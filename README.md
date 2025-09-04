# ARW 2025 API

This is the backend API for the Annual Recruitment Week (ARW) 2025 of De La Salle University.

---

## Running the Application

### Prerequisites
- Java 21
- Docker & Docker Compose

### Running in Development (with Mock Data)

1.  **Start the Database:**
    The project uses a MySQL database running in a Docker container for development. Start it using Docker Compose:
    ```bash
    docker-compose up -d
    ```

2.  **Run the Application with the `dev` profile:**
    To run the application and load the mock data, you must activate the `dev` profile. This tells Flyway to run the scripts in `src/main/resources/db/migration/dev`.
    ```bash
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
    ```
    The API will be available at `http://localhost:8080`.

### Running in Production (or without Mock Data)

By default (without an active profile), the application runs in a production-like mode where mock data is **not** loaded.

**Method 1: Running the JAR File**

1.  **Build the Application JAR:**
    Package the application into an executable JAR file.
    ```bash
    ./mvnw clean install
    ```

2.  **Run the Production JAR:**
    Run the application, providing all secrets as environment variables. **Do not** activate any profile to ensure a clean, production-ready database state.
    ```bash
    export SPRING_DATASOURCE_URL=jdbc:mysql://your-prod-db-host:3306/your-prod-db
    export SPRING_DATASOURCE_USERNAME=your-prod-user
    export SPRING_DATASOURCE_PASSWORD=your-prod-password
    export SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID=your-google-client-id
    export SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET=your-google-client-secret

    java -jar target/arw-2025-api-*.jar
    ```

**Method 2: Running as a Docker Container**

1.  **Build the Docker Image:**
    ```bash
    ./mvnw clean install
    docker build -t arw-2025-api:latest .
    ```

2.  **Run the Docker Container:**
    Run the container, passing all secrets and configuration as environment variables. See the **Production Environment Variables** section below for a complete list of variables to set.
    ```bash
    docker run -p 8080:8080 \
      -e SPRING_DATASOURCE_URL=jdbc:mysql://your-prod-db-host:3306/your-prod-db \
      -e SPRING_DATASOURCE_USERNAME=your-prod-user \
      -e SPRING_DATASOURCE_PASSWORD=your-prod-password \
      # ... and so on for all required variables
      arw-2025-api:latest
    ```

---

## Local API Testing Guide

Testing the API locally requires handling the cookie-based authentication flow. Because the API uses secure, `HttpOnly` cookies, you cannot simply copy and paste a token into an `Authorization` header. Instead, you must use a tool that can store and send cookies, just like a browser.

Here is the recommended process using a browser and an API client like Postman or Insomnia.

**Step 1: Start the Application**

1.  Make sure the database is running: `docker-compose up -d`
2.  Run the application with the `dev` profile to load mock data:
    ```bash
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
    ```

**Step 2: Authenticate Using Your Browser**

This is the easiest way to get the `access_token` and `refresh_token` cookies.

1.  Open your web browser (Chrome, Firefox, etc.).
2.  Navigate to the login endpoint: `http://localhost:8080/oauth2/authorization/google`
3.  Log in with your Google account.
4.  After a successful login, the server will set the cookies in your browser and redirect you to the frontend URL (`http://localhost:3000` by default). The page may not load if you don't have a frontend running, but that's okay. The cookies are now set.

**Step 3: Make Authenticated Requests with Your API Client**

Now, use an API client like Postman or Insomnia to test your endpoints.

1.  **Enable the Cookie Jar:** Ensure your API client's cookie jar is enabled. In Postman, cookies are managed automatically. In Insomnia, you can find the cookie jar in the top-right corner.
2.  **Make a Request:** Make a request to a protected endpoint, for example:
    `GET http://localhost:8080/api/orgs`
3.  **Do NOT add an `Authorization` header.** Your API client will automatically send the `access_token` cookie it received from the browser session, and the request will be authenticated.

**Step 4: Testing Token Refresh**

1.  The access token is short-lived (15 minutes by default).
2.  After it expires, your requests to protected endpoints will fail.
3.  To get a new access token, make a request to the refresh endpoint:
    `POST http://localhost:8080/api/auth/refresh`
4.  Your API client will automatically send the `refresh_token` cookie. The server will validate it and respond by setting a new `access_token` cookie.
5.  You can now continue making requests to protected endpoints.

---

## Production Environment Variables

When running the application in production (either as a JAR or a Docker container), you must override the default development values by setting the following environment variables.

### Required for Production

| Environment Variable                                                  |
| --------------------------------------------------------------------- | 
| `SPRING_DATASOURCE_URL`                                               |
| `SPRING_DATASOURCE_USERNAME`                                          |
| `SPRING_DATASOURCE_PASSWORD`                                          |
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID`         |
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET`     |
| `APP_OAUTH2_REDIRECT_URI`                                             |
| `APP_JWT_SECRET`                                                      |

### Recommended for Production

| Environment Variable                  | Value      | Description                                                                 |
| ------------------------------------- | ---------- | --------------------------------------------------------------------------- |
| `SPRING_JPA_HIBERNATE_DDL_AUTO`       | `validate` | Ensures the app's code matches the database schema without changing it.     |

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

