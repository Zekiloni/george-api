package com.zekiloni.george.workspace.infrastructure.in.web.page.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for FieldValidator entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldValidatorDTO {

    private Long id;
    private String type;
    private String value;
    private String errorMessage;
    private Boolean isActive;
}

