package org.dlsulscs.arw.organization.dto;

import java.math.BigDecimal;

public record OrganizationUpdateRequestDto(
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
        String clusterName,
        String collegeName) {
}
