package org.dlsulscs.arw.organization.dto;

import org.dlsulscs.arw.cluster.dto.ClusterDto;
import org.dlsulscs.arw.publication.dto.PublicationsDto;

import java.math.BigDecimal;

public record OrganizationResponseDto(
        Integer id,
        String name,
        String shortName,
        String about,
        BigDecimal fee,
        BigDecimal bundleFee,
        String gformsUrl,
        String facebookUrl,
        String mission,
        String vision,
        String tagline,
        ClusterDto cluster,
        PublicationsDto publications
) {
}
