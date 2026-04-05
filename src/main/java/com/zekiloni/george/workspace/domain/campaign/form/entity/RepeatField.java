package com.zekiloni.george.workspace.domain.campaign.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Specialized FormField for repeating field groups.
 * Allows users to add/remove multiple instances of field groups (arrays of fields).
 */
@Entity
@Table(name = "repeat_fields")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("REPEAT")
public class RepeatField extends FormField {

    @Column(name = "min_instances")
    private Integer minInstances = 1;

    @Column(name = "max_instances")
    private Integer maxInstances = null; // null means unlimited

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
    private String previewFields; // Comma-separated field names to show in preview
}

