package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field;

import com.zekiloni.george.workspace.domain.page.form.field.FieldType;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.metadata.FieldMetadataDto;

import java.util.List;

/**
 * Base DTO for creating FormField.
 * Does not include id, createdAt, updatedAt fields.
 * Use SuperBuilder for proper inheritance support.
 *
 * @param validators Relationships
 */
public record FormFieldCreateDto(String fieldName, String label, FieldType type, String placeholder, String helpText,
                                 String defaultValue, Integer displayOrder, Boolean required, Boolean readonly,
                                 Boolean hidden, FieldMetadataDto metadata, List<FieldValidatorCreateDto> validators,
                                 List<FieldOptionCreateDto> options, List<FormFieldCreateDto> field) {
}

