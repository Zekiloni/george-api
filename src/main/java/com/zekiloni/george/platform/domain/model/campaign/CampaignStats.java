package com.zekiloni.george.platform.domain.model.campaign;

// Aggregate funnel for a campaign overview: outreach delivery counts +
// visitor session outcome counts + derived conversion rate.
public record CampaignStats(
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
