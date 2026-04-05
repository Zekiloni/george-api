package com.zekiloni.george.workspace.domain.campaign.form.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for specialized RatingField.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingFieldDTO {

    private String ratingType;
    private Integer maxRating;
    private Boolean allowHalfRating;
    private Boolean showLabels;
    private String labelPoor;
    private String labelExcellent;
}

