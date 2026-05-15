package com.zekiloni.george.platform.infrastructure.in.web.dto.campaign;

import com.zekiloni.george.common.infrastructure.in.web.dto.RefDto;
import com.zekiloni.george.platform.domain.service.campaign.TokenGenerationStrategy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.List;

public record CampaignCreateDto(
        String name,
        List<RefDto> flow,
        RefDto serviceAccess,
        TokenGenerationStrategy tokenStrategy,
        int tokenLength,
        String messageTemplate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime scheduledAt,
        MultipartFile file,
        // ISO-3166-1 alpha-2 country codes whose visitors are blocked at the
        // gate. Empty / null = no geo block. UI multi-selects from the bundled
        // country list (libs/domain/.../country/countries.ts).
        List<String> blockedCountries
) {}
