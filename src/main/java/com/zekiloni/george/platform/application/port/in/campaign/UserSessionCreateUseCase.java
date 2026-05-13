package com.zekiloni.george.platform.application.port.in.campaign;

import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;

public interface UserSessionCreateUseCase {

    Result handle(String token, String userAgent, String ipAddress);

    record Result(
            String sessionId,
            String wsToken,
            // Base64-encoded AES-256-GCM key. Both ends use it for E2E payload
            // encryption — server never decrypts visitor↔operator payloads.
            String sessionKey,
            int currentStep,
            int totalSteps,
            PageDefinition pageDefinition) {}
}
