package com.zekiloni.george.domain.gsm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing protocol configuration for a GSM Box.
 * Each GSM Box can support multiple protocols with different settings.
 */
@Entity
@Table(name = "gsm_protocol_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GSMProtocolConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gsm_box_config_id", nullable = false)
    private GSMBoxConfig gsmBoxConfig;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GSMProtocol protocol;

    @Column(name = "is_enabled")
    private Boolean isEnabled = true;

    @Column(name = "priority")
    private Integer priority = 0; // Higher priority protocols tried first

    @Column(name = "port_number")
    private Integer portNumber;

    @Column(columnDefinition = "TEXT")
    private String settings; // JSON format for protocol-specific settings

    @Column(name = "max_bandwidth")
    private Long maxBandwidth; // In KB/s, null means unlimited

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
}

