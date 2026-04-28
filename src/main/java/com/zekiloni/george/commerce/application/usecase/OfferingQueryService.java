package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.in.OfferingQueryUseCase;
import com.zekiloni.george.commerce.application.port.out.OfferingRepositoryPort;
import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import com.zekiloni.george.commerce.domain.catalog.model.OfferingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OfferingQueryService implements OfferingQueryUseCase {
    private final OfferingRepositoryPort repository;

    @Override
    @Transactional(readOnly = true)
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

