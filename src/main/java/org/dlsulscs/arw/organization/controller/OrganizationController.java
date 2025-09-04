package org.dlsulscs.arw.organization.controller;

import org.dlsulscs.arw.organization.dto.OrganizationUpdateRequestDto;
import org.dlsulscs.arw.organization.model.Organization;
import org.dlsulscs.arw.organization.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orgs")
public class OrganizationController {

    private final OrganizationService organizationService;

    @Autowired
    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    /**
     * Gets all organizations, with an option to filter by cluster.
     * 
     * - GET /api/orgs -> returns all organizations.
     * - GET /api/orgs?cluster=SomeCluster&page=1&pageSize=5 -> returns all
     * organizations in that
     * cluster.
     *
     * @param cluster (optional) The name of the cluster to filter by.
     * @return A list of organizations.
     */
    @GetMapping
    public ResponseEntity<Page<Organization>> getOrganizations(
            @RequestParam(required = false) String cluster,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Organization> orgs = organizationService.getOrganizations(cluster, page, pageSize);
        return ResponseEntity.ok(orgs);
    }

    /**
     * Searches for organizations by name, short name, or cluster name.
     *
     * - GET /api/orgs/search?q=someQuery
     *
     * @param query The search query.
     * @return A list of matching organizations.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Organization>> searchOrganizations(@RequestParam("q") String query,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Organization> orgs = organizationService.searchOrganizations(query, page, pageSize);
        return ResponseEntity.ok(orgs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Organization> getOrganizationById(@PathVariable Integer id) {
        Organization org = this.organizationService.getOrganizationById(id);
        return ResponseEntity.ok(org);
    }

    /**
     * Get organization information by its unique name.
     * - GET /api/orgs/name/example-org
     *
     * @param name The unique name of the organization.
     * @return The organization.
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Organization> getOrganizationByName(@PathVariable String name) {
        Organization org = organizationService.getOrganizationByName(name);
        return ResponseEntity.ok(org);
    }

    @PostMapping
    public ResponseEntity<Organization> createOrganization(@RequestBody OrganizationUpdateRequestDto orgDto) {
        Organization newOrg = organizationService.createOrganization(orgDto);
        return ResponseEntity.status(201).body(newOrg);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Organization> patchOrganization(@PathVariable Integer id,
            @RequestBody OrganizationUpdateRequestDto partialUpdateDto) {
        Organization patchedOrg = organizationService.patchOrganization(id, partialUpdateDto);
        return ResponseEntity.ok(patchedOrg);
    }
}
