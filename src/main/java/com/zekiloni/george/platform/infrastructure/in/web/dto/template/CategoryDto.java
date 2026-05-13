package com.zekiloni.george.platform.infrastructure.in.web.dto.template;

import java.util.List;

public record CategoryDto(
        String id,
        String name,
        String slug,
        String parentId,
        int sortOrder,
        List<CategoryDto> children
) {}
