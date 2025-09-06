# ARW 2025 API Documentation

This document provides a reference for the available RESTful endpoints.

## Table of Contents

- [Authentication](#authentication-api-auth)
    - [Login - `/oauth2/authorization/google`](#login)
    - [Refresh Access Token - `/api/auth/refresh`](#refresh-access-token)
    - [Logout - `/api/auth/logout`](#logout)
    - [Access Token Refresh Strategy for Frontend](#access-token-refresh-strategy-for-frontend)
      - [Important Considerations for Frontend](#important-considerations-for-frontend)
- [Users (`/api/users`)](#users-api-users)
    - [Get Current User](#get-current-user)
    - [Get User by Email](#get-user-by-email)
- [Organizations (`/api/orgs`)](#organizations-api-orgs)
    - [Get All Organizations](#get-all-organizations)
    - [Search Organizations](#search-organizations)
    - [Get Organization by ID](#get-organization-by-id)
    - [Get Organization by Name](#get-organization-by-name)
    - [Create Organization](#create-organization)
    - [Update Organization](#update-organization)
    - [Delete Organization](#delete-organization)
- [Clusters (`/api/clusters`)](#clusters-api-clusters)
    - [Get All Clusters](#get-all-clusters)
    - [Get Cluster by ID](#get-cluster-by-id)
    - [Get Cluster by Name](#get-cluster-by-name)
    - [Create Cluster](#create-cluster)
    - [Update Cluster](#update-cluster)
    - [Delete Cluster](#delete-cluster)
- [Publications (`/api/pubs`)](#publications-api-pubs)
    - [Get Publications by Organization Name](#get-publications-by-organization-name)

---

## Authentication

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

This API uses short-lived JWT `access_token`s (15 minutes) and long-lived `refresh_token`s (7 days) for enhanced security. The frontend **must** implement a reactive strategy for token refreshing. The browser will **not** handle this automatically.

**Access Token Lifetime:** 15 minutes (configurable via `app.jwt.access-token-expiration-ms`).
**Refresh Token Lifetime:** 7 days (configurable via `app.jwt.refresh-token-expiration-ms`).

**Recommended Frontend Strategy:**

1.  **Make API Requests with Current Access Token:**
    *   For every authenticated API request, the frontend should simply make the request. The browser will automatically send the `HttpOnly` `access_token` cookie. Most modern HTTP clients (like Axios or Fetch) can be configured to handle this seamlessly.

2.  **Handle Expired Access Token (HTTP 401/403):**
    *   If an API request fails with an HTTP status code indicating an authentication issue (e.g., `401 Unauthorized` or `403 Forbidden`), this is the signal to refresh the token.
    *   Your frontend's HTTP client should be configured with an interceptor to catch these specific errors.
    *   When a 401/403 error is caught, the interceptor should **immediately** make a `POST` request to `/api/auth/refresh`. The browser will automatically send the `HttpOnly` `refresh_token` cookie with this request.

3.  **Process Refresh Token Response:**
    *   **If `/api/auth/refresh` succeeds (HTTP 200 OK):**
        *   The backend will have set a **new** `access_token` cookie in the browser.
        *   The frontend should then **retry the original failed API request**. The new access token will be automatically sent by the browser. This retry logic is often also handled within the HTTP client interceptor.
    *   **If `/api/auth/refresh` fails (e.g., HTTP 401/403):**
        *   This means the user's session has fully expired (the refresh token is also invalid).
        *   The frontend should **redirect the user to the login page** to re-authenticate.

#### **Important Considerations for Frontend:**

*   **`HttpOnly` Cookies:** The `access_token` and `refresh_token` are set as `HttpOnly` cookies. This means client-side JavaScript **cannot directly access** these tokens. This is a security measure to mitigate Cross-Site Scripting (XSS) attacks.
*   **No Proactive Polling:** Do not implement a timer to call `/api/auth/refresh` every 15 minutes. This is inefficient. The reactive interceptor-based approach described above is the standard and most robust method.
*   **User Experience:** When an access token is successfully refreshed, the user should ideally not perceive any interruption in their workflow. The interceptor pattern makes this process seamless.

---

## Users (`/api/users`)

### Get Current User

- **Method:** `GET`
- **Path:** `/api/users/current`
- **Description:** Retrieves the details of the currently authenticated user based on their `access_token`.
- **Requirements:** A valid `access_token` cookie must be sent with the request.
- **Response:** A `User` object.
- **Example Response:**
  ```json
  {
    "id": 1,
    "email": "test.user.one@example.com",
    "display_picture": "https://example.com/pic1.jpg",
    "name": "Test User One"
  }
  ```

### Get User by Email

- **Method:** `GET`
- **Path:** `/api/users`
- **Query Parameters:**
  - `email` (String, required): The user's email address.
- **Response:** A `User` object.
- **Example Response:**
  ```json
  {
    "id": 1,
    "email": "test.user.one@example.com",
    "display_picture": "https://example.com/pic1.jpg",
    "name": "Test User One"
  }
  ```

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
- **Response:** A `Page` object containing an array of `OrganizationResponseDto` objects and pagination details.
- **Example Response for `/api/orgs?cluster=ENGAGE&page=0`:**
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
                "name": "ENGAGE",
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
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 1,
    "size": 10,
    "number": 0,
    "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
    },
    "first": true,
    "numberOfElements": 1,
    "empty": false
}
```

### Search Organizations

- **Method:** `GET`
- **Path:** `/api/orgs/search`
- **Description:** Searches for organizations by their name, short name, or the name of their associated cluster.
- **Query Parameters:**
  - `q` (String, required): The search term (e.g., `/api/orgs/search?q=cs`).
  - `page` (Integer, optional, default: 0): The page number to retrieve.
  - `pageSize` (Integer, optional, default: 10): The number of items per page.
- **Response:** A `Page` object containing an array of matching `OrganizationResponseDto` objects and pagination details.

### Get Organization by ID

- **Method:** `GET`
- **Path:** `/api/orgs/{id}`
- **Description:** Retrieves a single organization by its primary ID.
- **Path Parameters:**
  - `id` (Integer, required): The unique ID of the organization.
- **Response:** An `OrganizationResponseDto` object.
- **Example Response:**
```json
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
        "name": "ENGAGE",
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
}
```

### Get Organization by Name

- **Method:** `GET`
- **Path:** `/api/orgs/name/{name}`
- **Description:** Retrieves a single organization by its unique name.
- **Path Parameters:**
  - `name` (String, required): The unique name of the organization (e.g., `/api/orgs/name/DLSU%20Computer%20Engineering%20Society`).
- **Response:** An `OrganizationResponseDto` object.

### Create Organization

- **Method:** `POST`
- **Path:** `/api/orgs`
- **Description:** Creates a new organization.
- **Request Body:** `OrganizationCreateUpdateRequestDto`
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
    "collegeName": "College of Computer Studies"
  }
  ```
- **Response:** The newly created `OrganizationResponseDto` object.

### Update Organization

- **Method:** `PATCH`
- **Path:** `/api/orgs/{id}`
- **Description:** Partially updates an existing organization's details.
- **Path Parameters:**
  - `id` (Integer, required): The ID of the organization to update.
- **Request Body:** `OrganizationCreateUpdateRequestDto` (only include fields to be updated).
- **Response:** The updated `OrganizationResponseDto` object.

### Delete Organization

- **Method:** `DELETE`
- **Path:** `/api/orgs/{id}`
- **Response:** `204 No Content`

---

## Clusters (`/api/clusters`)

### Get All Clusters

- **Method:** `GET`
- **Path:** `/api/clusters`
- **Response:** An array of `Cluster` objects.
- **Example Response:**
  ```json
  [
    {
        "id": 1,
        "name": "ENGAGE",
        "description": "Engineering Alliance Geared Towards Excellence"
    },
    {
        "id": 2,
        "name": "CAP13",
        "description": "College of Liberal Arts Organizations"
    }
  ]
  ```

### Get Cluster by ID

- **Method:** `GET`
- **Path:** `/api/clusters/{id}`
- **Response:** A `Cluster` object.
- **Example Response:**
  ```json
  {
      "id": 1,
      "name": "ENGAGE",
      "description": "Engineering Alliance Geared Towards Excellence"
  }
  ```

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
- **Example Response:**
  ```json
  {
    "id": 1,
    "main_pub_url": "https://example.com/coes_main.jpg",
    "fee_pub_url": "https://example.com/coes_fee.jpg",
    "logo_url": "https://example.com/coes_logo.png",
    "sub_logo_url": "https://example.com/coes_sublogo.png",
    "org_vid_url": "https://youtube.com/coes_vid"
  }
  ```
