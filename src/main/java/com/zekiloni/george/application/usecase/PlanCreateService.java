package com.zekiloni.george.application.usecase;

import com.zekiloni.george.application.port.in.PlanCreateUseCase;
import com.zekiloni.george.application.port.out.PlanRepositoryPort;
import com.zekiloni.george.domain.billing.model.Plan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanCreateService implements PlanCreateUseCase {
    private final PlanRepositoryPort repository;

    @Override
    public Plan create(Plan planCreate) {
        return repository.save(planCreate);
    }
}
