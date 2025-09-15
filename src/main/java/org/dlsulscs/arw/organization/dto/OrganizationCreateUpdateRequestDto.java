package org.dlsulscs.arw.organization.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrganizationCreateUpdateRequestDto(
        String name,
        @JsonProperty("short_name") String shortName,
        String about,
        String fee,
        @JsonProperty("gforms_url") String gformsUrl,
        @JsonProperty("facebook_url") String facebookUrl,
        String mission,
        String vision,
        @JsonProperty("tagline") String tagline,
        @JsonProperty("cluster_name") String clusterName) {
}