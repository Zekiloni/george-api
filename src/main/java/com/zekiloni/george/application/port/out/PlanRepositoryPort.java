package com.zekiloni.george.application.port.out;


import com.zekiloni.george.domain.billing.model.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PlanRepositoryPort {
    Plan save(Plan plan);

    Optional<Plan> findById(String id);

    void deleteById(String id);

    Page<Plan> findAll(Pageable pageable);
}
