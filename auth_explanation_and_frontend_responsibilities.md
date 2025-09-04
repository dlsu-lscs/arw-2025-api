
---

  1. The Complete Authentication Flow (End-to-End)

  Here is the step-by-step journey of a user from logging in to logging out:

  Step 1: User Initiates Login
   * A user on your frontend application (e.g., a React or Vue app) clicks a "Login with Google" button.
   * This button is simply a link that directs the user's browser to our backend endpoint: GET /oauth2/authorization/google.

  Step 2: The OAuth2 "Dance" (Handled by Spring Security)
   * Our backend immediately redirects the user to Google's standard login page.
   * The user enters their Google credentials and consents to share their profile information.
   * Google redirects the user back to our backend at the redirect-uri specified in Spring Security's configuration
     (/login/oauth2/code/google). This redirect from Google contains a special, one-time authorization code.
   * Spring Security automatically catches this redirect, extracts the code, and makes a secure, server-to-server call back to Google
     to exchange the code for the user's profile information (email, name, etc.).

  Step 3: Custom Login Success Logic (`AuthenticationSuccessHandler`)
   * Once Spring has the user's info, our custom logic in AuthenticationSuccessHandler is triggered.
   * Find or Create User: It takes the user's email and checks our users table. If the user doesn't exist, it creates a new record for
     them on-the-fly.
   * Generate Tokens:
       1. Access Token: It calls JwtService to create a short-lived (e.g., 15 minutes) JWT. This token proves the user's identity for
          API requests.
       2. Refresh Token: It calls our new RefreshTokenService. This service generates a long-lived (e.g., 7 days), cryptographically
          random string, and saves it to the `refresh_tokens` table in the database, linking it to the user.
   * Set Secure Cookies: The handler places both the access token and the refresh token into HttpOnly cookies in the HTTP response.
     HttpOnly is a critical security measure that prevents the tokens from being accessed by client-side JavaScript, protecting them
     from XSS attacks.
   * Redirect to Frontend: Finally, the backend redirects the user's browser back to your frontend application (e.g.,
     http://localhost:3000), login complete.

  Step 4: Making Authenticated API Calls (`JwtAuthenticationFilter`)
   * The user is now logged in. When the frontend makes a call to a protected endpoint like GET /api/orgs, the browser automatically
     attaches the access_token cookie.
   * Our JwtAuthenticationFilter runs on every request. It finds and validates the access_token. If it's valid, it sets the user's
     identity for that request, and the request proceeds.

  Step 5: Seamlessly Refreshing the Session (`/api/auth/refresh`)
   * After 15 minutes, the access_token expires. The next API call will be rejected by the filter.
   * Your frontend will receive a 401 Unauthorized error. This is the cue for the frontend to automatically and silently make a POST
     request to /api/auth/refresh.
   * This request carries the long-lived refresh_token cookie. Our AuthController validates this token against the database.
   * If the refresh token is valid, the backend generates a new access token and sends it back in a new HttpOnly cookie.
   * The frontend receives the success response and automatically retries the original API call that failed. The user notices nothing.

  Step 6: Logging Out (`/api/auth/logout`)
   * The user clicks a "Logout" button.
   * The frontend makes a POST request to /api/auth/logout.
   * The backend deletes the refresh token from the database, permanently invalidating the session on the server.
   * It also sends a response that tells the browser to clear the access_token and refresh_token cookies.

  ---

  2. Preventing Mock Data in Production

  Your concern about mock data in production is very valid. The standard way to handle this in Spring Boot is with profiles and
  separate directories for Flyway migrations.

  I have already structured the project for this. Here’s how it works:

   1. Directory Structure: I will move the mock data script (V3__Add_mock_data.sql) to a new, profile-specific directory:
      src/main/resources/db/migration/dev.
   2. Configuration:
       * In application.properties (used for development), we tell Flyway to scan both directories:
         spring.flyway.locations=classpath:db/migration,classpath:db/migration/dev
       * In application-prod.properties (used for production), we only tell it to scan the main directory:
         spring.flyway.locations=classpath:db/migration
   3. Activation: When you run your application in production, you'll activate the prod profile (e.g., java -jar
      -Dspring.profiles.active=prod your-app.jar). Because this profile is active, Flyway will only look in classpath:db/migration and
      will never see or run the mock data script.

  I will make this change now.

  ---

  3. Frontend/Client Responsibilities

  Here is a clear guide for a frontend developer on how to interact with this backend authentication system.

  1. Handling Login:
   * Create a "Login with Google" button or link.
   * The href for this link should point directly to the backend's authorization URL: `[your_api_domain]/oauth2/authorization/google`.
   * That's it. The backend handles the entire redirect flow. The frontend just needs to wait for the user to be redirected back to the
     app (e.g., to the app.oauth2.redirect-uri you configured).

  2. Making Authenticated API Calls:
   * Make API calls as you normally would (e.g., using fetch or axios).
   * You do not need to manually attach any Authorization headers. The browser will automatically handle sending the secure HttpOnly
     cookies.

  3. Handling Token Expiration (The Most Important Part):
   * You should implement a global error handler for your API calls (e.g., using Axios interceptors).
   * If an API call returns a `401 Unauthorized` or `403 Forbidden` status, do the following:
       1. Do not immediately log the user out.
       2. Instead, have your handler automatically make a POST request to `[your_api_domain]/api/auth/refresh`.
       3. If the `/refresh` call succeeds (200 OK): A new access_token cookie has been set. Automatically retry the original API call
          that failed. This will be seamless to the user.
       4. If the `/refresh` call fails: This means the refresh token is also invalid. Now you should treat the session as expired. Clear
          any user data from your frontend state management (Redux, etc.) and redirect the user to the login page.

  4. Handling Logout:
   * Create a "Logout" button.
   * When clicked, it should make a POST request to `[your_api_domain]/api/auth/logout`.
   * On a successful response, clear the user's data from your frontend state and redirect to the login page.

