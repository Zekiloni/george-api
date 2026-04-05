package com.zekiloni.george.platform.domain.gsm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity for logging GSM Box activities and events.
 * Tracks connections, disconnections, errors, and other activities.
 */
@Entity
@Table(name = "gsm_box_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GSMBoxLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gsm_box_config_id", nullable = false)
    private GSMBoxConfig gsmBoxConfig;

    @Column(nullable = false)
    private String eventType; // CONNECTED, DISCONNECTED, ERROR, SMS_SENT, CALL_RECEIVED, etc.

    @Column(columnDefinition = "TEXT")
    private String eventMessage;

    @Column(columnDefinition = "TEXT")
    private String errorDetails;

    @Column(name = "signal_strength")
    private Integer signalStrength;

    @Column(name = "battery_level")
    private Integer batteryLevel;

    @Column
    private String relatedPhone; // Phone number if applicable

    @Column(columnDefinition = "TEXT")
    private String relatedData; // JSON format for additional data

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

