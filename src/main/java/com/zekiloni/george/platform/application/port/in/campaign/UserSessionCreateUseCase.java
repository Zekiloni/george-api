package com.zekiloni.george.platform.application.port.in.campaign;


import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;

public interface UserSessionCreateUseCase {
    UserSession handle(String token, String userAgent, String ipAddress);
}
