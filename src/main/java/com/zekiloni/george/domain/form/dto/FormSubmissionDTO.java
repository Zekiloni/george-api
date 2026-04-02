package com.zekiloni.george.domain.form.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for FormSubmission entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormSubmissionDTO {

    private Long id;
    private Long formConfigId;
    private String submissionData;
    private String submittedBy;
    private LocalDateTime submittedAt;
    private String ipAddress;
    private String userAgent;
    private String status;
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // For response
    private String formName;
}

