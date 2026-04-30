package com.zekiloni.george.platform.infrastructure.in.web;

import com.zekiloni.george.platform.application.port.in.campaign.UserSessionCommandUseCase;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.base.path:/api/v1}/operator/sessions")
@RequiredArgsConstructor
public class OperatorSessionApiController {

    private final UserSessionCommandUseCase commandUseCase;

    @PostMapping("/{sessionId}/commands")
    public ResponseEntity<Void> sendCommand(@PathVariable String sessionId,
                                            @RequestBody UserEvent command) {
        commandUseCase.send(sessionId, command);
        return ResponseEntity.accepted().build();
    }
}
