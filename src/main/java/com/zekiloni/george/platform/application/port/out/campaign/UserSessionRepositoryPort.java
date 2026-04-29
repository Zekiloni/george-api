package com.zekiloni.george.platform.application.port.out.campaign;

import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;

public interface UserSessionRepositoryPort {
    UserSession save(UserSession session);
}
