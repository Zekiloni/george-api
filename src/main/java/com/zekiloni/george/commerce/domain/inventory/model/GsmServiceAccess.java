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

    private List<String> allowedGsmBoxIds;     // koje box-ove ovaj klijent može da koristi
    private Integer maxSmsPerDay;
    private Integer maxSmsPerHour;
    private Integer maxConcurrentChannels;     // koliko SMS-ova istovremeno
    private List<String> allowedOperators;     // MTS, Yettel, A1
    private boolean smsSendingEnabled;
    private boolean smsReceivingEnabled;       // ako hoće i prijem
}