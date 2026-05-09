package com.zekiloni.george.platform.infrastructure.in.web;

import com.zekiloni.george.platform.application.port.in.campaign.CampaignCreateUseCase;
import com.zekiloni.george.platform.application.port.in.campaign.CampaignQueryUseCase;
import com.zekiloni.george.platform.application.port.in.campaign.CampaignUpdateUseCase;
import com.zekiloni.george.platform.application.port.out.campaign.OutreachRepositoryPort;
import com.zekiloni.george.platform.application.port.out.campaign.UserSessionRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.outreach.OutreachStatus;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;
import com.zekiloni.george.platform.infrastructure.in.web.dto.campaign.CampaignCreateDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.campaign.CampaignSessionDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.campaign.CampaignStatsDto;
import com.zekiloni.george.platform.infrastructure.in.web.mapper.CampaignDto;
import com.zekiloni.george.platform.infrastructure.in.web.mapper.CampaignDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.base.path:/api/v1}/campaign")
public class CampaignApiController {
    private final CampaignCreateUseCase createUseCase;
    private final CampaignQueryUseCase queryUseCase;
    private final CampaignUpdateUseCase updateUseCase;
    private final CampaignDtoMapper mapper;
    private final UserSessionRepositoryPort sessionRepository;
    private final OutreachRepositoryPort outreachRepository;

    @PreAuthorize("@serviceAccessQueryUseCase.hasActiveAccess(T(com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification).SMTP)" +
            "or @serviceAccessQueryUseCase.hasActiveAccess(T(com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification).GSM)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createCampaign(@ModelAttribute @Valid CampaignCreateDto campaignCreate) throws IOException {
        createUseCase.handle(mapper.toDomain(campaignCreate), campaignCreate.file().getInputStream());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<CampaignDto>> findAll(Pageable pageable) {
        return ResponseEntity.ok(queryUseCase.findAll(pageable).map(mapper::toDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampaignDto> findById(@PathVariable String id) {
        return queryUseCase.findById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Aggregate funnel for the campaign-detail overview tiles. Reads outreach
    // counts (delivery funnel) + session counts (visitor outcomes) and derives
    // a conversion rate.
    @GetMapping("/{id}/stats")
    public ResponseEntity<CampaignStatsDto> stats(@PathVariable String id) {
        Map<OutreachStatus, Long> outreachCounts = outreachRepository.countByCampaignGroupedByStatus(id);
        Map<UserSessionStatus, Long> sessionCounts = sessionRepository.countByCampaignGroupedByStatus(id);

        long total = outreachCounts.values().stream().mapToLong(Long::longValue).sum();
        long sent = outreachCounts.getOrDefault(OutreachStatus.SENT, 0L)
                + outreachCounts.getOrDefault(OutreachStatus.VISITED, 0L)
                + outreachCounts.getOrDefault(OutreachStatus.COMPLETED, 0L)
                + outreachCounts.getOrDefault(OutreachStatus.ABANDONED, 0L);
        long visited = outreachCounts.getOrDefault(OutreachStatus.VISITED, 0L)
                + outreachCounts.getOrDefault(OutreachStatus.COMPLETED, 0L)
                + outreachCounts.getOrDefault(OutreachStatus.ABANDONED, 0L);
        long completedOutreach = outreachCounts.getOrDefault(OutreachStatus.COMPLETED, 0L);

        long completedSessions = sessionCounts.getOrDefault(UserSessionStatus.COMPLETED, 0L);
        double rate = sent == 0 ? 0d : ((double) completedSessions) / sent;

        return ResponseEntity.ok(new CampaignStatsDto(
                total,
                sent,
                visited,
                completedOutreach,
                outreachCounts.getOrDefault(OutreachStatus.ABANDONED, 0L),
                sessionCounts.getOrDefault(UserSessionStatus.ACTIVE, 0L),
                sessionCounts.getOrDefault(UserSessionStatus.IDLE, 0L),
                completedSessions,
                sessionCounts.getOrDefault(UserSessionStatus.ABANDONED, 0L),
                sessionCounts.getOrDefault(UserSessionStatus.BLOCKED, 0L),
                rate
        ));
    }

    // DB-backed paginated list, including terminal sessions. The operator's
    // /operator/sessions endpoint only sees the in-memory registry — this one
    // hands you the full history per campaign.
    @GetMapping("/{id}/sessions")
    public ResponseEntity<Page<CampaignSessionDto>> sessions(
            @PathVariable String id,
            @RequestParam(required = false) Set<UserSessionStatus> status,
            Pageable pageable
    ) {
        Set<UserSessionStatus> effective = (status == null || status.isEmpty()) ? null : EnumSet.copyOf(status);
        Page<UserSession> page = sessionRepository.findByCampaignId(id, effective, pageable);
        return ResponseEntity.ok(page.map(s -> new CampaignSessionDto(
                s.getId(),
                s.getStatus(),
                s.getIpAddress(),
                s.getUserAgent(),
                s.getFingerprint(),
                s.getCurrentStep(),
                s.getViewCount(),
                s.getCreatedAt(),
                s.getLastActivityAt()
        )));
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<CampaignDto> pause(@PathVariable String id) {
        return ResponseEntity.ok(mapper.toDto(updateUseCase.pause(id)));
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<CampaignDto> resume(@PathVariable String id) {
        return ResponseEntity.ok(mapper.toDto(updateUseCase.resume(id)));
    }

    @PostMapping("/{id}/abort")
    public ResponseEntity<CampaignDto> abort(@PathVariable String id) {
        return ResponseEntity.ok(mapper.toDto(updateUseCase.abort(id)));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<CampaignDto> complete(@PathVariable String id) {
        return ResponseEntity.ok(mapper.toDto(updateUseCase.complete(id)));
    }
}
