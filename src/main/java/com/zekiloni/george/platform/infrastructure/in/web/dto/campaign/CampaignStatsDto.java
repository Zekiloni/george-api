package com.zekiloni.george.platform.infrastructure.in.web.dto.campaign;

// Aggregate counts for a campaign: outreach delivery funnel + visitor session
// outcomes. Computed on read from outreach/user_sessions; no precomputation.
public record CampaignStatsDto(
        long totalRecipients,
        long sentCount,
        long visitedCount,
        long completedOutreachCount,
        long abandonedOutreachCount,
        long activeSessions,
        long idleSessions,
        long completedSessions,
        long abandonedSessions,
        long blockedSessions,
        double conversionRate
) {}
