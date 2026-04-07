package com.zekiloni.george.provisioning.domain.inventory.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SmtpServiceAccess extends ServiceAccess {
    private String smtpServer;
    private int port;
    private String username;
    private String password;
}
