package com.zekiloni.george.platform.domain.model.gateway.smtp;

import com.zekiloni.george.platform.domain.model.gateway.Gateway;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SmtpGateway extends Gateway {
    private SmtpGatewayProvider provider;
    private String host;
    private int port;
    private String fromDomain;
    private boolean useTls;
    private String adminUrl;
}
