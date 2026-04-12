package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form;

import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.FormFieldDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO for viewing FormConfig.
 * Includes id, createdAt, updatedAt fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormDto {
    private String id;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
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
    private List<FormFieldDto> field;
}

