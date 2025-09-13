package org.dlsulscs.arw.organization.dto;

public record OrganizationCreateUpdateRequestDto(
        String name,
        String shortName,
        String about,
        String fee,
        String gformsUrl,
        String facebookUrl,
        String mission,
        String vision,
        String tagline,
        String clusterName) {
}