package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.common.domain.model.Ref;
import com.zekiloni.george.platform.application.usecase.campaign.outreach.Outreach;

import java.time.OffsetDateTime;
import java.util.List;

public class Campaign {
    private String id;
    private String name;
    private CampaignStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Ref channel;
    private List<Outreach> outreach;
}
