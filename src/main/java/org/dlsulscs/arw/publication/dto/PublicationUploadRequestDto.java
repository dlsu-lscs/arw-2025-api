package org.dlsulscs.arw.publication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PublicationUploadRequestDto(
    @JsonProperty("short_name") String shortName,
    @JsonProperty("main_pub_url") String mainPubUrl,
    @JsonProperty("fee_pub_url") String feePubUrl,
    @JsonProperty("logo_url") String logoUrl,
    @JsonProperty("sub_logo_url") String subLogoUrl,
    @JsonProperty("org_vid_url") String orgVidUrl
) {}
