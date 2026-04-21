package com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EjoinPort(
        @JsonProperty("port")
        Integer port,

        @JsonProperty("slot")
        Integer slot
) {}