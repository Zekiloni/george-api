package com.zekiloni.george.platform.application.port.in.campaign;

import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CampaignQueryUseCase {
    Optional<Campaign> findById(String id);
    Page<Campaign> findAll(Pageable pageable);
}
