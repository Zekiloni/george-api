package com.zekiloni.george.workspace.domain.page.form;

import com.zekiloni.george.workspace.domain.page.form.field.FormField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"fields"})
public class FormConfig {
    private String id;
    private String formName;
    private String formTitle;
    private String description;
    private String successMessage;
    private String errorMessage;
    private String redirectUrlOnSuccess;
    private String submissionEmail;
    private Boolean sendConfirmationEmail = false;
    private Boolean isActive = true;
    private Boolean isPublic = false;
    private Boolean showProgressBar = false;
    private Boolean showSectionNumbers = true;
    private Boolean saveDrafts = false;
    private Boolean enableCaptcha = false;
    private String cssCustomization;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @Builder.Default
    private List<FormField> fields = new ArrayList<>();
}

