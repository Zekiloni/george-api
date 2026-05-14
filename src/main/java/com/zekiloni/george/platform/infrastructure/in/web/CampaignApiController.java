package com.zekiloni.george.platform.infrastructure.in.web;

import com.zekiloni.george.platform.application.port.in.campaign.CampaignCreateUseCase;
import com.zekiloni.george.platform.application.port.in.campaign.CampaignQueryUseCase;
import com.zekiloni.george.platform.application.port.in.campaign.CampaignUpdateUseCase;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;
import com.zekiloni.george.platform.infrastructure.in.web.dto.campaign.CampaignCreateDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.campaign.CampaignResponseDto;
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
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.base.path:/api/v1}/campaign")
public class CampaignApiController {
    private final CampaignCreateUseCase createUseCase;
    private final CampaignQueryUseCase queryUseCase;
    private final CampaignUpdateUseCase updateUseCase;
    private final CampaignDtoMapper mapper;

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

    @GetMapping("/{id}/stats")
    public ResponseEntity<CampaignStatsDto> stats(@PathVariable String id) {
        return ResponseEntity.ok(mapper.toDto(queryUseCase.stats(id)));
    }

    @GetMapping("/{id}/sessions")
    public ResponseEntity<Page<CampaignSessionDto>> sessions(
            @PathVariable String id,
            @RequestParam(required = false) Set<UserSessionStatus> status,
            Pageable pageable
    ) {
        return ResponseEntity.ok(queryUseCase.findSessions(id, status, pageable).map(mapper::toSessionDto));
    }

    @GetMapping("/{id}/responses")
    public ResponseEntity<List<CampaignResponseDto>> responses(@PathVariable String id) {
        return ResponseEntity.ok(queryUseCase.responses(id).stream().map(mapper::toResponseDto).toList());
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
