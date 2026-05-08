package com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EjoinDevStatus(
        String type,
        Integer seq,
        Integer expires,
        String mac,
        String ip,
        String ver,
        @JsonProperty("max-ports") Integer maxPorts,
        @JsonProperty("max-slot") Integer maxSlot,
        List<EjoinPortStatusResponse> status
) {}
