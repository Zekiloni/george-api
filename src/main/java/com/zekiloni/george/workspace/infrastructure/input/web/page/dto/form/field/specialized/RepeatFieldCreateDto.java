package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.specialized;

import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.FormFieldCreateDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Create DTO for RepeatField.
 * Extends FormFieldCreateDto to inherit all base field properties (fieldName, label, etc.).
 * Does not include id, createdAt, updatedAt fields.
 * Adds specialized repeat field properties.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RepeatFieldCreateDto extends FormFieldCreateDto {
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

