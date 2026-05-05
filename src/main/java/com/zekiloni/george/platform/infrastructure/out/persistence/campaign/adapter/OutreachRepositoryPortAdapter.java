package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.adapter;

import com.zekiloni.george.platform.application.port.out.campaign.OutreachRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.mapper.OutreachEntityMapper;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.repository.OutreachJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class OutreachRepositoryPortAdapter implements OutreachRepositoryPort {
    private final OutreachJpaRepository jpaRepository;
    private final OutreachEntityMapper mapper;

    @Override
    public Outreach save(Outreach outreach) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(outreach)));
    }

    @Override
    public List<Outreach> saveAll(List<Outreach> outreach) {
        return mapper.toDomain(jpaRepository.saveAll(mapper.toEntity(outreach)));
    }

    @Override
    public Stream<Outreach> findByCampaignId(String campaignId) {
        return mapper.toDomain(jpaRepository.streamByCampaignId(UUID.fromString(campaignId)));
    }

    @Override
    public Optional<Outreach> findBySessionToken(String sessionToken) {
        return jpaRepository.findBySessionToken(sessionToken).map(mapper::toDomain);
    }

    @Override
    public Optional<Outreach> findById(String id) {
        return jpaRepository.findById(UUID.fromString(id)).map(mapper::toDomain);
    }

    @Override
    public long countDispatchedSinceByServiceAccessId(String serviceAccessId, OffsetDateTime since) {
        return jpaRepository.countDispatchedSinceByServiceAccessId(UUID.fromString(serviceAccessId), since);
    }
}
