package com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.field;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("REPEAT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RepeatFieldEntity extends FormFieldEntity {

    @Column(name = "min_instances")
    private Integer minInstances = 1;

    @Column(name = "max_instances")
    private Integer maxInstances;

    @Column(name = "allow_add")
    private Boolean allowAdd = true;

    @Column(name = "allow_remove")
    private Boolean allowRemove = true;

    @Column(name = "allow_reorder")
    private Boolean allowReorder = false;

    @Column(name = "add_button_label")
    private String addButtonLabel = "Add";

    @Column(name = "remove_button_label")
    private String removeButtonLabel = "Remove";

    @Column(name = "show_counter")
    private Boolean showCounter = true;

    @Column(name = "preview_fields")
    private String previewFields;
}

