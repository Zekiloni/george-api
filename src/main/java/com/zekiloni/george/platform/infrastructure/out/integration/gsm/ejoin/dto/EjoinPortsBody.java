package com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record EjoinPortsBody(
        @JsonProperty("ports")
        List<EjoinPort> ports,

        @JsonProperty("ports_pattern")
        String portsPattern
) {}