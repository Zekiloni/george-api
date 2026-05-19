package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.platform.application.port.out.campaign.CampaignRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.domain.model.campaign.CampaignStatus;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity.CampaignStatusTransitionEntity;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.repository.CampaignStatusTransitionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CampaignStatusTransitionService {
    private static final Logger log = LoggerFactory.getLogger(CampaignStatusTransitionService.class);

    private final CampaignRepositoryPort repository;
    private final CampaignStatusTransitionJpaRepository auditRepository;

    @Transactional
    public Campaign transitionTo(String campaignId, CampaignStatus target) {
        return repository.findById(campaignId)
                .map(campaign -> {
                    CampaignStatus from = campaign.getStatus();
                    from.assertCanTransitionTo(target);
                    campaign.setStatus(target);
                    Campaign saved = repository.update(campaign);
                    recordTransition(campaignId, from, target);
                    return saved;
                })
                .orElseThrow(() -> new NoSuchElementException("Campaign not found: " + campaignId));
    }

    private void recordTransition(String campaignId, CampaignStatus from, CampaignStatus to) {
        CampaignStatusTransitionEntity entity = CampaignStatusTransitionEntity.builder()
                .campaignId(campaignId)
                .fromStatus(from)
                .toStatus(to)
                .occurredAt(OffsetDateTime.now())
                .build();
        auditRepository.save(entity);
        log.info("Campaign {} transitioned {} → {} by {}", campaignId, from, to);
    }
}
