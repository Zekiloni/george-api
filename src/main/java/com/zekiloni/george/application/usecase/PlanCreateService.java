package com.zekiloni.george.application.usecase;

import com.zekiloni.george.application.port.in.PlanCreateUseCase;
import com.zekiloni.george.domain.billing.model.Plan;
import org.springframework.stereotype.Service;

@Service
public class PlanCreateService implements PlanCreateUseCase {

    @Override
    public Plan create(Plan planCreateCommand) {
        return null;
    }
}
