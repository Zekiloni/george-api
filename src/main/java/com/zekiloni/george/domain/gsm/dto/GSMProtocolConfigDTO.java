package com.zekiloni.george.domain.gsm.dto;

import com.zekiloni.george.domain.gsm.entity.GSMProtocol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for GSMProtocolConfig entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GSMProtocolConfigDTO {

    private Long id;
    private Long gsmBoxConfigId;
    private GSMProtocol protocol;
    private Boolean isEnabled;
    private Integer priority;
    private Integer portNumber;
    private String settings; // JSON format
    private Long maxBandwidth;
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

