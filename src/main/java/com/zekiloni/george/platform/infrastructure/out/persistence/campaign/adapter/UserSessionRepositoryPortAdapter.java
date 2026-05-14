package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.adapter;

import com.zekiloni.george.platform.application.port.out.campaign.UserSessionRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity.UserSessionEntity;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.mapper.UserSessionEntityMapper;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.repository.UserSessionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserSessionRepositoryPortAdapter implements UserSessionRepositoryPort {
    private final UserSessionJpaRepository jpaRepository;
    private final UserSessionEntityMapper mapper;

    private static final List<UserSessionStatus> REUSABLE_STATUSES = List.of(
            UserSessionStatus.ACTIVE,
            UserSessionStatus.IDLE,
            UserSessionStatus.ABANDONED
    );

    @Override
    @Transactional
    public UserSession save(UserSession session) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(session)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserSession> findById(String id) {
        return jpaRepository.findById(UUID.fromString(id)).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserSession> findByWsToken(String wsToken) {
        return jpaRepository.findByWsToken(wsToken).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserSession> findByWsTokenAcrossTenants(String wsToken) {
        return jpaRepository.findByWsTokenAcrossTenants(wsToken).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserSession> findReusable(String outreachId, String fingerprint) {
        return jpaRepository.findFirstByOutreach_IdAndFingerprintAndStatusInOrderByCreatedAtDesc(
                UUID.fromString(outreachId), fingerprint, REUSABLE_STATUSES)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserSession> findByCampaignId(String campaignId, Collection<UserSessionStatus> statuses, Pageable pageable) {
        UUID id = UUID.fromString(campaignId);
        Page<UserSessionEntity> page = (statuses == null || statuses.isEmpty())
                ? jpaRepository.findByCampaignId(id, pageable)
                : jpaRepository.findByCampaignIdAndStatusIn(id, statuses, pageable);
        return page.map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UserSessionStatus, Long> countByCampaignGroupedByStatus(String campaignId) {
        Map<UserSessionStatus, Long> out = new EnumMap<>(UserSessionStatus.class);
        for (Object[] row : jpaRepository.countByCampaignIdGroupedByStatus(UUID.fromString(campaignId))) {
            out.put((UserSessionStatus) row[0], ((Number) row[1]).longValue());
        }
        return out;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSession> findCompletedByCampaignId(String campaignId, int limit) {
        return jpaRepository.findCompletedByCampaignId(UUID.fromString(campaignId), PageRequest.of(0, limit))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
