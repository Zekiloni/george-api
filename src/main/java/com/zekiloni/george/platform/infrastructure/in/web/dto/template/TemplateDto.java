package com.zekiloni.george.platform.infrastructure.in.web.dto.template;

import com.zekiloni.george.platform.domain.model.template.TemplateStep;
import com.zekiloni.george.platform.domain.model.template.TemplateVariable;

import java.time.OffsetDateTime;
import java.util.List;

public record TemplateDto(
        String id,
        String name,
        String description,
        String thumbnailUrl,
        List<TemplateStep> steps,
        List<TemplateVariable> variables,
        int version,
        long usageCount,
        String categoryId,
        String tenantId,
        boolean isPublic,
        int stepCount,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
