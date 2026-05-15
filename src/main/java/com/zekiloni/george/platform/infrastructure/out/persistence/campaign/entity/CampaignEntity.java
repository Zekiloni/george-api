package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.TenantEntity;
import com.zekiloni.george.platform.domain.model.campaign.CampaignStatus;
import com.zekiloni.george.platform.domain.service.campaign.TokenGenerationStrategy;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "campaigns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"definition"})
public class CampaignEntity extends TenantEntity {
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column
    private TokenGenerationStrategy tokenStrategy;

    @Column(nullable = false)
    private int tokenLength;

    @Column
    private String messageTemplate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CampaignStatus status;

    @Column(nullable = false)
    private String baseUrl;

    // Ordered list of page IDs forming the campaign's visitor flow.
    // First element is the entry page; subsequent steps are reached by
    // submitting the previous step's form.
    @Column(name = "flow_page_ids", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<UUID> flowPageIds = new ArrayList<>();

    @Column(name = "service_access_id", nullable = false)
    private UUID serviceAccessId;

    @Column(name = "scheduled_at")
    private OffsetDateTime scheduledAt;

    /** Set when this campaign was cloned from a Template. NULL otherwise. */
    @Column(name = "source_template_id")
    private UUID sourceTemplateId;

    /** Template version snapshot — bumped by the template author signals an update is available. */
    @Column(name = "source_template_version")
    private Integer sourceTemplateVersion;

    /** Geo block list — ISO-3166-1 alpha-2 codes. JSONB array; empty means no block. */
    @Column(name = "blocked_countries", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> blockedCountries = new ArrayList<>();

    @OneToMany(
            mappedBy = "campaign",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<OutreachEntity> outreach = new ArrayList<>();
}
