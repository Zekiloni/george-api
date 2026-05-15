package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.TenantEntity;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserEvent;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;

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
    @Column(name = "ws_token", unique = true)
    private String wsToken;

    /** AES-256-GCM key, base64-encoded. Server generates + stores; never decrypts payloads. */
    @Column(name = "session_key", length = 64)
    private String sessionKey;

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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "events", columnDefinition = "jsonb")
    private List<UserEvent> events;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "flags", columnDefinition = "jsonb")
    private List<String> flags;

    @Column(name = "country", length = 2)
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "asn")
    private Integer asn;

    @Column(name = "asn_org")
    private String asnOrg;

    @Column(name = "risk_score")
    private Integer riskScore;
}
