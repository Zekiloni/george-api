package com.zekiloni.george.platform.application.port.in.campaign;

import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;

public interface UserSessionCreateUseCase {

    Result handle(String token, String userAgent, String ipAddress);

    record Result(String sessionId, String wsToken, PageDefinition pageDefinition) {}
}
