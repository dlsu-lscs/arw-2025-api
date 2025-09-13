package org.dlsulscs.arw.organization.dto;

import org.dlsulscs.arw.cluster.dto.ClusterDto;
import org.dlsulscs.arw.publication.dto.PublicationsDto;

public record OrganizationResponseDto(
        Integer id,
        String name,
        String shortName,
        String about,
        String fee,
        String gformsUrl,
        String facebookUrl,
        String mission,
        String vision,
        String tagline,
        ClusterDto cluster,
        PublicationsDto publications
) {
}