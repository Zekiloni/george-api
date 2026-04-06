package com.zekiloni.george.provisioning.application.usecase;

import com.zekiloni.george.provisioning.application.port.in.OfferingQueryUseCase;
import com.zekiloni.george.provisioning.application.port.out.OfferingRepositoryPort;
import com.zekiloni.george.provisioning.domain.catalog.model.Offering;
import com.zekiloni.george.provisioning.domain.catalog.model.OfferingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OfferingQueryService implements OfferingQueryUseCase {
    private final OfferingRepositoryPort repository;

    @Override
    public Optional<Offering> getById(String offeringId) {
        return repository.findById(offeringId);
    }

    @Override
    public Page<Offering> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public java.util.List<Offering> getActive() {
        return repository.findByStatus(OfferingStatus.ACTIVE);
    }
}

