package com.zekiloni.george.platform.domain.model.campaign.outreach;

import java.util.Map;
import java.util.Set;

public enum OutreachStatus {
    SCHEDULED,
    SENT,
    DELIVERED,
    VISITED,
    BOUNCED,
    COMPLAINED,
    FAILED,
    COMPLETED,
    ABANDONED;

    private static final Map<OutreachStatus, Set<OutreachStatus>> ALLOWED = Map.of(
            SCHEDULED, Set.of(SENT, FAILED),
            SENT, Set.of(DELIVERED, BOUNCED, COMPLAINED, FAILED, VISITED),
            DELIVERED, Set.of(VISITED, BOUNCED, COMPLAINED, COMPLETED),
            VISITED, Set.of(COMPLETED, ABANDONED),
            BOUNCED, Set.of(),
            COMPLAINED, Set.of(),
            FAILED, Set.of(SCHEDULED),
            COMPLETED, Set.of(),
            ABANDONED, Set.of()
    );

    public void assertCanTransitionTo(OutreachStatus target) {
        if (!ALLOWED.get(this).contains(target)) {
            throw new IllegalStateException(
                    "OutreachStatus cannot transition from %s → %s".formatted(this, target)
            );
        }
    }
}
