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
