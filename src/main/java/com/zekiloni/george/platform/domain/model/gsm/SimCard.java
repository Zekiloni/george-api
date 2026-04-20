package com.zekiloni.george.platform.domain.model.gsm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimCard {
    private String id;
    private String phoneNumber;
    private String imsi;
    private String iccid;
    private Boolean active;
    private GsmGateway gateway;
}
