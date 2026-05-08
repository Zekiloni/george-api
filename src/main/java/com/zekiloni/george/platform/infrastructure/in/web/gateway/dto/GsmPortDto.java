package com.zekiloni.george.platform.infrastructure.in.web.gateway.dto;

import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmPortStatus;

import java.math.BigDecimal;

public record GsmPortDto(
        String port,
        GsmPortStatus status,
        String phoneNumber,
        String operator,
        int signalStrength,
        BigDecimal balance
) {}
