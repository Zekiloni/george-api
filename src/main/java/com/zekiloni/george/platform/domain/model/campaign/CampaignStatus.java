package com.zekiloni.george.platform.domain.model.campaign;

import java.util.Map;
import java.util.Set;

public enum CampaignStatus {
    SCHEDULED,
    ACTIVE,
    PAUSED,
    COMPLETED,
    ABORTED;

    private static final Map<CampaignStatus, Set<CampaignStatus>> ALLOWED = Map.of(
            SCHEDULED, Set.of(ACTIVE, ABORTED),
            ACTIVE, Set.of(PAUSED, COMPLETED, ABORTED),
            PAUSED, Set.of(ACTIVE, ABORTED),
            COMPLETED, Set.of(),
            ABORTED, Set.of()
    );

    public void assertCanTransitionTo(CampaignStatus target) {
        if (!ALLOWED.get(this).contains(target)) {
            throw new IllegalStateException(
                    "CampaignStatus cannot transition from %s → %s".formatted(this, target)
            );
        }
    }
}
