package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field;

import com.zekiloni.george.workspace.domain.page.form.field.FieldType;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.metadata.FieldMetadataDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Base DTO for viewing FormField entity.
 * Used for API requests and responses.
 * Includes id, createdAt, updatedAt fields.
 * Use SuperBuilder for proper inheritance support.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormFieldDto {
    protected String id;
    protected OffsetDateTime createdAt;
    protected OffsetDateTime updatedAt;
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
    protected List<FieldValidatorDto> validators;
    protected List<FieldOptionDto> options;
    protected List<FormFieldDto> field;
}

