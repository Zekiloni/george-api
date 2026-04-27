package com.zekiloni.george.platform.domain.model.gatway.smtp;

import com.zekiloni.george.platform.domain.model.gatway.Gateway;
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
    private String id;
    private SmtpGatewayProvider provider;
    private String host;
    private int port;
    private String username;
    private String password;
}
