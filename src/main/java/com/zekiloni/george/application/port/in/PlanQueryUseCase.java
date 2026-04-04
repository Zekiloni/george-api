package com.zekiloni.george.application.port.in;

import com.zekiloni.george.domain.billing.model.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PlanQueryUseCase {
    Optional<Plan> getById(String planId);
    Page<Plan> getAll(Pageable pageable);
}
