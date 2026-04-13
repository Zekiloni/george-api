package com.zekiloni.george.workspace.domain.page.form.field;

import com.zekiloni.george.workspace.domain.page.form.field.metadata.FieldMetadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(exclude = {"validators", "options", "field"})
public class FormField {
    private String id;
    private String fieldName;
    private String label;
    private FieldType type;
    private String placeholder;
    private String helpText;
    private String defaultValue;
    private Integer displayOrder;
    private Boolean required;
    private Boolean readonly = false;
    private Boolean hidden = false;
    private FieldMetadata metadata;
    @Builder.Default
    private List<FieldValidator> validators = new ArrayList<>();
    @Builder.Default
    private List<FieldOption> options = new ArrayList<>();
    @Builder.Default
    private List<FormField> field = new ArrayList<>();
    private FormField parentField;
    protected OffsetDateTime createdAt;
    protected OffsetDateTime updatedAt;
}

