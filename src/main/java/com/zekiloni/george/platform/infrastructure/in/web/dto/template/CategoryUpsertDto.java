package com.zekiloni.george.platform.infrastructure.in.web.dto.template;

import jakarta.validation.constraints.NotBlank;

public record CategoryUpsertDto(
        @NotBlank String name,
        @NotBlank String slug,
        String parentId,
        int sortOrder
) {}
