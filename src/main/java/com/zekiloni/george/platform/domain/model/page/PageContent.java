package com.zekiloni.george.platform.domain.model.page;

import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

/**
 * Fields shared by anything that carries a page builder definition. {@link Page}
 * adds the live-page lifecycle (status, createdBy); {@link PageTemplate} adds a
 * source flag (BUILTIN/USER) and is inert until cloned into a Page.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class PageContent {
    private String id;
    private String title;
    private String slug;
    private String description;
    private String keywords;
    private String faviconUrl;
    private PageDefinition definition;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
