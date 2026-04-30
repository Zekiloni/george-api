package com.zekiloni.george.platform.application.port.out.campaign;

import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;

import java.util.Optional;

public interface UserSessionRepositoryPort {
    UserSession save(UserSession session);

    Optional<UserSession> findById(String id);

    Optional<UserSession> findByWsToken(String wsToken);
}
