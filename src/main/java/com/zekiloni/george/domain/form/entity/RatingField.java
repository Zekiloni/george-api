package com.zekiloni.george.domain.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Specialized FormField for rating input.
 * Supports star ratings, emoji ratings, and other rating formats.
 */
@Entity
@Table(name = "rating_fields")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("RATING")
public class RatingField extends FormField {

    @Column(name = "rating_type")
    private String ratingType; // "STAR", "EMOJI", "NUMBER", "SMILEY"

    @Column(name = "max_rating")
    private Integer maxRating = 5;

    @Column(name = "allow_half_rating")
    private Boolean allowHalfRating = false;

    @Column(name = "show_labels")
    private Boolean showLabels = false;

    @Column(name = "label_poor")
    private String labelPoor;

    @Column(name = "label_excellent")
    private String labelExcellent;
}

