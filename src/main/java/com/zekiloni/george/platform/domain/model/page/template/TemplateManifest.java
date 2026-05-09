package com.zekiloni.george.platform.domain.model.page.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// Declares the contract between a template and its page builder form:
//   - `fields` are the marketer-editable variables (each maps to {{key}} in HTML).
//   - `form` is the lead-capture form embedded in the rendered page (optional).
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateManifest {
    @Builder.Default
    private List<TemplateField> fields = new ArrayList<>();
    private FormConfig form;
}
