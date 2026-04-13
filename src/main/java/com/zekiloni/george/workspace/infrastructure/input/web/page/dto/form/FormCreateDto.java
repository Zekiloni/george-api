package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form;

import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.FormFieldCreateDto;

import java.util.List;

/**
 * DTO for creating FormConfig.
 * Does not include id, createdAt, updatedAt fields.
 *
 * @param field Relationships
 */
public record FormCreateDto(String name, String title, String description, String successMessage, String errorMessage,
                            String redirectUrlOnSuccess, Boolean sendConfirmationEmail, Boolean showProgressBar,
                            Boolean enableCaptcha, List<FormFieldCreateDto> field) {
}

