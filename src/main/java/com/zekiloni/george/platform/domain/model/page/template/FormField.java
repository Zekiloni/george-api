package com.zekiloni.george.platform.domain.model.page.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// One field on the lead-capture form embedded in the rendered page.
// `type` is an HTML input type (text, email, tel, …).
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormField {
    private String name;
    private String label;
    private String type;
    private boolean required;
    private String placeholder;
}
