package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.adapter;

import com.zekiloni.george.platform.application.port.out.campaign.CampaignRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.mapper.CampaignEntityMapper;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.repository.CampaignJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CampaignRepositoryPortAdapter implements CampaignRepositoryPort {
    private final CampaignJpaRepository jpaRepository;
    private final CampaignEntityMapper mapper;

    @Override
    public Campaign save(Campaign campaign) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(campaign)));
    }

    @Override
    public Campaign update(Campaign campaign) {
        var entity = jpaRepository.findById(UUID.fromString(campaign.getId()))
                .orElseThrow(() -> new RuntimeException("Campaign not found: " + campaign.getId()));
        mapper.updateEntityFromDomain(campaign, entity);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Campaign> findById(String id) {
        return jpaRepository.findById(UUID.fromString(id))
                .map(mapper::toDomain);
    }

    @Override
    public Page<Campaign> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public List<Campaign> findScheduledReadyForDispatch() {
        return jpaRepository.findByStatusAndScheduledAtLessThanEqual(
                com.zekiloni.george.platform.domain.model.campaign.CampaignStatus.SCHEDULED,
                OffsetDateTime.now()
        ).stream().map(mapper::toDomain).toList();
    }
}
