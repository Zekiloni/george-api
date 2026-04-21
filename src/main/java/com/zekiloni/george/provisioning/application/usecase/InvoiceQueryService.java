package com.zekiloni.george.provisioning.application.usecase;

import com.zekiloni.george.provisioning.application.port.in.InvoiceQueryUseCase;
import com.zekiloni.george.provisioning.application.port.out.InvoiceRepositoryPort;
import com.zekiloni.george.provisioning.domain.order.model.invoice.Invoice;
import com.zekiloni.george.provisioning.infrastructure.out.persistence.order.entity.InvoiceSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceQueryService implements InvoiceQueryUseCase {
    private final InvoiceRepositoryPort repositoryPort;

    @Override
    public Optional<Invoice> getByOrderId(String orderId) {
        return repositoryPort.findByOrderId(UUID.fromString(orderId));
    }

    @Override
    public Page<Invoice> getAll(Pageable pageable, InvoiceSpecification specification) {
        return repositoryPort.findAll(pageable, specification);
    }
}
