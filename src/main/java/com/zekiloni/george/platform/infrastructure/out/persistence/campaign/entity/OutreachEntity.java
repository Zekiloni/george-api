package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.TenantEntity;
import com.zekiloni.george.platform.domain.model.campaign.outreach.OutreachStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Entity
@Table(name = "outreach")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"definition"})
public class OutreachEntity extends TenantEntity {
    @Column(nullable = false)
    private String sessionToken;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutreachStatus status;

    @Column
    private String externalId;

    @Column
    private String country;

    @Column
    private String carrier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false, updatable = false)
    private CampaignEntity campaign;

    @Column
    private OffsetDateTime scheduledAt;

    @Column
    private OffsetDateTime dispatchedAt;

    @Column
    private OffsetDateTime deliveredAt;

    @Column
    private OffsetDateTime failedAt;

    @Column
    private OffsetDateTime bouncedAt;

    @Column
    private OffsetDateTime complainedAt;

    @Column(length = 500)
    private String failureReason;
}
