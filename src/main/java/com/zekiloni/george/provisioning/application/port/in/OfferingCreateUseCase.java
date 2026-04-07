package com.zekiloni.george.provisioning.application.port.in;

import com.zekiloni.george.provisioning.domain.catalog.model.Offering;

public interface OfferingCreateUseCase {
    Offering create(Offering offeringCreate);
}

