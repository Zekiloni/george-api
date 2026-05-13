package com.zekiloni.george.platform.domain.model.template;

import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One stop in a template's flow — a page definition plus its position in
 * the sequence. Stored as a JSONB array element inside {@code template.steps}
 * so it's never queried individually; the application clones the whole
 * ordered list into tenant-owned Pages at campaign-create time.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateStep {
    /** Optional friendly name for the step shown in the library detail view. */
    private String name;
    /** 0-based position in the flow. */
    private int order;
    private PageDefinition definition;
}
