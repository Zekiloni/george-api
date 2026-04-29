package com.zekiloni.george.platform.application.usecase.gateway;

import com.zekiloni.george.platform.application.port.in.gateway.GatewayCreateUseCase;
import com.zekiloni.george.platform.application.port.in.gateway.GatewayQueryUseCase;
import com.zekiloni.george.platform.application.port.out.gateway.GatewayRepositoryPort;
import com.zekiloni.george.platform.domain.model.gatway.Gateway;
import com.zekiloni.george.platform.domain.model.gatway.GatewayType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Slf4j
public class GatewayManagementService implements GatewayCreateUseCase, GatewayQueryUseCase {
    private final GatewayRepositoryPort gatewayRepository;

    @Override
    @Transactional
    public Gateway create(Gateway gateway) {
        Gateway saved = gatewayRepository.save(gateway);
        log.info("Created gateway: id={}, type={}", saved.getId(), saved.getType());
        return saved;
    }

    @Override
    @Transactional
    public Gateway update(Gateway gateway) {
        Gateway updated = gatewayRepository.update(gateway);
        log.info("Updated gateway: id={}", updated.getId());
        return updated;
    }

    @Override
    @Transactional
    public void delete(String id) {
        gatewayRepository.deleteById(id);
        log.info("Deleted gateway: id={}", id);
    }

    @Override
    @Transactional
    public void enable(String id) {
        Gateway gateway = gatewayRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gateway not found: " + id));
        gateway.setEnabled(true);
        gatewayRepository.update(gateway);
        log.info("Enabled gateway: id={}", id);
    }

    @Override
    @Transactional
    public void disable(String id) {
        Gateway gateway = gatewayRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gateway not found: " + id));
        gateway.setEnabled(false);
        gatewayRepository.update(gateway);
        log.info("Disabled gateway: id={}", id);
    }

    @Override
    public Gateway findById(String id) {
        return gatewayRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gateway not found: " + id));
    }

    @Override
    public List<Gateway> findByType(GatewayType type) {
        return gatewayRepository.findByType(type);
    }

    @Override
    public List<Gateway> findAll() {
        return gatewayRepository.findAll();
    }

    @Override
    public Page<Gateway> findAll(Pageable pageable) {
        return gatewayRepository.findAll(pageable);
    }

    @Override
    public List<Gateway> findActive() {
        return gatewayRepository.findActive();
    }

    @Override
    public Gateway findLeastLoaded(GatewayType type) {
        return gatewayRepository.findByType(type).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No gateways found for type: " + type));
    }
}
