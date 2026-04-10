package com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.field;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("RATING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RatingFieldEntity extends FormFieldEntity {

    @Column(name = "rating_type")
    private String ratingType;

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

