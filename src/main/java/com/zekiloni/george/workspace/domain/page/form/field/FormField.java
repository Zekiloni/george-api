package com.zekiloni.george.workspace.domain.page.form.field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"validators", "options", "subFields"})
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
    private Boolean isActive = true;
    private Boolean isReadOnly = false;
    private Boolean isHidden = false;
    private String customAttributes;
    @Builder.Default
    private List<FieldValidator> validators = new ArrayList<>();
    @Builder.Default
    private List<FieldOption> options = new ArrayList<>();
    @Builder.Default
    private List<FormField> subFields = new ArrayList<>();
    private FormField parentField;
}

