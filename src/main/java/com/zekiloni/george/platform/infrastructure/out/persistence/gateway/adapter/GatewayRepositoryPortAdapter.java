package com.zekiloni.george.platform.infrastructure.out.persistence.gateway.adapter;

import com.zekiloni.george.platform.application.port.out.gateway.GatewayRepositoryPort;
import com.zekiloni.george.platform.domain.model.gatway.Gateway;
import com.zekiloni.george.platform.domain.model.gatway.GatewayType;
import com.zekiloni.george.platform.infrastructure.out.persistence.gateway.entity.GatewayEntity;
import com.zekiloni.george.platform.infrastructure.out.persistence.gateway.mapper.GatewayEntityMapper;
import com.zekiloni.george.platform.infrastructure.out.persistence.gateway.repository.GatewayJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Component
@RequiredArgsConstructor
@Slf4j
public class GatewayRepositoryPortAdapter implements GatewayRepositoryPort {

    private final GatewayJpaRepository gatewayJpaRepository;
    private final GatewayEntityMapper entityMapper;

    @Override
    @Transactional
    public Gateway save(Gateway gateway) {
        GatewayEntity entity = entityMapper.toEntity(gateway);
        return entityMapper.toDomain(gatewayJpaRepository.save(entity));
    }

    @Override
    @Transactional
    public Gateway update(Gateway gateway) {
        GatewayEntity entity = entityMapper.toEntity(gateway);
        return entityMapper.toDomain(gatewayJpaRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        gatewayJpaRepository.deleteById(UUID.fromString(id));
    }

    @Override
    public Optional<Gateway> findById(String id) {
        return gatewayJpaRepository.findById(UUID.fromString(id))
                .map(entityMapper::toDomain);
    }

    @Override
    public List<Gateway> findByType(GatewayType type) {
        return gatewayJpaRepository.findByType(type).stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Gateway> findAll() {
        return gatewayJpaRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public Page<Gateway> findAll(Pageable pageable) {
        return gatewayJpaRepository.findAll(pageable)
                .map(entityMapper::toDomain);
    }

    @Override
    public List<Gateway> findActive() {
        return gatewayJpaRepository.findByEnabled(true).stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(String id) {
        return gatewayJpaRepository.existsById(UUID.fromString(id));
    }
}
