package com.zekiloni.george.application.port.out;


import com.zekiloni.george.domain.billing.model.Plan;

public interface PlanRepositoryPort {
    Plan save(Plan plan);

    Plan findById(String id);

    void deleteById(String id);
}
