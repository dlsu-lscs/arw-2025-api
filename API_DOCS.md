# ARW 2025 API Documentation

This document provides a reference for the available RESTful endpoints.

---

## Authentication (`/api/auth`)

### Login

Login is handled via an OAuth2 flow with Google. The primary entry point for a client application is to redirect the user to:

`GET /oauth2/authorization/google`

Upon successful authentication, the API will set two `HttpOnly` cookies:
- `access_token`: A short-lived JWT for authenticating requests.
- `refresh_token`: A long-lived token used to get a new access token.

The user will then be redirected to the frontend URL specified in the application properties.

### Refresh Access Token

- **Method:** `POST`
- **Path:** `/api/auth/refresh`
- **Description:** Renews the `access_token`.
- **Requirements:** A valid `refresh_token` cookie must be sent with the request.
- **Response:** Sets a new `access_token` cookie and returns a success message.

### Logout

- **Method:** `POST`
- **Path:** `/api/auth/logout`
- **Description:** Logs the user out by invalidating their refresh token on the server and clearing the session cookies.
- **Requirements:** A valid `refresh_token` cookie must be sent with the request.
- **Response:** Clears the `access_token` and `refresh_token` cookies.

### Access Token Refresh Strategy for Frontend

This API uses short-lived JWT `access_token`s (15 minutes) and long-lived `refresh_token`s (7 days) for enhanced security. The frontend should implement a reactive strategy for token refreshing, rather than proactively polling the `/api/auth/refresh` endpoint at fixed intervals.

**Access Token Lifetime:** 15 minutes (configurable via `app.jwt.access-token-expiration-ms` in `application.properties`).
**Refresh Token Lifetime:** 7 days (configurable via `app.jwt.refresh-token-expiration-ms` in `application.properties`).

**Recommended Frontend Strategy:**

1.  **Make API Requests with Current Access Token:**
    *   For every authenticated API request (i.e., any request to an endpoint requiring a logged-in user), the frontend should simply make the request. The browser will automatically send the `HttpOnly` `access_token` cookie.

2.  **Handle Expired Access Token (HTTP 401/403):**
    *   If an API request fails with an HTTP status code indicating an authentication issue (e.g., `401 Unauthorized` or `403 Forbidden` due to an expired access token), this is the signal to refresh the token.
    *   **Immediately** make a `POST` request to `/api/auth/refresh`. The browser will automatically send the `HttpOnly` `refresh_token` cookie with this request.

3.  **Process Refresh Token Response:**
    *   **If `/api/auth/refresh` succeeds (HTTP 200 OK):**
        *   The backend will have set a **new** `access_token` cookie in the browser.
        *   The frontend should then **retry the original failed API request**. The new access token will be automatically sent by the browser.
    *   **If `/api/auth/refresh` fails (e.g., HTTP 401/403, or a custom error indicating the refresh token is expired/invalid):**
        *   This means the user's session has fully expired.
        *   The frontend should **redirect the user to the login page** to re-authenticate.

**Important Considerations for Frontend:**

*   **`HttpOnly` Cookies:** The `access_token` and `refresh_token` are set as `HttpOnly` cookies. This means client-side JavaScript **cannot directly access** these tokens. This is a security measure to mitigate Cross-Site Scripting (XSS) attacks. The browser handles sending these cookies automatically with requests to the API's domain.
*   **No Proactive Polling:** Do not implement a timer to call `/api/auth/refresh` every 15 minutes. This is inefficient and can lead to unnecessary server load. The reactive approach described above is more robust and user-friendly.
*   **User Experience:** When an access token is successfully refreshed, the user should ideally not perceive any interruption in their workflow. The process should be seamless.

---

## Organizations (`/api/orgs`)

### Get All Organizations

- **Method:** `GET`
- **Path:** `/api/orgs`
- **Description:** Retrieves a paginated list of all organizations. Can be filtered by cluster.
- **Query Parameters:**
  - `cluster` (String, optional): The name of the cluster to filter by (e.g., `/api/orgs?cluster=ENGAGE`).
  - `page` (Integer, optional, default: 0): The page number to retrieve.
  - `pageSize` (Integer, optional, default: 10): The number of items per page.

- **Response:** A `Page` object containing an array of `Organization` objects and pagination details.

Example Response to `/api/orgs?cluster=ENGAGE&page=0`:
```json
{
    "content": [
        {
            "id": 1,
            "name": "DLSU Computer Engineering Society",
            "shortName": "DLSU CoES",
            "about": "Dedicated to the advancement of computer engineering.",
            "fee": 180.00,
            "bundleFee": 150.00,
            "gformsUrl": "https://forms.gle/coes",
            "facebookUrl": "https://facebook.com/coes",
            "mission": "To foster excellence in CoE.",
            "vision": "To be a leading CoE org.",
            "tagline": "Innovate. Create. Engineer.",
            "cluster": {
                "id": 1,
                "name": "Engage",
                "description": "Engineering Alliance Geared Towards Excellence"
            },
            "college": {
                "id": 1,
                "name": "College of Computer Studies",
                "description": "The premier institution for computer science and information technology."
            },
            "publications": {
                "id": 1,
                "mainPubUrl": "https://example.com/coes_main.jpg",
                "feePubUrl": "https://example.com/coes_fee.jpg",
                "logoUrl": "https://example.com/coes_logo.png",
                "subLogoUrl": "https://example.com/coes_sublogo.png",
                "orgVidUrl": "https://youtube.com/coes_vid"
            }
        },
        {
            "id": 2,
            "name": "DLSU Electronics and Communications Engineering Society",
            "shortName": "DLSU ECE",
            "about": "Promoting ECE excellence.",
            "fee": 170.00,
            "bundleFee": 140.00,
            "gformsUrl": "https://forms.gle/ece",
            "facebookUrl": "https://facebook.com/ece",
            "mission": "To advance ECE knowledge.",
            "vision": "To be the best ECE org.",
            "tagline": "Connect. Communicate. Create.",
            "cluster": {
                "id": 1,
                "name": "Engage",
                "description": "Engineering Alliance Geared Towards Excellence"
            },
            "college": {
                "id": 1,
                "name": "College of Computer Studies",
                "description": "The premier institution for computer science and information technology."
            },
            "publications": {
                "id": 2,
                "mainPubUrl": "https://example.com/ece_main.jpg",
                "feePubUrl": "https://example.com/ece_fee.jpg",
                "logoUrl": "https://example.com/ece_logo.png",
                "subLogoUrl": "https://example.com/ece_sublogo.png",
                "orgVidUrl": "https://youtube.com/ece_vid"
            }
        },
        {
            "id": 9,
            "name": "DLSU Civil Engineering Society",
            "shortName": "DLSU CES",
            "about": "Building the future.",
            "fee": 190.00,
            "bundleFee": 160.00,
            "gformsUrl": "https://forms.gle/ces",
            "facebookUrl": "https://facebook.com/ces",
            "mission": "To construct sustainable solutions.",
            "vision": "To be a leader in civil engineering.",
            "tagline": "Build. Design. Sustain.",
            "cluster": {
                "id": 1,
                "name": "Engage",
                "description": "Engineering Alliance Geared Towards Excellence"
            },
            "college": {
                "id": 1,
                "name": "College of Computer Studies",
                "description": "The premier institution for computer science and information technology."
            },
            "publications": {
                "id": 9,
                "mainPubUrl": "https://example.com/ces_main.jpg",
                "feePubUrl": "https://example.com/ces_fee.jpg",
                "logoUrl": "https://example.com/ces_logo.png",
                "subLogoUrl": "https://example.com/ces_sublogo.png",
                "orgVidUrl": "https://youtube.com/ces_vid"
            }
        },
        {
            "id": 14,
            "name": "DLSU Industrial Engineering Society",
            "shortName": "DLSU IES",
            "about": "Optimizing systems for efficiency.",
            "fee": 185.00,
            "bundleFee": 155.00,
            "gformsUrl": "https://forms.gle/ies",
            "facebookUrl": "https://facebook.com/ies",
            "mission": "To improve processes and systems.",
            "vision": "To be the forefront of IE.",
            "tagline": "Optimize. Innovate. Deliver.",
            "cluster": {
                "id": 1,
                "name": "Engage",
                "description": "Engineering Alliance Geared Towards Excellence"
            },
            "college": {
                "id": 1,
                "name": "College of Computer Studies",
                "description": "The premier institution for computer science and information technology."
            },
            "publications": {
                "id": 14,
                "mainPubUrl": "https://example.com/ies_main.jpg",
                "feePubUrl": "https://example.com/ies_fee.jpg",
                "logoUrl": "https://example.com/ies_logo.png",
                "subLogoUrl": "https://example.com/ies_sublogo.png",
                "orgVidUrl": "https://youtube.com/ies_vid"
            }
        }
    ],
    "page": {
        "size": 10,
        "number": 0,
        "totalElements": 4,
        "totalPages": 1
    }
}
```

### Search Organizations

- **Method:** `GET`
- **Path:** `/api/orgs/search`
- **Description:** Searches for organizations by their name, short name, or the name of their associated cluster.
- **Example Requests:** 
  - `/api/orgs/search?q=cs&page=1&pageSize=10`
- **Query Parameters:**
  - `q` (String, required): The search term (e.g., `/api/orgs/search?q=cs`).
  - `page` (Integer, optional, default: 0): The page number to retrieve.
  - `pageSize` (Integer, optional, default: 10): The number of items per page.
- **Response:** A `Page` object containing an array of matching `Organization` objects and pagination details.

### Get Organization by ID

- **Method:** `GET`
- **Path:** `/api/orgs/{id}`
- **Description:** Retrieves a single organization by its primary ID.
- **Path Parameters:**
  - `id` (Integer, required): The unique ID of the organization.
- **Response:** An `Organization` object.

### Get Organization by Name

- **Method:** `GET`
- **Path:** `/api/orgs/name/{name}`
- **Description:** Retrieves a single organization by its unique name.
- **Path Parameters:**
  - `name` (String, required): The unique name of the organization (e.g., `/api/orgs/name/Computer-Society`).
- **Response:** An `Organization` object.

### Create Organization

- **Method:** `POST`
- **Path:** `/api/orgs`
- **Description:** Creates a new organization.
- **Request Body:** `OrganizationUpdateRequestDto`
  ```json
  {
    "name": "New Org",
    "shortName": "NO",
    "about": "...",
    "fee": 50.00,
    "bundleFee": 40.00,
    "gformsUrl": "...",
    "facebookUrl": "...",
    "mission": "...",
    "vision": "...",
    "tagline": "...",
    "clusterName": "ENGAGE",
    "collegeName": "CCS"
  }
  ```
- **Response:** The newly created `Organization` object.

### Update Organization

- **Method:** `PATCH`
- **Path:** `/api/orgs/{id}`
- **Description:** Partially updates an existing organization's details.
- **Path Parameters:**
  - `id` (Integer, required): The ID of the organization to update.
- **Request Body:** `OrganizationUpdateRequestDto` (only include fields to be updated).
- **Response:** The updated `Organization` object.

---

## Clusters (`/api/clusters`)

### Get All Clusters

- **Method:** `GET`
- **Path:** `/api/clusters`
- **Response:** An array of `Cluster` objects.

### Get Cluster by ID

- **Method:** `GET`
- **Path:** `/api/clusters/{id}`
- **Response:** A `Cluster` object.

### Get Cluster by Name

- **Method:** `GET`
- **Path:** `/api/clusters/name/{name}`
- **Response:** A `Cluster` object.

### Create Cluster

- **Method:** `POST`
- **Path:** `/api/clusters`
- **Request Body:** `Cluster`
- **Response:** The created `Cluster` object.

### Update Cluster

- **Method:** `PUT`
- **Path:** `/api/clusters/{id}`
- **Request Body:** `Cluster`
- **Response:** The updated `Cluster` object.

### Delete Cluster

- **Method:** `DELETE`
- **Path:** `/api/clusters/{id}`
- **Response:** `204 No Content`.

---

## Publications (`/api/pubs`)

### Get Publications by Organization Name

- **Method:** `GET`
- **Path:** `/api/pubs/by-org-name`
- **Query Parameters:**
  - `orgName` (String, required): The name of the organization.
- **Response:** A `Publications` object.

---

## Users (`/api/users`)

### Get User by Email

- **Method:** `GET`
- **Path:** `/api/users`
- **Query Parameters:**
  - `email` (String, required): The user's email address.
- **Response:** A `User` object.
