package com.zekiloni.george.platform.infrastructure.out.persistence.gateway.entity;

import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmProvider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "gsm_gateways")
@DiscriminatorValue("GSM")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
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
