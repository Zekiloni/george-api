package com.zekiloni.george.platform.infrastructure.out.persistence.gateway.entity;

import com.zekiloni.george.platform.domain.model.gatway.smtp.SmtpGatewayProvider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "smtp_gateways")
@DiscriminatorValue("SMTP")
public class SmtpGatewayEntity extends GatewayEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private SmtpGatewayProvider provider;

    @Column(name = "host", nullable = false)
    private String host;

    @Column(name = "port", nullable = false)
    private int port;

    @Column(name = "from_domain")
    private String fromDomain;

    @Column(name = "use_tls")
    private boolean useTls;
}
