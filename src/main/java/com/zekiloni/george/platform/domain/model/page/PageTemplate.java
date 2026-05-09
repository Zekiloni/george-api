package com.zekiloni.george.platform.domain.model.page;

import com.zekiloni.george.platform.domain.model.page.template.TemplateManifest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

// BUILTIN templates are seeded by DevPageTemplateLoader from
// src/main/resources/page-templates/<id>/{template.html, template.css, manifest.json}.
// USER templates come from the API and are never overwritten by the loader.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PageTemplate extends PageContent {
    private TemplateSource source;
    private TemplateManifest manifest;
}
