package com.zekiloni.george.provisioning.application.port.out;

import com.zekiloni.george.provisioning.domain.catalog.model.Offering;
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
}

