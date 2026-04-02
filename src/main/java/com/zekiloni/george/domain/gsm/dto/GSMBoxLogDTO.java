package com.zekiloni.george.domain.gsm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for GSMBoxLog entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GSMBoxLogDTO {

    private Long id;
    private Long gsmBoxConfigId;
    private String boxName; // For reference
    private String eventType;
    private String eventMessage;
    private String errorDetails;
    private Integer signalStrength;
    private Integer batteryLevel;
    private String relatedPhone;
    private String relatedData; // JSON format

    private LocalDateTime createdAt;
}

