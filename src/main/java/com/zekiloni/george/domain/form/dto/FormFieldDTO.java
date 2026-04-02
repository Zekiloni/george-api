package com.zekiloni.george.domain.form.dto;

import com.zekiloni.george.domain.form.entity.FieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for FormField entity.
 * Used for API requests and responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormFieldDTO {

    private Long id;
    private String fieldName;
    private String label;
    private FieldType type;
    private String placeholder;
    private String helpText;
    private String defaultValue;
    private Integer displayOrder;
    private Boolean isRequired;
    private Boolean isActive;
    private Boolean isReadOnly;
    private Boolean isHidden;
    private String customAttributes;

    // Relationships
    private List<FieldValidatorDTO> validators;
    private List<FieldOptionDTO> options;
    private List<FormFieldDTO> subFields;

    // Specialized field properties
    private PasswordFieldDTO passwordField;
    private AddressFieldDTO addressField;
    private PhoneFieldDTO phoneField;
    private CreditCardFieldDTO creditCardField;
    private RatingFieldDTO ratingField;
    private RepeatFieldDTO repeatField;
}

