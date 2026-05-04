package com.zekiloni.george.platform.domain.model.campaign.outreach;

import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Outreach {
    private String id;
    private String campaignId;
    private String sessionToken;
    private String recipient;
    private String message;
    private String externalId;
    private OutreachStatus status;
    private List<UserSession> session;
    private OffsetDateTime scheduledAt;
    private OffsetDateTime dispatchedAt;
    private OffsetDateTime deliveredAt;
    private OffsetDateTime failedAt;
    private OffsetDateTime bouncedAt;
    private OffsetDateTime complainedAt;
    /** Free-form reason from the provider (DSN status, SMS error code, etc.). */
    private String failureReason;
}
