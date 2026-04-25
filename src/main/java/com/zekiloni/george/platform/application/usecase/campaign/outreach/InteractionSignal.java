package com.zekiloni.george.platform.application.usecase.campaign.outreach;

import java.time.OffsetDateTime;
import java.util.Map;

public class InteractionSignal {
    private String id;
    private InteractionType type;
    private Map<String, Object> payload;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
