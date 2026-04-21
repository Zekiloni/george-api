package com.zekiloni.george.commerce.application.port.in;

import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OfferingQueryUseCase {
    Optional<Offering> getById(String offeringId);
    Page<Offering> getAll(Pageable pageable);
    List<Offering> getActive();
}

