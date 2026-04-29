package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.adapter;

import com.zekiloni.george.platform.application.port.out.campaign.UserSessionRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.mapper.UserSessionEntityMapper;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.repository.UserSessionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSessionRepositoryPortAdapter implements UserSessionRepositoryPort {
    private final UserSessionJpaRepository jpaRepository;
    private final UserSessionEntityMapper mapper;

    @Override
    public UserSession save(UserSession session) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(session)));
    }
}
