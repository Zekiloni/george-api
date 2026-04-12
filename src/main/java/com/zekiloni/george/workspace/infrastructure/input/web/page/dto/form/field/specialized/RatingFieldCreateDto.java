package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.specialized;

import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.FormFieldCreateDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Create DTO for RatingField.
 * Extends FormFieldCreateDto to inherit all base field properties (fieldName, label, etc.).
 * Does not include id, createdAt, updatedAt fields.
 * Adds specialized rating field properties.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RatingFieldCreateDto extends FormFieldCreateDto {
    private String ratingType;
    private Integer maxRating;
    private Boolean allowHalfRating;
    private Boolean showLabels;
    private String labelPoor;
    private String labelExcellent;
}

