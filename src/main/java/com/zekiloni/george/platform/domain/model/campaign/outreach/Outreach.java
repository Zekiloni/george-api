package com.zekiloni.george.platform.domain.model.campaign.outreach;

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
    private String sessionToken;
    private String recipient;
    private String message;
    private String externalId;
    private OutreachStatus status;
    private List<InteractionSignal> signal;
    private OffsetDateTime scheduledAt;
    private OffsetDateTime dispatchedAt;
    private OffsetDateTime deliveredAt;
    private OffsetDateTime failedAt;
}
