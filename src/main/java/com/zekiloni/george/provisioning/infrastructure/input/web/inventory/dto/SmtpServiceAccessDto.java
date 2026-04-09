package com.zekiloni.george.provisioning.infrastructure.input.web.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SmtpServiceAccessDto extends ServiceAccessDto {
    private String smtpServer;
    private int port;
    private String username;
    private String password;
}

