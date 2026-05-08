package com.zekiloni.george.platform.domain.model.gateway.gsm;

import java.math.BigDecimal;

public record GsmPort(
        String id,
        GsmPortStatus status,
        String phoneNumber,
        String operator,
        int signalStrength,
        BigDecimal balance
) {}
