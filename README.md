# ARW 2025 API

This is the backend API for the Annual Recruitment Week (ARW) 2025 of De La Salle University.

---

## Running the Application

### Prerequisites

- Java 21
- Docker & Docker Compose

### Environments & Configuration

This project uses Spring Profiles to manage environment-specific configurations. The configuration for each environment is defined in a corresponding `application-{profile}.properties` file.

-   **`local`**: For local development on `localhost`. Contains settings for local database connections, mock data, relaxed cookie settings, and local CORS origins.
-   **`dev`**: For the hosted development environment (`arw-dev.api.dlsu-lscs.org`).
-   **`prod`**: For the production environment.

To activate a specific profile, set the `SPRING_PROFILES_ACTIVE` environment variable. For example: `export SPRING_PROFILES_ACTIVE=dev`.

When deploying with Docker or to a service like Coolify, you set this environment variable in your service configuration. All other environment-specific properties (like cookie domains, CORS origins, etc.) are loaded from the profile-specific properties file. You only need to provide secrets (like database credentials and API keys) as environment variables.

### Running for Local Development

1.  **Start the Database & API:**
    The project uses a PostgreSQL database and API running in a Docker container for development. Start it using Docker Compose:
    ```bash
    docker-compose up -d
    ```

2.  **Or run the Application with the `local` profile (standalone without database):**
    -  you can edit `SPRING_DATASOURCE_URL` in `application-local.properties` file for your database URL.
    To run the application and load the mock data, you must activate the `local` profile.
    ```bash
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
    ```
    The API will be available at `http://localhost:8080`.

### Building for an Environment (dev/prod)

1.  **Build the Application JAR:**
    Package the application into an executable JAR file.
    ```bash
    ./mvnw clean install
    ```
    The resulting JAR file will be in the `target/` directory.

2.  **Run the JAR with the correct profile:**
    Run the application, providing all secrets as environment variables and activating the desired profile.
    ```bash
    # Example for 'dev' environment
    export SPRING_PROFILES_ACTIVE=dev
    export SPRING_DATASOURCE_URL=...
    export SPRING_DATASOURCE_USERNAME=...
    export SPRING_DATASOURCE_PASSWORD=...
    export GOOGLE_CLIENT_ID=...
    export GOOGLE_CLIENT_SECRET=...
    export JWT_SECRET=...

    java -jar target/arw-2025-api-*.jar
    ```

### Running with Docker

1.  **Build the Docker Image:**
    ```bash
    ./mvnw clean install
    docker build -t arw-2025-api:latest .
    ```

2.  **Run the Docker Container:**
    Run the container, passing secrets and the active profile as environment variables.
    ```bash
    # Example for 'dev' environment
    docker run -p 8080:8080 \
      -e SPRING_PROFILES_ACTIVE=dev \
      -e SPRING_DATASOURCE_URL=... \
      -e SPRING_DATASOURCE_USERNAME=... \
      -e SPRING_DATASOURCE_PASSWORD=... \
      -e GOOGLE_CLIENT_ID=... \
      -e GOOGLE_CLIENT_SECRET=... \
      -e JWT_SECRET=... \
      arw-2025-api:latest
    ```

---

## Local API Testing Guide (with Postman)

Testing the API locally requires handling the cookie-based authentication flow. Because the API uses secure, `HttpOnly` cookies, you cannot simply copy and paste a token into an `Authorization` header. Instead, you must first use a browser to get the cookies and then configure Postman to use them.

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

**Step 3: Transfer Cookies from Browser to Postman**

1.  In your browser, open **Developer Tools** (F12).
2.  Go to the **Application** tab (or "Storage" in some browsers).
3.  Under the "Storage" section on the left, expand **Cookies** and select `http://localhost:8080`.
4.  You will see `access_token` and `refresh_token`. For each token, copy the long string from the "Value" column.

**Step 4: Add Cookies to Postman's Cookie Manager**

1.  Open Postman.
2.  In any request tab, click the "Cookies" link (usually below the URL bar). This opens the "MANAGE COOKIES" modal.
3.  Click "Add Cookie".
4.  Enter `localhost` as the Domain.
5.  Add the `access_token`:
    -   **Name:** `access_token`
    -   **Value:** *Paste the value you copied from the browser.*
6.  Add the `refresh_token`:
    -   **Name:** `refresh_token`
    -   **Value:** *Paste the value you copied from the browser.*
7.  Click "Save". Postman will now automatically send these cookies with requests to `localhost`.

**Step 5: Make Authenticated Requests**

Now you're ready to test.

1.  Create a new request in Postman, e.g., `GET http://localhost:8080/api/orgs`.
2.  **Do NOT add an `Authorization` header.** Postman will now automatically send the cookies with every request to `localhost`, authenticating you successfully.
3.  **Testing Token Refresh (Reactive Approach):**
    *   Make an authenticated request (e.g., `GET http://localhost:8080/api/orgs`). This should succeed initially.
    *   Wait for the `access_token` to expire (15 minutes).
    *   Make another authenticated request (e.g., `GET http://localhost:8080/api/orgs`). This request should now fail with a `401 Unauthorized` or `403 Forbidden` status.
    *   **Immediately after the failed request**, send a `POST` request to `http://localhost:8080/api/auth/refresh`. Postman will automatically send the `refresh_token` cookie.
    *   If the refresh is successful, a new `access_token` cookie will be set in Postman's cookie manager. You can then retry the original `GET /api/orgs` request, and it should now succeed.
    *   If the refresh fails (e.g., refresh token expired after 7 days), you'll need to re-authenticate via the browser.

---

## Production Environment Variables

When running the application in production (either as a JAR or a Docker container), you must override the default development values by setting the following environment variables.

### Required for Production

| Environment Variable |
| -------------------- | 
| `SPRING_DATASOURCE_URL`       |
| `SPRING_DATASOURCE_USERNAME`  |
| `SPRING_DATASOURCE_PASSWORD`  |
| `GOOGLE_CLIENT_ID`   |
| `GOOGLE_CLIENT_SECRET`|
| `FRONTEND_REDIRECT_URI`|
| `JWT_SECRET`         |

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
- [x] Document APIs (OpenAPI/Swagger)
- [ ] Write unit and integration tests
- [x] Log requests, responses, and errors
- [x] Monitor application health (Actuator, metrics)
- [x] Handle pagination for large lists
- [x] Set proper HTTP status codes
- [x] Use environment variables for secrets/config
- [ ] Handle CORS if needed
- [x] Version your API if public (or better Change Management -> don't implement breaking changes for future changes)
- [ ] Rate limiting/throttling (if needed)
- [ ] Graceful shutdown and resource cleanup
