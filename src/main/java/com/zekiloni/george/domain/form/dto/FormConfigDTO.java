package com.zekiloni.george.domain.form.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for FormConfig entity.
 * Used for API requests and responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormConfigDTO {

    private Long id;
    private String formName;
    private String formTitle;
    private String description;
    private String successMessage;
    private String errorMessage;
    private String redirectUrlOnSuccess;
    private String submissionEmail;
    private Boolean sendConfirmationEmail;
    private String notificationWebhookUrl;
    private Boolean isActive;
    private Boolean isPublic;
    private Boolean showProgressBar;
    private Boolean showSectionNumbers;
    private Boolean saveDrafts;
    private Boolean enableCaptcha;
    private String cssCustomization;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Relationships
    private List<FormFieldDTO> fields;
}

