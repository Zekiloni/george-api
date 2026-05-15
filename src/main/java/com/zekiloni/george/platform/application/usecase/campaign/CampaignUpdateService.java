package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.platform.application.port.in.campaign.CampaignUpdateUseCase;
import com.zekiloni.george.platform.application.port.out.campaign.CampaignRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.domain.model.campaign.CampaignStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CampaignUpdateService implements CampaignUpdateUseCase {
    private final CampaignStatusTransitionService statusTransitionService;
    private final CampaignRepositoryPort campaignRepository;

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

    @Override
    @Transactional
    public Campaign updateBlockedCountries(String campaignId, List<String> blockedCountries) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new NoSuchElementException("Campaign not found: " + campaignId));
        List<String> normalized = blockedCountries == null
                ? List.of()
                : blockedCountries.stream()
                        .filter(c -> c != null && !c.isBlank())
                        .map(String::toUpperCase)
                        .distinct()
                        .toList();
        campaign.setBlockedCountries(normalized);
        return campaignRepository.save(campaign);
    }
}
