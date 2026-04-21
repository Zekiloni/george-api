package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.in.InvoiceQueryUseCase;
import com.zekiloni.george.commerce.application.port.out.InvoiceRepositoryPort;
import com.zekiloni.george.commerce.domain.order.model.invoice.Invoice;
import com.zekiloni.george.commerce.infrastructure.out.persistence.order.entity.InvoiceSpecification;
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
