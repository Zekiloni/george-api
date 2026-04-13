package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View DTO for RepeatField.
 * Extends FormFieldDto to inherit all base field properties (id, createdAt, updatedAt, etc.).
 * Adds specialized repeat field properties.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepeatFieldDto implements FieldMetadataDto{
    private Integer minInstances;
    private Integer maxInstances;
    private Boolean allowAdd;
    private Boolean allowRemove;
    private Boolean allowReorder;
    private String addButtonLabel;
    private String removeButtonLabel;
    private Boolean showCounter;
    private String previewFields;
}

