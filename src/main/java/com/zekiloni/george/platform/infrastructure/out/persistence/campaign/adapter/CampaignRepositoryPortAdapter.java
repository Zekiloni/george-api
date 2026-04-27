package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.adapter;

import com.zekiloni.george.platform.application.port.out.CampaignRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.mapper.CampaignEntityMapper;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.repository.CampaignJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
    public Optional<Campaign> findById(String id) {
        return jpaRepository.findById(UUID.fromString(id))
                .map(mapper::toDomain);
    }
}
