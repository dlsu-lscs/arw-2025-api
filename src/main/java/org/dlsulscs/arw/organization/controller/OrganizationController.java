package org.dlsulscs.arw.organization.controller;

import org.dlsulscs.arw.organization.dto.OrganizationCreateUpdateRequestDto;
import org.dlsulscs.arw.organization.model.Organization;
import org.dlsulscs.arw.organization.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.dlsulscs.arw.organization.dto.OrganizationResponseDto;
import org.dlsulscs.arw.cluster.dto.ClusterDto;
import org.dlsulscs.arw.publication.dto.PublicationsDto;

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
     * - GET /api/orgs?cluster=SomeCluster&page=0&pageSize=5 -> returns all
     * organizations in that
     * cluster.
     *
     * @param cluster (optional) The name of the cluster to filter by.
     * @return A list of organizations.
     */
    @GetMapping
    public ResponseEntity<Page<OrganizationResponseDto>> getOrganizations(
            @RequestParam(required = false) String cluster,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String seed) {
        Page<Organization> orgs = organizationService.getOrganizations(cluster, page, pageSize, seed);
        Page<OrganizationResponseDto> orgsDto = orgs.map(this::mapToOrganizationResponseDto);
        return ResponseEntity.ok(orgsDto);
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
    public ResponseEntity<Page<OrganizationResponseDto>> searchOrganizations(@RequestParam("q") String query,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Organization> orgs = organizationService.searchOrganizations(query, page, pageSize);
        Page<OrganizationResponseDto> orgsDto = orgs.map(this::mapToOrganizationResponseDto);
        return ResponseEntity.ok(orgsDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponseDto> getOrganizationById(@PathVariable Integer id) {
        Organization org = this.organizationService.getOrganizationById(id);
        return ResponseEntity.ok(mapToOrganizationResponseDto(org));
    }

    /**
     * Get organization information by its unique name.
     * - GET /api/orgs/name/example-org
     *
     * @param name The unique name of the organization.
     * @return The organization.
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<OrganizationResponseDto> getOrganizationByName(@PathVariable String name) {
        Organization org = organizationService.getOrganizationByName(name);
        return ResponseEntity.ok(mapToOrganizationResponseDto(org));
    }

    @PostMapping
    public ResponseEntity<OrganizationResponseDto> createOrganization(
            @RequestBody OrganizationCreateUpdateRequestDto orgDto) {
        Organization newOrg = organizationService.createOrganization(orgDto);
        return ResponseEntity.status(201).body(mapToOrganizationResponseDto(newOrg));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrganizationResponseDto> patchOrganization(@PathVariable Integer id,
            @RequestBody OrganizationCreateUpdateRequestDto partialUpdateDto) {
        Organization patchedOrg = organizationService.patchOrganization(id, partialUpdateDto);
        return ResponseEntity.ok(mapToOrganizationResponseDto(patchedOrg));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Integer id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }

    private OrganizationResponseDto mapToOrganizationResponseDto(Organization org) {
        ClusterDto clusterDto = null;
        if (org.getCluster() != null) {
            clusterDto = new ClusterDto(org.getCluster().getId(), org.getCluster().getName(),
                    org.getCluster().getDescription());
        }

        PublicationsDto publicationsDto = null;
        if (org.getPublications() != null) {
            publicationsDto = new PublicationsDto(
                    org.getPublications().getId(),
                    org.getPublications().getMain_pub_url(),
                    org.getPublications().getFee_pub_url(),
                    org.getPublications().getLogo_url(),
                    org.getPublications().getSub_logo_url(),
                    org.getPublications().getOrg_vid_url());
        }

        return new OrganizationResponseDto(
                org.getId(),
                org.getName(),
                org.getShortName(),
                org.getAbout(),
                org.getFee(),
                org.getBundleFee(),
                org.getGformsUrl(),
                org.getFacebookUrl(),
                org.getMission(),
                org.getVision(),
                org.getTagline(),
                clusterDto,
                publicationsDto);
    }
}
