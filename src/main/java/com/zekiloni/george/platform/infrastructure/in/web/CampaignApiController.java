package com.zekiloni.george.platform.infrastructure.in.web;

import com.zekiloni.george.platform.application.port.in.campaign.CampaignCreateUseCase;
import com.zekiloni.george.platform.infrastructure.in.web.dto.CampaignCreateDto;
import com.zekiloni.george.platform.infrastructure.in.web.mapper.CampaignDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.base.path:/api/v1}/campaign")
public class CampaignApiController {
    private final CampaignCreateUseCase createUseCase;
    private final CampaignDtoMapper mapper;

    @PreAuthorize("@serviceAccessQueryUseCase.hasActiveAccess(T(com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification).SMTP)" +
            "or @serviceAccessQueryUseCase.hasActiveAccess(T(com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification).GSM)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createCampaign(@ModelAttribute @Valid CampaignCreateDto campaignCreate) throws IOException {
        createUseCase.handle(mapper.toDomain(campaignCreate), campaignCreate.file().getInputStream());
        return ResponseEntity.ok().build();
    }
}
