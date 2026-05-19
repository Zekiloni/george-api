package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.platform.application.port.in.campaign.CampaignUpdateUseCase;
import com.zekiloni.george.platform.application.port.out.campaign.CampaignRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.domain.model.campaign.CampaignStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignUpdateService implements CampaignUpdateUseCase {
    private final CampaignStatusTransitionService statusTransitionService;
    private final CampaignDispatchService dispatchService;
    private final CampaignRepositoryPort campaignRepository;

    @Override
    public Campaign schedule(String campaignId) {
        return statusTransitionService.transitionTo(campaignId, CampaignStatus.SCHEDULED);
    }

    @Override
    public Campaign launch(String campaignId) {
        Campaign campaign = statusTransitionService.transitionTo(campaignId, CampaignStatus.ACTIVE);
        dispatchService.dispatch(campaignId, campaign.getServiceAccess().getId());
        return campaign;
    }

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

    private static final Set<String> VALID_ISO_COUNTRIES = Arrays.stream(Locale.getISOCountries())
            .collect(Collectors.toSet());

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
                .peek(c -> {
                    if (!VALID_ISO_COUNTRIES.contains(c)) {
                        throw new IllegalArgumentException("Invalid ISO-3166-1 alpha-2 country code: " + c);
                    }
                })
                .distinct()
                .toList();
        campaign.setBlockedCountries(normalized);
        return campaignRepository.save(campaign);
    }
}
