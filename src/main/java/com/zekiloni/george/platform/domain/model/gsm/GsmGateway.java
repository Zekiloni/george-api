package com.zekiloni.george.platform.domain.model.gsm;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GsmGateway {
    private String id;
    private String ipAddress;
    private int port;
    private GsmProvider provider;
    private int totalPort;
    private List<GsmGatewaySlot> slot;
    private String username;
    private String password;

    public int getActivePortCount() {
        if (slot == null) return 0;
        return (int) slot.stream().filter(GsmGatewaySlot::getActive).count();
    }
}
