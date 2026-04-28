package com.zekiloni.george.platform.domain.model.campaign.outreach.session;

import java.time.OffsetDateTime;
import java.util.Map;

public class InteractionSignal {
    private String id;
    private boolean incoming;
    private InteractionType type;
    private Map<String, Object> payload;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
