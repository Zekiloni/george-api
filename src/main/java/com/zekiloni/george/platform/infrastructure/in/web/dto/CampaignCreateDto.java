package com.zekiloni.george.platform.infrastructure.in.web.dto;

import com.zekiloni.george.common.infrastructure.in.web.dto.RefDto;
import com.zekiloni.george.platform.domain.service.campaign.TokenGenerationStrategy;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;

public record CampaignCreateDto(String name, RefDto page, TokenGenerationStrategy tokenStrategy, int tokenLength,
                                OffsetDateTime scheduledAt, MultipartFile file) {
}
