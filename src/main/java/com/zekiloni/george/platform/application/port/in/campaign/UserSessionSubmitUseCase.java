package com.zekiloni.george.platform.application.port.in.campaign;

import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;

import java.util.Map;

public interface UserSessionSubmitUseCase {

    Result handle(String wsToken, Map<String, Object> formData);

    record Result(
            String sessionId,
            boolean accepted,
            boolean complete,
            int currentStep,
            int totalSteps,
            PageDefinition nextPageDefinition) {}
}
