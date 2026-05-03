package com.zekiloni.george.platform.infrastructure.out.persistence.gateway.entity;

import com.zekiloni.george.platform.domain.model.gateway.smtp.SmtpGatewayProvider;
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
}
