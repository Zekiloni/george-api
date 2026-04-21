package com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin.dto;

import java.util.List;

public record EjoinApiRequest(
        String baseUrl,
        String username,
        String password,
        List<EjoinPort> ports,       // nullable
        String portsPattern          // nullable
) {}