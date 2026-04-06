package com.zekiloni.george.provisioning.domain.inventory.model;


import lombok.experimental.SuperBuilder;

@SuperBuilder
public class SmtpServiceAccess extends ServiceAccess {
    private String smtpServer;
    private int port;
    private String username;
    private String password;
}
