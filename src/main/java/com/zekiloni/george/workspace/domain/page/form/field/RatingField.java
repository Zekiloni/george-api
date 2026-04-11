package com.zekiloni.george.workspace.domain.page.form.field;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatingField extends FormField {
    private String ratingType; // "STAR", "EMOJI", "NUMBER", "SMILEY"
    private Integer maxRating = 5;
    private Boolean allowHalfRating = false;
    private Boolean showLabels = false;
    private String labelPoor;
    private String labelExcellent;
}

