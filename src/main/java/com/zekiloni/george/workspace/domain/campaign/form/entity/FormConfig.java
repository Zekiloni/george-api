package com.zekiloni.george.workspace.domain.campaign.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Main entity representing a form configuration.
 * Users can create custom forms with various field types and configurations.
 */
@Entity
@Table(name = "form_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"fields"})
public class FormConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String formName;

    @Column(nullable = false)
    private String formTitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String successMessage;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "redirect_url_on_success")
    private String redirectUrlOnSuccess;

    @Column(name = "submission_email")
    private String submissionEmail;

    @Column(name = "send_confirmation_email")
    private Boolean sendConfirmationEmail = false;

    @Column(name = "notification_webhook_url")
    private String notificationWebhookUrl;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_public")
    private Boolean isPublic = false;

    @Column(name = "show_progress_bar")
    private Boolean showProgressBar = false;

    @Column(name = "show_section_numbers")
    private Boolean showSectionNumbers = true;

    @Column(name = "save_drafts")
    private Boolean saveDrafts = false;

    @Column(name = "enable_captcha")
    private Boolean enableCaptcha = false;

    @Column(columnDefinition = "TEXT")
    private String cssCustomization;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    // One-to-Many relationship with form fields
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "form_config_id")
    @OrderBy("displayOrder ASC")
    private List<FormField> fields = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

