package com.zekiloni.george.provisioning.application.port.in;

import com.zekiloni.george.provisioning.domain.billing.model.Offering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OfferingQueryUseCase {
    Optional<Offering> getById(String offeringId);
    Page<Offering> getAll(Pageable pageable);
}

