package com.zekiloni.george.workspace.domain.page.form.field.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepeatField implements FieldMetadata {
    private Integer minInstances = 1;
    private Integer maxInstances = null;
    private Boolean allowAdd = true;
    private Boolean allowRemove = true;
    private Boolean allowReorder = false;
    private String addButtonLabel = "Add";
    private String removeButtonLabel = "Remove";
    private Boolean showCounter = true;
}

