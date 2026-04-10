package com.zekiloni.george.workspace.domain.page.tracking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingLink {
    private String id;
    private String token;
    private String originalUrl;
    private String shortCode;
    private Boolean isActive;
    private String formSubmissionId;
    private Integer clickCount;
    private OffsetDateTime lastClickedAt;
    private OffsetDateTime expiresAt;
    private String metadata;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

