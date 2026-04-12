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
public class FormEntity extends TenantEntity {
    @Column(name = "form_name", nullable = false)
    private String name;

    @Column(name = "form_title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "success_message")
    private String successMessage;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "redirect_url_on_success")
    private String redirectUrlOnSuccess;

    @Column(name = "send_confirmation_email", nullable = false)
    private Boolean sendConfirmationEmail = false;

    @Column(name = "show_progress_bar", nullable = false)
    private Boolean showProgressBar = false;

    @Column(name = "enable_captcha", nullable = false)
    private Boolean enableCaptcha = false;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "formConfig")
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<FormFieldEntity> field = new ArrayList<>();
}
