package com.zekiloni.george.platform.infrastructure.in.web;

import com.zekiloni.george.platform.application.port.in.campaign.UserSessionCreateUseCase;
import com.zekiloni.george.platform.application.port.in.campaign.UserSessionCreateUseCase.Result;
import com.zekiloni.george.platform.application.port.in.campaign.UserSessionSubmitUseCase;
import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.zekiloni.george.common.infrastructure.in.web.util.RequestHeaderUtil.getIpAddress;
import static com.zekiloni.george.common.infrastructure.in.web.util.RequestHeaderUtil.getUserAgent;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.base.path:/api/v1}/user-session")
public class UserSessionApiController {
    private final UserSessionCreateUseCase createUseCase;
    private final UserSessionSubmitUseCase submitUseCase;

    @GetMapping("/{token}")
    public ResponseEntity<UserSessionBootstrapDto> create(@PathVariable String token, HttpServletRequest request) {
        Result result = createUseCase.handle(token, getUserAgent(request), getIpAddress(request));
        return ResponseEntity.ok(new UserSessionBootstrapDto(
                result.sessionId(),
                result.wsToken(),
                result.pageDefinition()));
    }

    @PostMapping("/{wsToken}/submit")
    public ResponseEntity<SubmitResponseDto> submit(@PathVariable String wsToken,
                                                    @RequestBody Map<String, Object> formData) {
        UserSessionSubmitUseCase.Result result = submitUseCase.handle(wsToken, formData);
        if (!result.accepted()) {
            return ResponseEntity.status(409).body(new SubmitResponseDto(result.sessionId(), false));
        }
        return ResponseEntity.ok(new SubmitResponseDto(result.sessionId(), true));
    }

    public record UserSessionBootstrapDto(String sessionId, String wsToken, PageDefinition pageDefinition) {}

    public record SubmitResponseDto(String sessionId, boolean accepted) {}
}
