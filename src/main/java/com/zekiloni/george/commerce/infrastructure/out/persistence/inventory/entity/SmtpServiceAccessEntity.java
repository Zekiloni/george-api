package com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "smtp_service_access")
public class SmtpServiceAccessEntity extends ServiceAccessEntity {
    private String smtpServer;
    private int port;
    private String username;
    private String password;
}
