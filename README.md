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

## Local API Testing Guide (with Insomnia)

Testing the API locally requires handling the cookie-based authentication flow. Because the API uses secure, `HttpOnly` cookies, you cannot simply copy and paste a token into an `Authorization` header. Instead, you must first use a browser to get the cookies and then import them into Insomnia's cookie jar.

**Step 1: Start the Application**

1.  Make sure the database is running: `docker-compose up -d`
2.  Run the application with the `dev` profile to load mock data:
    ```bash
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
    ```

**Step 2: Authenticate Using Your Browser**

This step is required to interact with Google's login page and get the cookies.

1.  Open your web browser (Chrome, Firefox, etc.).
2.  Navigate to the login endpoint: `http://localhost:8080/oauth2/authorization/google`
3.  Log in with your Google account.
4.  After a successful login, the server will set the cookies in your browser and redirect you to the frontend URL (`http://localhost:3000` by default). The page may not load if you don't have a frontend running, but that's okay. The cookies are now set in your browser.

**Step 3: Transfer Cookies from Browser to Insomnia**

1.  In your browser, open **Developer Tools** (F12).
2.  Go to the **Application** tab.
3.  Under the "Storage" section on the left, expand **Cookies** and select `http://localhost:8080`.
4.  You will see `access_token` and `refresh_token`. For each token, copy the long string from the "Value" column.

**Step 4: Add Cookies to Insomnia's Cookie Jar**

1.  In Insomnia, open the **Cookies** manager for your workspace.
2.  Click **"Add Cookie"** and add the `access_token`:
    - **Name:** `access_token`
    - **Value:** *Paste the value you copied.*
    - **Domain:** `localhost`
3.  Do the same for the `refresh_token`.

**Step 5: Make Authenticated Requests**

Now you're ready to test.

1.  Create a new request, e.g., `GET http://localhost:8080/api/orgs`.
2.  **Do NOT add an `Authorization` header.** Insomnia will now automatically send the cookies with every request to `localhost`, authenticating you successfully.
3.  To test token refresh, wait 15 minutes for the access token to expire, then send a `POST` request to `http://localhost:8080/api/auth/refresh`. This will fail if your refresh token is also expired (it lasts 7 days).

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

