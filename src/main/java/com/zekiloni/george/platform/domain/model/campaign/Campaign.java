package com.zekiloni.george.platform.domain.model.campaign;

import com.zekiloni.george.common.domain.model.Ref;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.service.campaign.TokenGenerationStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {
    private String id;
    private String name;
    private TokenGenerationStrategy tokenStrategy;
    private int tokenLength;
    private String messageTemplate;
    private CampaignStatus status;
    @Builder.Default
    private List<Ref> flow = new ArrayList<>();
    private Ref serviceAccess;
    private List<Outreach> outreach;
    private String baseUrl;
    private OffsetDateTime scheduledAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String tenantId;
    /** When this campaign was cloned from a Template, the source template's id is remembered here. */
    private String sourceTemplateId;
    /** Version snapshot at clone time — used to detect when an updated template is available. */
    private Integer sourceTemplateVersion;
    /**
     * ISO-3166-1 alpha-2 country codes whose visitors are refused at the gate.
     * Empty / null means "no geo block." Checked by UserSessionCreateService
     * against the simulator-computed enrichment.country — when the visitor's
     * country is in this set, session creation returns 401 and the page is
     * never rendered. Case-insensitive match (server normalizes to upper).
     */
    @Builder.Default
    private List<String> blockedCountries = new ArrayList<>();
}
