package com.zekiloni.george.platform.infrastructure.in.web.dto.campaign;

import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;

import java.time.OffsetDateTime;
import java.util.Map;

// One row per visitor session for the campaign-detail Responses tab. formData
// is the merged payload across the session's SUBMIT events (later submits win
// on collisions, so re-submits on later steps take precedence).
public record CampaignResponseDto(
        String sessionId,
        String outreachId,
        String recipient,
        UserSessionStatus status,
        int currentStep,
        String ipAddress,
        String userAgent,
        OffsetDateTime completedAt,
        OffsetDateTime createdAt,
        Map<String, Object> formData
) {}
