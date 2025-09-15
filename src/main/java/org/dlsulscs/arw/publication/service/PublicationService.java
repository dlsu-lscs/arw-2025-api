package org.dlsulscs.arw.publication.service;

import org.dlsulscs.arw.common.exception.ResourceNotFoundException;
import org.dlsulscs.arw.organization.model.Organization;
import org.dlsulscs.arw.organization.service.OrganizationService;
import org.dlsulscs.arw.publication.dto.PublicationUploadRequestDto;
import org.dlsulscs.arw.publication.model.Publications;
import org.dlsulscs.arw.publication.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PublicationService {
    private final PublicationRepository publicationRepository;
    private final OrganizationService organizationService;

    @Autowired
    public PublicationService(PublicationRepository publicationRepository, OrganizationService organizationService) {
        this.publicationRepository = publicationRepository;
        this.organizationService = organizationService;
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

    @Transactional
    public Publications upsertPublication(PublicationUploadRequestDto dto) {
        Organization org = organizationService.getOrganizationByShortName(dto.shortName());

        Publications publications = publicationRepository.findByOrganization(org)
                                        .orElse(new Publications());

        publications.setOrganization(org);
        if (dto.mainPubUrl() != null) publications.setMain_pub_url(dto.mainPubUrl());
        if (dto.feePubUrl() != null) publications.setFee_pub_url(dto.feePubUrl());
        if (dto.logoUrl() != null) publications.setLogo_url(dto.logoUrl());
        if (dto.subLogoUrl() != null) publications.setSub_logo_url(dto.subLogoUrl());
        if (dto.orgVidUrl() != null) publications.setOrg_vid_url(dto.orgVidUrl());

        return publicationRepository.save(publications);
    }

    @Transactional
    public List<Publications> bulkUpsertPublications(List<PublicationUploadRequestDto> dtos) {
        List<Publications> pubsToSave = dtos.stream().map(dto -> {
            Organization org = organizationService.getOrganizationByShortName(dto.shortName());
            Publications publications = publicationRepository.findByOrganization(org)
                                            .orElse(new Publications());
            publications.setOrganization(org);
            if (dto.mainPubUrl() != null) publications.setMain_pub_url(dto.mainPubUrl());
            if (dto.feePubUrl() != null) publications.setFee_pub_url(dto.feePubUrl());
            if (dto.logoUrl() != null) publications.setLogo_url(dto.logoUrl());
            if (dto.subLogoUrl() != null) publications.setSub_logo_url(dto.subLogoUrl());
            if (dto.orgVidUrl() != null) publications.setOrg_vid_url(dto.orgVidUrl());
            return publications;
        }).collect(Collectors.toList());

        return publicationRepository.saveAll(pubsToSave);
    }
}
