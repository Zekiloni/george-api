package com.zekiloni.george.platform.infrastructure.in.web.dto.page;

import com.zekiloni.george.platform.domain.model.page.TemplateSource;
import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageTemplateDto {
    private String id;
    private String title;
    private String slug;
    private String description;
    private String keywords;
    private String faviconUrl;
    private TemplateSource source;
    private PageDefinition definition;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
