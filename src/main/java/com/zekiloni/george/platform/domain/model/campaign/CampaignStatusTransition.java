package com.zekiloni.george.platform.domain.model.campaign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignStatusTransition {
    private String id;
    private String campaignId;
    private CampaignStatus fromStatus;
    private CampaignStatus toStatus;
    private String actorId;
    private OffsetDateTime occurredAt;
}
