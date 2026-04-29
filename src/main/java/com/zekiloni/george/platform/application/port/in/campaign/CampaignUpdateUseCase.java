package com.zekiloni.george.platform.application.port.in.campaign;

import com.zekiloni.george.platform.domain.model.campaign.Campaign;

public interface CampaignUpdateUseCase {
    Campaign pause(String campaignId);
    Campaign resume(String campaignId);
    Campaign abort(String campaignId);
    Campaign complete(String campaignId);
}
