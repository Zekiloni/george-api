package com.zekiloni.george.platform.application.port.out.campaign;

import com.zekiloni.george.platform.domain.model.campaign.Campaign;

import java.util.List;
import java.util.Optional;

public interface CampaignRepositoryPort {
    Campaign save(Campaign campaign);
    Campaign update(Campaign campaign);
    Optional<Campaign> findById(String id);
    List<Campaign> findScheduledReadyForDispatch();
}
