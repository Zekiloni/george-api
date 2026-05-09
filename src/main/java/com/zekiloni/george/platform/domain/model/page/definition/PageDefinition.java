package com.zekiloni.george.platform.domain.model.page.definition;

import com.zekiloni.george.platform.domain.model.page.template.FormConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

// Raw HTML/CSS template body + the marketer's filled-in variable values.
// Variables are substituted at render time via {{key}} placeholders.
// `form` is copied from the template manifest at clone time so the page
// keeps working even if the source template's manifest later changes.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageDefinition {
    private String html;
    private String css;
    @Builder.Default
    private Map<String, String> variables = new HashMap<>();
    private FormConfig form;
}
