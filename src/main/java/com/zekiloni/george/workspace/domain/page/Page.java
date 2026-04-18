package com.zekiloni.george.workspace.domain.page;

import com.zekiloni.george.workspace.domain.page.definition.PageDefinition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Page {
    private String id;
    private String title;
    private String slug;
    private String description;
    private String keywords;
    private String faviconUrl;
    private PageStatus status;
    private PageDefinition definition;
    private String createdBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
