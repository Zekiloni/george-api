package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import com.zekiloni.george.platform.domain.model.campaign.CampaignStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Entity
@Table(name = "campaign_status_transitions")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CampaignStatusTransitionEntity extends BaseEntity {

    @Column(name = "campaign_id", nullable = false)
    private String campaignId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", nullable = false)
    private CampaignStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false)
    private CampaignStatus toStatus;

    @Column(name = "actor_id")
    private String actorId;

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;
}
