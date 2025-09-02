package org.dlsulscs.arw.organization.controller;

import org.dlsulscs.arw.organization.dto.OrganizationUpdateRequestDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.dlsulscs.arw.organization.model.Organization;
import org.dlsulscs.arw.organization.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orgs")
public class OrganizationController {

    private final OrganizationService organizationService;

    @Autowired
    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    /*
     * Get all organizations
     * 
     * @return list of organizations
     */
    @GetMapping
    public ResponseEntity<List<Organization>> getAllOrganizations() {
        // TODO: randomize organizations (or should this be done client-side?)
        List<Organization> orgs = this.organizationService.getAllOrganizations();
        return ResponseEntity.ok(orgs);
    }

    /*
     * Get a list of organizations with their information by cluster name
     * 
     * Request example: /api/orgs/by-cluster-name?clusterName=ENGAGE
     *
     * @param clusterName
     * 
     * @return list of organizations
     */
    @GetMapping("/by-cluster-name")
    public ResponseEntity<List<Organization>> getOrganizationNamesByCluster(@RequestParam String clusterName) {
        // TODO: randomize list of organization response
        List<Organization> orgs = organizationService.getOrganizationByCluster(clusterName);
        return ResponseEntity.ok(orgs);
    }

    /*
     * Get organization information by name
     * 
     * Request example: /api/orgs/name/example-org
     * 
     * @param name
     * 
     * @return organization
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

    // NOTE: do i need a delete endpoint? for what?

    // TODO: add search functionality here
    // - should include metadata, org name,
}
