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
@Table(name = "gsm_service_access")
public class GsmServiceAccessEntity extends ServiceAccessEntity {

    @Column(name = "port")
    private int port;
}
