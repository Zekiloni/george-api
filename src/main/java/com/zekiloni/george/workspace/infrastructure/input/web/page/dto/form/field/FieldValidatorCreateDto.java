package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field;

import com.zekiloni.george.workspace.domain.page.form.field.ValidationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating FieldValidator.
 * Does not include id, createdAt, updatedAt fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldValidatorCreateDto {
    private ValidationType type;
    private Object value;
    private String errorMessage;
}

