package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field;

import com.zekiloni.george.workspace.domain.page.form.field.ValidationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * DTO for viewing FieldValidator.
 * Includes id, createdAt, updatedAt fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldValidatorDto {
    private String id;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private ValidationType type;
    private Object value;
    private String errorMessage;
}

