package com.zekiloni.george.platform.application.port.in.campaign;

import java.util.Map;

public interface UserSessionSubmitUseCase {

    Result handle(String wsToken, Map<String, Object> formData);

    record Result(String sessionId, boolean accepted) {}
}
