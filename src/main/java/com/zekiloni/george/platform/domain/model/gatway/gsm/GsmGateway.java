package com.zekiloni.george.platform.domain.model.gatway.gsm;


import com.zekiloni.george.platform.domain.model.gatway.Gateway;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GsmGateway extends Gateway {
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
