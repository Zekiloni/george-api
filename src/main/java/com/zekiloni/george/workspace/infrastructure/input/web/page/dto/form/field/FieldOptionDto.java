package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * DTO for viewing FieldOption.
 * Includes id, createdAt, updatedAt fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldOptionDto {
    private String id;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String label;
    private Object value;
    private Integer displayOrder;
    private Boolean isDefault;
    private String description;
}

