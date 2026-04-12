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
@ToString(exclude = {"field"})
public class FormConfig {
    private String id;
    private String name;
    private String title;
    private String description;
    private String successMessage;
    private String errorMessage;
    private String redirectUrlOnSuccess;
    private Boolean sendConfirmationEmail = false;
    private Boolean showProgressBar = false;
    private Boolean enableCaptcha = false;
    @Builder.Default
    private List<FormField> field = new ArrayList<>();
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

