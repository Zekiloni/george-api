package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating FieldOption.
 * Does not include id, createdAt, updatedAt fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldOptionCreateDto {
    private String label;
    private Object value;
    private Integer displayOrder;
    private Boolean isDefault;
    private String description;
}

