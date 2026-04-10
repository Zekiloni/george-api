package com.zekiloni.george.workspace.infrastructure.in.web.page.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for specialized RepeatField.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepeatFieldDTO {

    private Integer minInstances;
    private Integer maxInstances;
    private Boolean allowAdd;
    private Boolean allowRemove;
    private Boolean allowReorder;
    private String addButtonLabel;
    private String removeButtonLabel;
    private Boolean showCounter;
    private String previewFields;
}

