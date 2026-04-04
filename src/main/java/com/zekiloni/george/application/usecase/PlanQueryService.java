package com.zekiloni.george.application.usecase;

import com.zekiloni.george.application.port.in.PlanCreateUseCase;
import com.zekiloni.george.application.port.in.PlanQueryUseCase;
import com.zekiloni.george.application.port.out.PlanRepositoryPort;
import com.zekiloni.george.domain.billing.model.Plan;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlanQueryService implements PlanQueryUseCase {
    private final PlanRepositoryPort repository;

    @Override
    public Optional<Plan> getById(String planId) {
        return repository.findById(planId);
    }

    @Override
    public Page<Plan> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
