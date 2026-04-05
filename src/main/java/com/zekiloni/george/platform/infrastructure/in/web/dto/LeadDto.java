package com.zekiloni.george.platform.infrastructure.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadDto {
    private String id;
    private String country;
    private String areaCode;
    private String regionCode;
    private String location;
    private String phoneNumber;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    public boolean isSmtp;
}
