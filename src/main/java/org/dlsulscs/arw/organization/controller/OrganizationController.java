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
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    @Autowired
    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping
    public ResponseEntity<Organization> createOrganization(@RequestBody OrganizationUpdateRequestDto orgDto) {
        Organization newOrg = organizationService.createOrganization(orgDto);
        return ResponseEntity.status(201).body(newOrg);
    }

    @GetMapping("/by-cluster-name")
    public ResponseEntity<List<Organization>> getOrganizationNamesByCluster(@RequestParam String clusterName) {
        List<Organization> orgs = organizationService.getOrganizationByCluster(clusterName);
        return ResponseEntity.ok(orgs);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Organization> getOrganizationByName(@PathVariable String name) {
        Organization org = organizationService.getOrganizationByName(name);
        return ResponseEntity.ok(org);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Organization> patchOrganization(@PathVariable Integer id,
            @RequestBody OrganizationUpdateRequestDto partialUpdateDto) {
        Organization patchedOrg = organizationService.patchOrganization(id, partialUpdateDto);
        return ResponseEntity.ok(patchedOrg);
    }
}
