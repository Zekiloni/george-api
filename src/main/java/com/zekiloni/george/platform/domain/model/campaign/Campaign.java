package com.zekiloni.george.platform.domain.model.campaign;

import com.zekiloni.george.common.domain.model.Ref;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.service.campaign.TokenGenerationStrategy;
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
public class Campaign {
    private String id;
    private String name;
    private TokenGenerationStrategy tokenStrategy;
    private int tokenLength;
    private String messageTemplate;
    private CampaignStatus status;
    private Ref page;
    private Ref serviceAccess;  // Changed from gateway to serviceAccess
    private List<Outreach> outreach;
    private String baseUrl;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
