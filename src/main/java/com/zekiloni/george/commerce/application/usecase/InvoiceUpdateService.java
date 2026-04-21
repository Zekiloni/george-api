package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.in.InvoiceUpdateUseCase;
import com.zekiloni.george.commerce.application.port.out.InvoiceRepositoryPort;
import com.zekiloni.george.commerce.domain.order.model.invoice.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceUpdateService implements InvoiceUpdateUseCase {
    private final InvoiceRepositoryPort repository;

    @Override
    public Invoice update(Invoice invoice) {
        return repository.save(invoice);
    }
}
