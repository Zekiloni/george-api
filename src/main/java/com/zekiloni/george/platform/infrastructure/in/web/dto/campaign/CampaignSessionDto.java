package com.zekiloni.george.platform.infrastructure.in.web.dto.campaign;

import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;

import java.time.OffsetDateTime;
import java.util.List;

// Row for the campaign-detail sessions list. Exposes IP/UA so operators can
// recognize the visitor; carries currentStep so the UI can render flow
// progress without an extra fetch. Risk/quality fields (flags + geo + asn +
// riskScore) populated by the simulator's pre-render gate.
public record CampaignSessionDto(
        String sessionId,
        UserSessionStatus status,
        String ipAddress,
        String userAgent,
        String fingerprint,
        int currentStep,
        int viewCount,
        OffsetDateTime createdAt,
        OffsetDateTime lastActivityAt,
        List<String> flags,
        String country,
        String city,
        Integer asn,
        String asnOrg,
        Integer riskScore
) {}
