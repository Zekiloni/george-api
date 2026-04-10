package com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.TenantEntity;
import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.field.FormFieldEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "form_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"fields"})
public class FormConfigEntity extends TenantEntity {

    @Column(name = "form_name", nullable = false)
    private String formName;

    @Column(name = "form_title")
    private String formTitle;

    @Column(name = "description")
    private String description;

    @Column(name = "success_message")
    private String successMessage;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "redirect_url_on_success")
    private String redirectUrlOnSuccess;

    @Column(name = "submission_email")
    private String submissionEmail;

    @Column(name = "send_confirmation_email", nullable = false)
    private Boolean sendConfirmationEmail = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @Column(name = "show_progress_bar", nullable = false)
    private Boolean showProgressBar = false;

    @Column(name = "show_section_numbers", nullable = false)
    private Boolean showSectionNumbers = true;

    @Column(name = "save_drafts", nullable = false)
    private Boolean saveDrafts = false;

    @Column(name = "enable_captcha", nullable = false)
    private Boolean enableCaptcha = false;

    @Column(name = "css_customization", columnDefinition = "TEXT")
    private String cssCustomization;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "formConfig")
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<FormFieldEntity> fields = new ArrayList<>();
}
