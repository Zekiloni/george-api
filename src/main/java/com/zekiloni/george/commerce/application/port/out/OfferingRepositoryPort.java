package com.zekiloni.george.commerce.application.port.out;

import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import com.zekiloni.george.commerce.domain.catalog.model.OfferingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OfferingRepositoryPort {
    Offering save(Offering offering);

    Optional<Offering> findById(String id);

    void deleteById(String id);

    Page<Offering> findAll(Pageable pageable);

    Optional<Offering> findByIdentifier(String identifier);

    List<Offering> findByStatus(OfferingStatus offeringStatus);
}

