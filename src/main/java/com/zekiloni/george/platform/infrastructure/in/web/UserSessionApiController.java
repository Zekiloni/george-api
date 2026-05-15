package com.zekiloni.george.platform.infrastructure.in.web;

import com.zekiloni.george.platform.application.port.in.campaign.OutreachKickUseCase;
import com.zekiloni.george.platform.application.port.in.campaign.UserSessionCreateUseCase;
import com.zekiloni.george.platform.application.port.in.campaign.UserSessionCreateUseCase.Enrichment;
import com.zekiloni.george.platform.application.port.in.campaign.UserSessionCreateUseCase.Result;
import com.zekiloni.george.platform.application.port.in.campaign.UserSessionSubmitUseCase;
import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.zekiloni.george.common.infrastructure.in.web.util.RequestHeaderUtil.getIpAddress;
import static com.zekiloni.george.common.infrastructure.in.web.util.RequestHeaderUtil.getUserAgent;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.base.path:/api/v1}/user-session")
public class UserSessionApiController {
    private final UserSessionCreateUseCase createUseCase;
    private final UserSessionSubmitUseCase submitUseCase;
    private final OutreachKickUseCase kickUseCase;

    @PostMapping("/{token}")
    public ResponseEntity<UserSessionBootstrapDto> create(@PathVariable String token,
                                                          @RequestBody(required = false) EnrichmentDto body,
                                                          HttpServletRequest request) {
        Enrichment enrichment = body == null ? Enrichment.empty() : body.toDomain();
        Result result = createUseCase.handle(token, getUserAgent(request), getIpAddress(request), enrichment);
        return ResponseEntity.ok(new UserSessionBootstrapDto(
                result.sessionId(),
                result.wsToken(),
                result.sessionKey(),
                result.currentStep(),
                result.totalSteps(),
                result.pageDefinition()));
    }

    /**
     * Posted by the simulator's pre-render security gate. All fields optional;
     * a missing body means "no enrichment available" (dev mode without the
     * GeoLite2/IPQS stack wired up) and the session row's geo/flag columns
     * stay null.
     */
    public record EnrichmentDto(
            List<String> flags,
            String country,
            String city,
            Integer asn,
            String asnOrg,
            Integer riskScore) {

        public Enrichment toDomain() {
            return new Enrichment(
                    flags == null ? List.of() : flags,
                    country, city, asn, asnOrg, riskScore);
        }
    }

    @PostMapping("/{token}/kick")
    public ResponseEntity<Void> kick(@PathVariable String token, @RequestBody KickRequestDto body) {
        kickUseCase.kick(token, body.reason());
        return ResponseEntity.noContent().build();
    }

    public record KickRequestDto(String reason) {}

    @PostMapping("/{wsToken}/submit")
    public ResponseEntity<SubmitResponseDto> submit(@PathVariable String wsToken,
                                                    @RequestBody Map<String, Object> formData) {
        UserSessionSubmitUseCase.Result result = submitUseCase.handle(wsToken, formData);
        if (!result.accepted()) {
            return ResponseEntity.status(409).body(new SubmitResponseDto(
                    result.sessionId(), false, false, result.currentStep(), result.totalSteps(), null));
        }
        return ResponseEntity.ok(new SubmitResponseDto(
                result.sessionId(),
                true,
                result.complete(),
                result.currentStep(),
                result.totalSteps(),
                result.nextPageDefinition()));
    }

    public record UserSessionBootstrapDto(
            String sessionId,
            String wsToken,
            // Base64 AES-256-GCM key — visitor client uses this to encrypt
            // outgoing payloads and decrypt operator commands. Server never
            // sees plaintext after the bootstrap response.
            String sessionKey,
            int currentStep,
            int totalSteps,
            PageDefinition pageDefinition) {}

    public record SubmitResponseDto(
            String sessionId,
            boolean accepted,
            boolean complete,
            int currentStep,
            int totalSteps,
            PageDefinition nextPageDefinition) {}
}
