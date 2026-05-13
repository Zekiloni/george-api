package com.zekiloni.george.platform.domain.model.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Declared placeholder a template asks the user to fill in at instantiation
 * time. Each step's page definition may reference these by name via
 * {@code {{variable}}} substitution; the value supplied by the user is
 * substituted before the cloned Page is saved.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateVariable {
    /** Substitution key — matches the {@code {{name}}} placeholder in the HTML. */
    private String name;
    /** Human-readable label for the "Use template" dialog field. */
    private String label;
    /** Field type hint: TEXT, URL, EMAIL, COLOR, IMAGE, NUMBER, BOOLEAN. */
    private String type;
    private String defaultValue;
    private boolean required;
}
