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
    /** Owning tenant; carried so anonymous visitor flows can pivot tenant context after loading. */
    private String tenantId;
    private String campaignId;
    private String sessionToken;
    private String recipient;
    private String message;
    private String externalId;
    private OutreachStatus status;
    private List<UserSession> session;
    /** ISO-3166 country code of the recipient (lower-case), copied from the matching {@code Lead}. */
    private String country;
    /** Carrier slug (lower-case, e.g. {@code bell}/{@code fido}), copied from the matching {@code Lead}. */
    private String carrier;
    private OffsetDateTime scheduledAt;
    private OffsetDateTime dispatchedAt;
    private OffsetDateTime deliveredAt;
    private OffsetDateTime failedAt;
    private OffsetDateTime bouncedAt;
    private OffsetDateTime complainedAt;
    /** Free-form reason from the provider (DSN status, SMS error code, etc.). */
    private String failureReason;
    /**
     * Set by the simulator when a visit was rejected by the bot gate. Null when
     * the outreach has not been visited or was visited successfully. Values
     * mirror the simulator-side BotReason enum (UA_DENYLIST, HEADER_GAUNTLET,
     * JA4_KNOWN_BOT, VELOCITY_TOKEN, VELOCITY_IP, etc.).
     */
    private String kickReason;
}
