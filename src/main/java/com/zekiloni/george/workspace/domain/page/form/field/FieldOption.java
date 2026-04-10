package com.zekiloni.george.workspace.domain.page.form.field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing an option for select, radio, or multi-select fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldOption {
    private String id;
    private String label;
    private String value;
    private Integer displayOrder;
    private Boolean isDefault = false;
    private Boolean isActive = true;
    private String description;
}

