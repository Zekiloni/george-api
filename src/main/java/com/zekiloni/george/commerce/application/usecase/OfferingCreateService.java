package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.in.OfferingCreateUseCase;
import com.zekiloni.george.commerce.application.port.out.OfferingRepositoryPort;
import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfferingCreateService implements OfferingCreateUseCase {
    private final OfferingRepositoryPort repository;

    @Override
    public Offering create(Offering offeringCreate) {
        return repository.save(offeringCreate);
    }
}

