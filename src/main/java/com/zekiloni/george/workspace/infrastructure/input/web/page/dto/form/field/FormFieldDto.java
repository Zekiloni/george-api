package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field;

import com.zekiloni.george.workspace.domain.page.form.field.FieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO for viewing FormField entity.
 * Used for API requests and responses.
 * Includes id, createdAt, updatedAt fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormFieldDto {
    private String id;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String fieldName;
    private String label;
    private FieldType type;
    private String placeholder;
    private String helpText;
    private String defaultValue;
    private Integer displayOrder;
    private Boolean required;
    private Boolean isReadOnly;
    private Boolean isHidden;
    private String customAttributes;

    // Relationships
    private List<FieldValidatorDto> validators;
    private List<FieldOptionDto> options;
    private List<FormFieldDto> field;
}

