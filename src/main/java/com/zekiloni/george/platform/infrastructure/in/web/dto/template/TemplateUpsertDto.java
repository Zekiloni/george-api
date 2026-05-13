package com.zekiloni.george.platform.infrastructure.in.web.dto.template;

import com.zekiloni.george.platform.domain.model.template.TemplateStep;
import com.zekiloni.george.platform.domain.model.template.TemplateVariable;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record TemplateUpsertDto(
        @NotBlank String name,
        String description,
        String thumbnailUrl,
        List<TemplateStep> steps,
        List<TemplateVariable> variables,
        /** Only honored by the admin publish endpoint; ignored for createOwned/update. */
        String categoryId
) {}
