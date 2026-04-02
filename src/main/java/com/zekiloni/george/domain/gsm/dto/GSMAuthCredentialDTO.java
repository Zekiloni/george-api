package com.zekiloni.george.domain.gsm.dto;

import com.zekiloni.george.domain.gsm.entity.GSMAuthType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for GSMAuthCredential entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GSMAuthCredentialDTO {

    private Long id;
    private Long gsmBoxConfigId;
    private GSMAuthType authType;
    private String credentialName;
    private String description;
    private String credentialValue; // Masked in responses
    private String credentialSecret; // Masked in responses
    private String additionalData; // JSON format
    private Boolean isPrimary;
    private Boolean isActive;

    private LocalDateTime expiresAt;
    private LocalDateTime lastUsedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}

