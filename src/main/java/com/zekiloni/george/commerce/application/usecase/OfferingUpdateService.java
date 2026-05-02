package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.in.OfferingUpdateUseCase;
import com.zekiloni.george.commerce.application.port.out.OfferingRepositoryPort;
import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OfferingUpdateService implements OfferingUpdateUseCase {

    private final OfferingRepositoryPort repository;

    @Override
    @Transactional
    public Offering update(String id, Offering offering) {
        repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Offering " + id + " not found"));
        offering.setId(id);
        return repository.save(offering);
    }
}
