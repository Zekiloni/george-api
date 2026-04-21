package com.zekiloni.george.provisioning.infrastructure.in.web.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SmtpServiceAccessDto extends ServiceAccessDto {
    private String smtpServer;
    private int port;
    private String username;
    private String password;
}

