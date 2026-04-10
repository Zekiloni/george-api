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
public class TrackingEvent {
    private String id;
    private String trackingLinkId;
    private String eventType;
    private String ipAddress;
    private String userAgent;
    private String referer;
    private OffsetDateTime eventTimestamp;
    private String metadata;
    private String sessionId;
    private String deviceType;
    private String geographicLocation;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

