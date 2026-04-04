package com.zekiloni.george.infrastructure.output.persistence.billing.adapter;

import com.zekiloni.george.application.port.out.PlanRepositoryPort;
import com.zekiloni.george.domain.billing.model.Plan;
import com.zekiloni.george.infrastructure.output.persistence.billing.mapper.PlanEntityMapper;
import com.zekiloni.george.infrastructure.output.persistence.billing.repository.PlanJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PlanRepositoryPortAdapter implements PlanRepositoryPort {
    private final PlanJpaRepository jpaRepository;
    private final PlanEntityMapper mapper;

    @Override
    public Plan save(Plan plan) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(plan)));
    }

    @Override
    public Optional<Plan> findById(String id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Page<Plan> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(mapper::toDomain);
    }
}
