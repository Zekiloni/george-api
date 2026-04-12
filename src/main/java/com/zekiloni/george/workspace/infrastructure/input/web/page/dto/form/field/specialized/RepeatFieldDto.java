package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.specialized;

import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.FormFieldDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * View DTO for RepeatField.
 * Extends FormFieldDto to inherit all base field properties (id, createdAt, updatedAt, etc.).
 * Adds specialized repeat field properties.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RepeatFieldDto extends FormFieldDto {
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

