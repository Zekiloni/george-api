package com.zekiloni.george.platform.application.port.in.campaign;

import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;

public interface UserSessionCreateUseCase {
    PageDefinition handle(String token, String userAgent, String ipAddress);
}
