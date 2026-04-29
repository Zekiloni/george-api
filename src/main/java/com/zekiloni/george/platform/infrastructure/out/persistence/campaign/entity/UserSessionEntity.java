package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.TenantEntity;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"outreach"})
public class UserSessionEntity extends TenantEntity {
    @Column(name = "fingerprint")
    private String fingerprint;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "ip_address")
    private String ipAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outreach_id", nullable = false, updatable = false)
    private OutreachEntity outreach;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserSessionStatus status;

    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    @Column(name = "last_activity_at")
    private OffsetDateTime lastActivityAt;
}
