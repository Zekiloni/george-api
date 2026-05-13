package com.zekiloni.george.platform.infrastructure.in.web.dto.template;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record CreateCampaignFromTemplateDto(
        @NotBlank String campaignName,
        /** Variable name → user-supplied value. Nullable when the template declares no variables. */
        Map<String, String> variables
) {}
