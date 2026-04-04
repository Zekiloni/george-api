package com.zekiloni.george.application.port.in;

import com.zekiloni.george.domain.billing.model.Plan;

import java.util.Optional;

public interface PlanQueryUseCase {
    Optional<Plan> findById(String planId);
}
