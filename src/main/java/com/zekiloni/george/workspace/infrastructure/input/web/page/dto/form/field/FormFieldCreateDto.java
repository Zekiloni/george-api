package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field;

import com.zekiloni.george.workspace.domain.page.form.field.FieldType;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.metadata.FieldMetadataDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Base DTO for creating FormField.
 * Does not include id, createdAt, updatedAt fields.
 * Use SuperBuilder for proper inheritance support.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormFieldCreateDto {
    protected String fieldName;
    protected String label;
    protected FieldType type;
    protected String placeholder;
    protected String helpText;
    protected String defaultValue;
    protected Integer displayOrder;
    protected Boolean required;
    protected Boolean isReadOnly;
    protected Boolean isHidden;
    protected FieldMetadataDto metadata;

    // Relationships
    protected List<FieldValidatorCreateDto> validators;
    protected List<FieldOptionCreateDto> options;
    protected List<FormFieldCreateDto> field;
}

