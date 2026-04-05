package com.zekiloni.george.application.usecase;

import com.zekiloni.george.application.port.in.OfferingCreateUseCase;
import com.zekiloni.george.application.port.out.OfferingRepositoryPort;
import com.zekiloni.george.domain.billing.model.Offering;
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

