package com.zekiloni.george.platform.domain.model.campaign.outreach.session;

import java.time.OffsetDateTime;
import java.util.Map;

public class UserEvent {
    private String id;
    private String eventKey;
    private boolean incoming;
    private InteractionType type;
    private Map<String, Object> payload;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
