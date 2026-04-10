package com.zekiloni.george.workspace.domain.page.form.field;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Specialized FormField for repeating field groups.
 * Allows users to add/remove multiple instances of field groups (arrays of fields).
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("REPEAT")
public class RepeatField extends FormField {
    private Integer minInstances = 1;
    private Integer maxInstances = null;
    private Boolean allowAdd = true;
    private Boolean allowRemove = true;
    private Boolean allowReorder = false;
    private String addButtonLabel = "Add";
    private String removeButtonLabel = "Remove";
    private Boolean showCounter = true;
    private String previewFields; // Comma-separated field names to show in preview
}

