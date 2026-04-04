package com.zekiloni.george.application.port.in;

import com.zekiloni.george.domain.billing.model.Plan;

public interface PlanCreateUseCase {
    Plan create(Plan planCreateCommand);
}
