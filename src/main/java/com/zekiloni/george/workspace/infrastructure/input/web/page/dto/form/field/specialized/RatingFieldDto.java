package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.specialized;

import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.FormFieldDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * View DTO for RatingField.
 * Extends FormFieldDto to inherit all base field properties (id, createdAt, updatedAt, etc.).
 * Adds specialized rating field properties.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RatingFieldDto extends FormFieldDto {
    private String ratingType;
    private Integer maxRating;
    private Boolean allowHalfRating;
    private Boolean showLabels;
    private String labelPoor;
    private String labelExcellent;
}

