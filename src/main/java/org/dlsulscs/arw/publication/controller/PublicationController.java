package org.dlsulscs.arw.publication.controller;

import org.dlsulscs.arw.publication.model.Publications;
import org.dlsulscs.arw.publication.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
