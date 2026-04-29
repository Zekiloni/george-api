package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.platform.application.port.out.campaign.CampaignRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.domain.model.campaign.CampaignStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CampaignStatusTransitionService {
    private final CampaignRepositoryPort repository;

    @Transactional
    public Campaign transitionTo(String campaignId, CampaignStatus target) {
        return repository.findById(campaignId)
                .map(campaign -> {
                    campaign.getStatus().assertCanTransitionTo(target);
                    campaign.setStatus(target);
                    return repository.update(campaign);
                })
                .orElseThrow(() -> new RuntimeException("Campaign not found: " + campaignId));
    }
}
