package com.zekiloni.george.platform.domain.model.campaign;

import java.time.OffsetDateTime;
import java.util.Map;

import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;

// Derived view for the campaign Responses tab: one row per visitor session
// with the form data merged across all SUBMIT events on that session.
public record CampaignResponse(
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
