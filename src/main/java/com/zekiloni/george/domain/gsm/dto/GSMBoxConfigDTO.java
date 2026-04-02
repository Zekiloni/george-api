package com.zekiloni.george.domain.gsm.dto;

import com.zekiloni.george.domain.gsm.entity.GSMBoxStatus;
import com.zekiloni.george.domain.gsm.entity.GSMBoxType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for GSMBoxConfig entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GSMBoxConfigDTO {

    private Long id;
    private String boxName;
    private String boxLabel;
    private String description;
    private GSMBoxType boxType;
    private String ipAddress;
    private Integer port;
    private String imei;
    private String imsi;
    private String phoneNumber;
    private String manufacturer;
    private String model;
    private String firmwareVersion;
    private GSMBoxStatus status;
    private Integer signalStrength;
    private Integer batteryLevel;
    private Boolean isActive;
    private Boolean isLocked;
    private Boolean allowSMS;
    private Boolean allowCalls;
    private Boolean allowData;
    private Integer maxConcurrentCalls;
    private Integer maxSMSPerMinute;
    private String notes;

    private LocalDateTime lastSeenAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Relationships
    private List<GSMProtocolConfigDTO> protocols;
    private List<GSMAuthCredentialDTO> authCredentials;
}

