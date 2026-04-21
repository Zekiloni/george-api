package com.zekiloni.george.provisioning.infrastructure.out.persistence.catalog.adapter;

import com.zekiloni.george.provisioning.application.port.out.OfferingRepositoryPort;
import com.zekiloni.george.provisioning.domain.catalog.model.Offering;
import com.zekiloni.george.provisioning.domain.catalog.model.OfferingStatus;
import com.zekiloni.george.provisioning.infrastructure.out.persistence.catalog.mapper.OfferingEntityMapper;
import com.zekiloni.george.provisioning.infrastructure.out.persistence.catalog.repository.OfferingJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OfferingRepositoryPortAdapter implements OfferingRepositoryPort {
    private final OfferingJpaRepository jpaRepository;
    private final OfferingEntityMapper mapper;

    @Override
    public Offering save(Offering offering) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(offering)));
    }

    @Override
    public Optional<Offering> findById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return jpaRepository.findById(uuid)
                    .map(mapper::toDomain);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(UUID.fromString(id));
    }

    @Override
    public Page<Offering> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Offering> findByIdentifier(String identifier) {
        return Optional.empty();
    }

    @Override
    public List<Offering> findByStatus(OfferingStatus offeringStatus) {
        return jpaRepository.findAllByStatus(offeringStatus)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}

