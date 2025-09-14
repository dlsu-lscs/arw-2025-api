package org.dlsulscs.arw.organization.dto;

public record OrganizationBulkUpdateDto(
        String shortName,
        String fee,
        String gformsUrl
) {
}
