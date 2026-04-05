package com.zekiloni.george.application.port.in;

import com.zekiloni.george.domain.billing.model.Offering;

public interface OfferingCreateUseCase {
    Offering create(Offering offeringCreateCommand);
}

