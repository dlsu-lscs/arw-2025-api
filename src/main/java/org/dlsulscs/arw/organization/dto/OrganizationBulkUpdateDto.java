package org.dlsulscs.arw.organization.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrganizationBulkUpdateDto(
        @JsonProperty("short_name") String shortName,
        String fee,
        @JsonProperty("gforms_url") String gformsUrl
) {
}
