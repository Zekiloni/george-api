package com.zekiloni.george.platform.infrastructure.in.job.campaign;

import com.zekiloni.george.platform.application.port.out.campaign.CampaignRepositoryPort;
import com.zekiloni.george.platform.application.usecase.campaign.CampaignDispatchService;
import com.zekiloni.george.platform.application.usecase.campaign.CampaignStatusTransitionService;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.domain.model.campaign.CampaignStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CampaignScheduler {
    private final CampaignRepositoryPort campaignRepository;
    private final CampaignStatusTransitionService statusTransitionService;
    private final CampaignDispatchService dispatchService;

    @Scheduled(fixedDelay = 300000) // every 5 minutes
    public void dispatchScheduledCampaigns() {
        List<Campaign> ready = campaignRepository.findScheduledReadyForDispatch();
        if (ready.isEmpty()) {
            return;
        }

        log.info("Found {} scheduled campaigns ready for dispatch", ready.size());

        for (Campaign campaign : ready) {
            try {
                statusTransitionService.transitionTo(campaign.getId(), CampaignStatus.ACTIVE);
                dispatchService.dispatch(campaign.getId(), campaign.getServiceAccess().getId());
            } catch (Exception e) {
                log.error("Failed to dispatch scheduled campaign {}: {}", campaign.getId(), e.getMessage(), e);
            }
        }
    }
}
