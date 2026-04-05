package com.zekiloni.george.provisioning.infrastructure.output.persistence.billing.adapter;

import com.zekiloni.george.provisioning.application.port.out.OfferingRepositoryPort;
import com.zekiloni.george.provisioning.domain.catalog.model.Offering;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.billing.mapper.OfferingEntityMapper;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.billing.repository.OfferingJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Page<Offering> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(mapper::toDomain);
    }
}

