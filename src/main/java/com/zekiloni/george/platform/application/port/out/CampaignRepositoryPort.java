package com.zekiloni.george.platform.application.port.out;

import com.zekiloni.george.platform.domain.model.campaign.Campaign;

public interface CampaignRepositoryPort {
    Campaign save(Campaign campaign);
}
