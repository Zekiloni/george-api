package com.zekiloni.george.platform.infrastructure.in.web.dto;

import java.time.OffsetDateTime;

public record LeadDto(String id, String country, String areaCode, String regionCode, String location,
                      String phoneNumber, String carrier, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
}
