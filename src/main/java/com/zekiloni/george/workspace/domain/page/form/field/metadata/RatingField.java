package com.zekiloni.george.workspace.domain.page.form.field.metadata;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingField implements FieldMetadata{
    private String ratingType; // "STAR", "EMOJI", "NUMBER", "SMILEY"
    private Integer maxRating = 5;
    private Boolean allowHalfRating = false;
    private Boolean showLabels = false;
    private String labelPoor;
    private String labelExcellent;
}

