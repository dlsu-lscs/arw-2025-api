# ARW 2025 API

- The API for the Annual Recruitment Week (ARW) 2025 in De La Salle University.

## Description

This API handles

## DB Model

org_pubs
orgs
users
colleges
clusters (like categories to classify orgs)


## Other details gathered from meeting

**Agenda:**

- Techstack overview  
  - Frontend  
    - NEXT  
    - Tanstack  
    - Drizzle  
  - Backend  
    - **JAVA SPRINGBOOT IS THE FUTURE** for your career  
    - Or Go pag naisipan (ijbol)  
  - Deployment  
    - Digital Ocean ($200 credits)  
      - 2 app instance & 1 database  
- Others  
  - Google Analytics

	

- Quick DB modelling and validation  
  - orgs  
  - clusters  
  - colleges  
  - org_pubs  
    - Few revisions to handle more pubs per org  
  - users (Google login schema)
- User flow  
  - List of pages:  
    - Landing page  
      - Press anywhere on the screen to continue  
    - Google Login page  
      - can merge with landing page similar to leap  
    - Org nav page  
      - List all orgs  
      - Inclusive of sorts by:  
        - Alphabetical  
        - Cluster (color coded)  
        - Availability (if slots exist)  
      - Inclusive of filters by:  
        - Search  
        - Cluster  
    - Cluster list page  
      - List of clusters  
    - Org nav page (clusters filtered)  
      - List of orgs based on the cluster selected  
    - 404 page  
    - Loading page  
    - View org page  
  - Draft flow:  
    - Landing -> Google Login -> List of all orgs -> Cluster list page -> List of orgs per cluster


## **Production API Readiness Checklist**

- [x] Use DTOs for requests and responses
- [ ] Validate all incoming data (use `@Valid`, custom validators)
- [ ] Implement global error handling (`@ControllerAdvice`)
- [x] Secure endpoints (authentication, authorization)
- [ ] Document APIs (OpenAPI/Swagger)
- [ ] Write unit and integration tests
- [ ] Log requests, responses, and errors
- [ ] Monitor application health (Actuator, metrics)
- [ ] Handle pagination for large lists
- [ ] Set proper HTTP status codes
- [ ] Use environment variables for secrets/config
- [ ] Handle CORS if needed
- [ ] Version your API if public (or better Change Management -> don't implement breaking changes for future changes)
- [ ] Rate limiting/throttling (if needed)
- [ ] Graceful shutdown and resource cleanup


login via google oauth -> if valid, then generate & return jwt (access token) and refresh token, store both in http-only cookie
  - now all request is intercepted by the JWT filter
