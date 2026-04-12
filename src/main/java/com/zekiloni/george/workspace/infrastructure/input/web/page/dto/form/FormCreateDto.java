package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form;

import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.FormFieldCreateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating FormConfig.
 * Does not include id, createdAt, updatedAt fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormCreateDto {
    private String name;
    private String title;
    private String description;
    private String successMessage;
    private String errorMessage;
    private String redirectUrlOnSuccess;
    private Boolean sendConfirmationEmail;
    private Boolean showProgressBar;
    private Boolean enableCaptcha;

    // Relationships
    private List<FormFieldCreateDto> field;
}

