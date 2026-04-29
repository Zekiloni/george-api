package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.platform.application.port.in.campaign.CampaignUpdateUseCase;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.domain.model.campaign.CampaignStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignUpdateService implements CampaignUpdateUseCase {
    private final CampaignStatusTransitionService statusTransitionService;

    @Override
    public Campaign pause(String campaignId) {
        return statusTransitionService.transitionTo(campaignId, CampaignStatus.PAUSED);
    }

    @Override
    public Campaign resume(String campaignId) {
        return statusTransitionService.transitionTo(campaignId, CampaignStatus.ACTIVE);
    }

    @Override
    public Campaign abort(String campaignId) {
        return statusTransitionService.transitionTo(campaignId, CampaignStatus.ABORTED);
    }

    @Override
    public Campaign complete(String campaignId) {
        return statusTransitionService.transitionTo(campaignId, CampaignStatus.COMPLETED);
    }
}
