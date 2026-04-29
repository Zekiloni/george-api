package com.zekiloni.george.platform.infrastructure.in.web;

import com.zekiloni.george.platform.application.port.in.campaign.CampaignCreateUseCase;
import com.zekiloni.george.platform.application.port.in.campaign.CampaignQueryUseCase;
import com.zekiloni.george.platform.application.port.in.campaign.CampaignUpdateUseCase;
import com.zekiloni.george.platform.infrastructure.in.web.dto.campaign.CampaignCreateDto;
import com.zekiloni.george.platform.infrastructure.in.web.mapper.CampaignDto;
import com.zekiloni.george.platform.infrastructure.in.web.mapper.CampaignDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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

    @GetMapping("/{id}")
    public ResponseEntity<CampaignDto> findById(@PathVariable String id) {
        return queryUseCase.findById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
