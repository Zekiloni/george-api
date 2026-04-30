package com.zekiloni.george.platform.application.port.in.campaign;

import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserEvent;

public interface UserSessionCommandUseCase {

    void send(String sessionId, UserEvent command);
}
