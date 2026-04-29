package com.zekiloni.george.platform.infrastructure.in.web.dto.campaign;

import com.zekiloni.george.common.infrastructure.in.web.dto.RefDto;
import com.zekiloni.george.platform.domain.service.campaign.TokenGenerationStrategy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;

public record CampaignCreateDto(String name, RefDto page, RefDto serviceAccess,
                                TokenGenerationStrategy tokenStrategy, int tokenLength,
                                String messageTemplate,
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime scheduledAt,
                                MultipartFile file) {
}
