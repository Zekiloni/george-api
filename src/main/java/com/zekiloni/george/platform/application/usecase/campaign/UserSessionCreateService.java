package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.platform.application.port.in.campaign.UserSessionCreateUseCase;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSessionCreateService implements UserSessionCreateUseCase {

    @Override
    public UserSession handle(String token, String userAgent, String ipAddress) {
        UserSession.builder()
                .build();
        return null;
    }
}
