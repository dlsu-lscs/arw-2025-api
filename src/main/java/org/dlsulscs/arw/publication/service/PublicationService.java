package org.dlsulscs.arw.publication.service;

import org.dlsulscs.arw.common.exception.ResourceNotFoundException;
import org.dlsulscs.arw.publication.model.Publications;
import org.dlsulscs.arw.publication.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublicationService {
    private final PublicationRepository publicationRepository;

    @Autowired
    public PublicationService(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    /*
     * Get specific publication information of an organization
     * 
     * @param orgName
     * 
     * @return
     */
    public Publications getPubsByOrgName(String orgName) {
        Publications pub = this.publicationRepository.findPubsByOrgName(orgName)
                .orElseThrow(() -> new ResourceNotFoundException("Publication not found with org name: " + orgName));
        return pub;
    }
}
