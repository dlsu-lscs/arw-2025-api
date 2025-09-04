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
