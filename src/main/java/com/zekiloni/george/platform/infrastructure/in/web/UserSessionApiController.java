package com.zekiloni.george.platform.infrastructure.in.web;

import com.zekiloni.george.platform.application.port.in.campaign.UserSessionCreateUseCase;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.zekiloni.george.common.infrastructure.in.web.util.RequestHeaderUtil.getIpAddress;
import static com.zekiloni.george.common.infrastructure.in.web.util.RequestHeaderUtil.getUserAgent;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.base.path:/api/v1}/user-session")
public class UserSessionApiController {
    private final UserSessionCreateUseCase createUseCase;

    @GetMapping("/{token}")
    public ResponseEntity<Void> create(@PathVariable String token, HttpServletRequest request) {
        createUseCase.handle(token, getUserAgent(request), getIpAddress(request));
        return ResponseEntity.ok().build();
    }
}
