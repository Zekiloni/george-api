package com.zekiloni.george.platform.infrastructure.in.web.dto.campaign;

import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;

import java.time.OffsetDateTime;

// Row for the campaign-detail sessions list. Exposes IP/UA so operators can
// recognize the visitor; carries currentStep so the UI can render flow
// progress without an extra fetch.
public record CampaignSessionDto(
        String sessionId,
        UserSessionStatus status,
        String ipAddress,
        String userAgent,
        String fingerprint,
        int currentStep,
        int viewCount,
        OffsetDateTime createdAt,
        OffsetDateTime lastActivityAt
) {}
