package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.adapter;

import com.zekiloni.george.platform.application.port.out.campaign.ConversionRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.conversion.Conversion;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.mapper.ConversionEntityMapper;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.repository.ConversionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConversionRepositoryPortAdapter implements ConversionRepositoryPort {

    private final ConversionJpaRepository jpaRepository;
    private final ConversionEntityMapper mapper;

    @Override
    public Conversion save(Conversion conversion) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(conversion)));
    }

    @Override
    public List<Conversion> findByCampaignId(String campaignId) {
        return jpaRepository.findByCampaignId(campaignId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
