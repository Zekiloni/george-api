package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field;

import com.zekiloni.george.workspace.domain.page.form.field.FieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for FormField entity.
 * Used for API requests and responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormFieldDTO {

    private Long id;
    private String fieldName;
    private String label;
    private FieldType type;
    private String placeholder;
    private String helpText;
    private String defaultValue;
    private Integer displayOrder;
    private Boolean isRequired;
    private Boolean isActive;
    private Boolean isReadOnly;
    private Boolean isHidden;
    private String customAttributes;

    // Relationships
    private List<FieldValidatorDTO> validators;
    private List<FieldOptionDTO> options;
    private List<FormFieldDTO> field;
}

