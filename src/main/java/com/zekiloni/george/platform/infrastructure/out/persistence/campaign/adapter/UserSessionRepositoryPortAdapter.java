package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.adapter;

import com.zekiloni.george.platform.application.port.out.campaign.UserSessionRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.mapper.UserSessionEntityMapper;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.repository.UserSessionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserSessionRepositoryPortAdapter implements UserSessionRepositoryPort {
    private final UserSessionJpaRepository jpaRepository;
    private final UserSessionEntityMapper mapper;

    @Override
    public UserSession save(UserSession session) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(session)));
    }

    @Override
    public Optional<UserSession> findById(String id) {
        return jpaRepository.findById(UUID.fromString(id)).map(mapper::toDomain);
    }

    @Override
    public Optional<UserSession> findByWsToken(String wsToken) {
        return jpaRepository.findByWsToken(wsToken).map(mapper::toDomain);
    }
}
