package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.platform.application.port.in.campaign.UserSessionSubmitUseCase;
import com.zekiloni.george.platform.application.port.out.campaign.UserSessionRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.InteractionType;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserEvent;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;
import com.zekiloni.george.platform.infrastructure.in.ws.session.ConnectedSession;
import com.zekiloni.george.platform.infrastructure.in.ws.session.UserSessionLifecycleService;
import com.zekiloni.george.platform.infrastructure.in.ws.session.UserSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSessionSubmitService implements UserSessionSubmitUseCase {

    private final UserSessionRepositoryPort sessionRepository;
    private final UserSessionRegistry registry;
    private final UserSessionLifecycleService lifecycleService;

    @Override
    public Result handle(String wsToken, Map<String, Object> formData) {
        UserSession session = sessionRepository.findByWsToken(wsToken)
                .orElseThrow(() -> new NoSuchElementException("Session not found for token"));

        UserSessionStatus current = session.getStatus();
        if (current == UserSessionStatus.COMPLETED || current == UserSessionStatus.ABANDONED
                || current == UserSessionStatus.BLOCKED) {
            log.info("Submit rejected: session {} already in terminal status {}", session.getId(), current);
            return new Result(session.getId(), false);
        }

        UserEvent submitEvent = UserEvent.builder()
                .id(UUID.randomUUID().toString())
                .type(InteractionType.SUBMIT)
                .incoming(false)
                .payload(formData)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        ConnectedSession connected = registry.get(session.getId()).orElse(null);
        if (connected != null) {
            connected.appendEvent(submitEvent);
        }

        lifecycleService.markCompleted(session.getId());
        return new Result(session.getId(), true);
    }
}
