package com.zekiloni.george.workspace.infrastructure.in.web.page.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for FieldOption entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldOptionDTO {

    private Long id;
    private String label;
    private String value;
    private Integer displayOrder;
    private Boolean isDefault;
    private Boolean isActive;
    private String description;
}

