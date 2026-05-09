package com.zekiloni.george.platform.domain.model.page.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// One marketer-editable field declared by a template's manifest.
// `key` must match a {{key}} placeholder in the template HTML.
// `showWhen` (optional) names a BOOLEAN field that gates this one's visibility.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateField {
    private String key;
    private String label;
    private FieldType type;
    private String defaultValue;
    private boolean required;
    private List<String> options;
    private String showWhen;
}
