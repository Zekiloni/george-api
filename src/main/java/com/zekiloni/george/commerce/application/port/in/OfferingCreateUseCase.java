package com.zekiloni.george.commerce.application.port.in;

import com.zekiloni.george.commerce.domain.catalog.model.Offering;

public interface OfferingCreateUseCase {
    Offering create(Offering offeringCreate);
}

