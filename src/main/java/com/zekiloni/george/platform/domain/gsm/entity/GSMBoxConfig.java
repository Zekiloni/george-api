package com.zekiloni.george.platform.domain.gsm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a GSM Box Configuration.
 * Admins can manage multiple GSM boxes with different protocols and authentication methods.
 */
@Entity
@Table(name = "gsm_box_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"protocols", "authCredentials"})
public class GSMBoxConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String boxName;

    @Column(nullable = false)
    private String boxLabel;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GSMBoxType boxType;

    @Column(nullable = false)
    private String ipAddress;

    @Column
    private Integer port = 9000;

    @Column(nullable = false)
    private String imei; // International Mobile Equipment Identity

    @Column
    private String imsi; // International Mobile Subscriber Identity

    @Column
    private String phoneNumber;

    @Column
    private String manufacturer;

    @Column
    private String model;

    @Column
    private String firmwareVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GSMBoxStatus status = GSMBoxStatus.OFFLINE;

    @Column(name = "signal_strength")
    private Integer signalStrength; // 0-31, null if offline

    @Column(name = "battery_level")
    private Integer batteryLevel; // 0-100, null if not applicable

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_locked")
    private Boolean isLocked = false;

    @Column(name = "allow_sms")
    private Boolean allowSMS = true;

    @Column(name = "allow_calls")
    private Boolean allowCalls = true;

    @Column(name = "allow_data")
    private Boolean allowData = true;

    @Column(name = "max_concurrent_calls")
    private Integer maxConcurrentCalls = 1;

    @Column(name = "max_sms_per_minute")
    private Integer maxSMSPerMinute = 10;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    // Relationships
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "gsm_box_config_id")
    private List<GSMProtocolConfig> protocols = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "gsm_box_config_id")
    private List<GSMAuthCredential> authCredentials = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "gsm_box_config_id")
    @OrderBy("createdAt DESC")
    private List<GSMBoxLog> logs = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        lastSeenAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

