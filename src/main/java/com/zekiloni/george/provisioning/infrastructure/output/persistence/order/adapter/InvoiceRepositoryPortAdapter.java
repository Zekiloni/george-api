package com.zekiloni.george.provisioning.infrastructure.output.persistence.order.adapter;

import com.zekiloni.george.provisioning.application.port.out.InvoiceRepositoryPort;
import com.zekiloni.george.provisioning.domain.order.model.invoice.Invoice;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.order.mapper.InvoiceEntityMapper;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.order.repository.InvoiceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InvoiceRepositoryPortAdapter implements InvoiceRepositoryPort {
    private final InvoiceJpaRepository repository;
    private final InvoiceEntityMapper mapper;

    @Override
    public Optional<Invoice> findByOrderId(UUID orderId) {
        return repository.findByOrderId(orderId)
                .map(mapper::toDomain);
    }

    @Override
    public Invoice save(Invoice invoice) {
        return mapper.toDomain(repository.save(mapper.toEntity(invoice)));
    }
}
