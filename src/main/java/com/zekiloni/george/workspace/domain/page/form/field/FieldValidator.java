package com.zekiloni.george.workspace.domain.page.form.field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldValidator {
    private String id;
    private ValidationType type;
    private String value;
    private String errorMessage;
    private Boolean isActive = true;
}

