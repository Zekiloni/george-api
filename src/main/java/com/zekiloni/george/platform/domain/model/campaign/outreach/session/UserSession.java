package com.zekiloni.george.platform.domain.model.campaign.outreach.session;

import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;

import java.time.OffsetDateTime;
import java.util.List;

public class UserSession {
    private String id;
    private String fingerprint;
    private String userAgent;
    private String ipAddress;
    private List<InteractionSignal> signal;
    private Outreach outreach;
    private UserSessionStatus status;
    private int viewCount;
    private OffsetDateTime lastActivityAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
