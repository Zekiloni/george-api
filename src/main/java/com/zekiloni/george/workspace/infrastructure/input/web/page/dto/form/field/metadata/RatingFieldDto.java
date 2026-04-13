package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View DTO for RatingField.
 * Extends FormFieldDto to inherit all base field properties (id, createdAt, updatedAt, etc.).
 * Adds specialized rating field properties.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingFieldDto implements FieldMetadataDto {
    private String ratingType;
    private Integer maxRating;
    private Boolean allowHalfRating;
    private Boolean showLabels;
    private String labelPoor;
    private String labelExcellent;
}

