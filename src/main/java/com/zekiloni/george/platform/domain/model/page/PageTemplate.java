package com.zekiloni.george.platform.domain.model.page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Inert page builder definition that an admin can clone into a {@link Page}.
 * BUILTIN templates are seeded by {@code DevPageTemplateLoader} from
 * {@code src/main/resources/page-templates/*.html}; USER templates come from
 * the API and are never overwritten by the loader.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PageTemplate extends PageContent {
    private TemplateSource source;
}
