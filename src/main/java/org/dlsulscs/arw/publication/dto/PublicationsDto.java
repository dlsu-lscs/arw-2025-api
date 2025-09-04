package org.dlsulscs.arw.publication.dto;

public record PublicationsDto(
        Integer id,
        String mainPubUrl,
        String feePubUrl,
        String logoUrl,
        String subLogoUrl,
        String orgVidUrl
) {
}
