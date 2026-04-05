package com.zekiloni.george.provisioning.application.usecase;

import com.zekiloni.george.provisioning.application.port.in.OfferingCreateUseCase;
import com.zekiloni.george.provisioning.application.port.out.OfferingRepositoryPort;
import com.zekiloni.george.provisioning.domain.billing.model.Offering;
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

