package com.zekiloni.george.workspace.domain.campaign.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a form submission.
 * Stores submitted form data with references to the original form configuration.
 */
@Entity
@Table(name = "form_submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_config_id", nullable = false)
    private FormConfig formConfig;

    @Column(columnDefinition = "LONGTEXT")
    private String submissionData; // JSON format containing field names and values

    @Column(name = "submitted_by")
    private String submittedBy; // Can be anonymous or user email

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "status")
    private String status; // PENDING, CONFIRMED, REJECTED, PROCESSED

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        submittedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

