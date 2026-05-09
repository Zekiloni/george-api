package com.zekiloni.george.platform.infrastructure.in.web.mapper;

import com.zekiloni.george.common.domain.model.Ref;
import com.zekiloni.george.platform.domain.model.campaign.CampaignStatus;
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
public class CampaignDto {
    private String id;
    private String name;
    private TokenGenerationStrategy tokenStrategy;
    private int tokenLength;
    private String messageTemplate;
    private CampaignStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<Ref> flow;
    private Ref serviceAccess;
}
