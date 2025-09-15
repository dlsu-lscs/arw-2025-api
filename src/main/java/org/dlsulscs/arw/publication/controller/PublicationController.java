package org.dlsulscs.arw.publication.controller;

import org.dlsulscs.arw.publication.dto.PublicationUploadRequestDto;
import org.dlsulscs.arw.publication.dto.PublicationsDto;
import org.dlsulscs.arw.publication.model.Publications;
import org.dlsulscs.arw.publication.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pubs")
public class PublicationController {
    private final PublicationService publicationService;

    @Autowired
    public PublicationController(PublicationService publicationService) {
        this.publicationService = publicationService;
    }

    /*
     * Request example: GET /api/publications/by-org-name?orgName=SomeOrg
     * 
     * @param orgName
     * 
     * @return
     */
    @GetMapping("/by-org-name")
    public ResponseEntity<Publications> getPublicationsByOrgName(@RequestParam String orgName) {
        Publications pubs = publicationService.getPubsByOrgName(orgName);
        if (pubs == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pubs);
    }

    @PostMapping
    public ResponseEntity<PublicationsDto> upsertPublication(@RequestBody PublicationUploadRequestDto dto) {
        Publications savedPub = publicationService.upsertPublication(dto);
        return ResponseEntity.ok(mapToDto(savedPub));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<PublicationsDto>> bulkUpsertPublications(@RequestBody List<PublicationUploadRequestDto> dtos) {
        List<Publications> savedPubs = publicationService.bulkUpsertPublications(dtos);
        List<PublicationsDto> dtosResponse = savedPubs.stream().map(this::mapToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtosResponse);
    }

    private PublicationsDto mapToDto(Publications p) {
        return new PublicationsDto(
            p.getId(),
            p.getMain_pub_url(),
            p.getFee_pub_url(),
            p.getLogo_url(),
            p.getSub_logo_url(),
            p.getOrg_vid_url()
        );
    }

}
