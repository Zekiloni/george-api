package com.zekiloni.george.commerce.domain.order.service.strategy;

import com.zekiloni.george.commerce.application.port.in.ServiceAccessCreateUseCase;
import com.zekiloni.george.commerce.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.commerce.application.port.out.gateway.GatewaySelectionPort;
import com.zekiloni.george.commerce.application.port.out.gateway.GsmProvisioningPort;
import com.zekiloni.george.commerce.application.port.out.gateway.GsmProvisioningPort.GsmPortInfo;
import com.zekiloni.george.commerce.application.usecase.ServiceUsageBootstrap;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.inventory.model.GsmServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceStatus;
import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;
import com.zekiloni.george.common.domain.model.Characteristic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class GsmProvisioningStrategy implements ProvisioningStrategy {

    static final String DEDICATED_PORT_CHARACTERISTIC = "dedicated_port";

    private final ServiceAccessCreateUseCase serviceAccessCreateUseCase;
    private final GatewaySelectionPort gatewaySelectionPort;
    private final GsmProvisioningPort gsmProvisioningPort;
    private final InventoryRepositoryPort inventoryRepository;
    private final ServiceUsageBootstrap usageBootstrap;

    @Override
    public ServiceSpecification getType() {
        return ServiceSpecification.GSM;
    }

    @Override
    public void provision(Order order, OrderItem orderItem) {
        log.info("Provisioning GSM service access for order item: {}", orderItem.getId());

        String gatewayId = gatewaySelectionPort.selectLeastLoadedGateway("GSM");
        int port = requiresDedicatedPort(orderItem) ? selectFreePort(gatewayId) : 0;

        GsmServiceAccess serviceAccess = GsmServiceAccess.builder()
                .validFrom(OffsetDateTime.now())
                .validTo(getValidTo(orderItem))
                .serviceSpecification(getType())
                .status(ServiceStatus.ACTIVE)
                .characteristic(orderItem.getCharacteristic())
                .orderItem(orderItem)
                .tenantId(order.getTenantId())
                .gatewayId(gatewayId)
                .port(port)
                .build();

        ServiceAccess saved = serviceAccessCreateUseCase.create(serviceAccess);
        usageBootstrap.initialize(saved, orderItem, order.getTenantId());

        gatewaySelectionPort.recordSuccess(gatewayId);
        gatewaySelectionPort.incrementConnectionCount(gatewayId);

        log.info("Provisioned GSM access on gateway {} port {} for tenant {}",
                gatewayId, port, order.getTenantId());
    }

    @Override
    public void deprovision(ServiceAccess access) {
        if (!(access instanceof GsmServiceAccess gsm)) {
            return;
        }
        if (gsm.getGatewayId() != null) {
            gatewaySelectionPort.decrementConnectionCount(gsm.getGatewayId());
        }
        log.info("Released GSM service access {} (gateway={}, port={})",
                gsm.getId(), gsm.getGatewayId(), gsm.getPort());
    }

    private boolean requiresDedicatedPort(OrderItem orderItem) {
        List<Characteristic> characteristics = orderItem.getCharacteristic();
        if (characteristics == null) {
            return false;
        }
        return characteristics.stream()
                .filter(c -> DEDICATED_PORT_CHARACTERISTIC.equals(c.getName()))
                .map(Characteristic::getValue)
                .anyMatch(GsmProvisioningStrategy::isTruthy);
    }

    private static boolean isTruthy(Object value) {
        if (value instanceof Boolean b) return b;
        if (value instanceof String s) return Boolean.parseBoolean(s);
        return false;
    }

    private int selectFreePort(String gatewayId) {
        Set<Integer> taken = inventoryRepository.findAllocatedGsmPorts(gatewayId);
        return gsmProvisioningPort.listGatewayPorts(gatewayId).stream()
                .filter(GsmPortInfo::available)
                .filter(p -> !taken.contains(p.port()))
                .max(Comparator.comparingInt(GsmPortInfo::signalStrength))
                .map(GsmPortInfo::port)
                .orElseThrow(() -> new IllegalStateException(
                        "No free dedicated GSM port available on gateway " + gatewayId));
    }
}
