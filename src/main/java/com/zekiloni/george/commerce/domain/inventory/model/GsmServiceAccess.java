package com.zekiloni.george.commerce.domain.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GsmServiceAccess extends ServiceAccess {
    private String gatewayId;   // ID of the gateway this service uses
    private int port;           // Port number assigned to this GSM service access
}