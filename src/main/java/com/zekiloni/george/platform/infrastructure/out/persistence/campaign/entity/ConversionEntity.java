package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.TenantEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(name = "conversions")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ConversionEntity extends TenantEntity {

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "outreach_id")
    private String outreachId;

    @Column(name = "campaign_id")
    private String campaignId;

    @Column(name = "page_id")
    private String pageId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "form_data", columnDefinition = "jsonb")
    private Map<String, Object> formData;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "fingerprint")
    private String fingerprint;

    @Column(name = "converted_at", nullable = false)
    private OffsetDateTime convertedAt;
}
