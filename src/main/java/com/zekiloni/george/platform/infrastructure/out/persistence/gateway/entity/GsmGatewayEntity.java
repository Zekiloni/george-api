package com.zekiloni.george.platform.infrastructure.out.persistence.gateway.entity;

import com.zekiloni.george.platform.domain.model.gatway.gsm.GsmProvider;
import jakarta.persistence.*;

@Entity
@Table(name = "gsm_gateways")
@DiscriminatorValue("GSM")
public class GsmGatewayEntity extends GatewayEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private GsmProvider provider;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "port", nullable = false)
    private int port;

    @Column(name = "total_port")
    private int totalPort;

    @Column(name = "max_concurrent_channels")
    private int maxConcurrentChannels;
}
