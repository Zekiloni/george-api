package com.zekiloni.george.platform.application.port.out.campaign;

import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CampaignRepositoryPort {
    Campaign save(Campaign campaign);
    Campaign update(Campaign campaign);
    Optional<Campaign> findById(String id);
    Page<Campaign> findAll(Pageable pageable);
    List<Campaign> findScheduledReadyForDispatch();
}
