package com.zekiloni.george.platform.domain.model.gateway.gsm;

import com.zekiloni.george.platform.domain.model.gateway.Gateway;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GsmGateway extends Gateway {
    private GsmProvider provider;
    private List<GsmGatewaySlot> slot;

    public int getActivePortCount() {
        if (slot == null) return 0;
        return (int) slot.stream().filter(GsmGatewaySlot::getActive).count();
    }
}
