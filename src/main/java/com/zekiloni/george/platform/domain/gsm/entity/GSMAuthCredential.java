package com.zekiloni.george.platform.domain.gsm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing authentication credentials for a GSM Box.
 * Supports multiple authentication methods: API keys, OAuth, certificates, etc.
 */
@Entity
@Table(name = "gsm_auth_credentials")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GSMAuthCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gsm_box_config_id", nullable = false)
    private GSMBoxConfig gsmBoxConfig;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GSMAuthType authType;

    @Column(nullable = false)
    private String credentialName;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Credentials (encrypted in production)
    @Column(columnDefinition = "TEXT", nullable = false)
    private String credentialValue; // API key, token, password, etc.

    @Column(columnDefinition = "TEXT")
    private String credentialSecret; // For OAuth, HMAC, etc.

    @Column(columnDefinition = "TEXT")
    private String additionalData; // JSON for extra fields

    @Column(name = "is_primary")
    private Boolean isPrimary = false; // Primary credential to use

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt; // For tokens/certificates

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

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

