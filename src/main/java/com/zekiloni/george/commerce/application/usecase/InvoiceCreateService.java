package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.commerce.application.port.in.InvoiceCreateUseCase;
import com.zekiloni.george.commerce.application.port.out.ExternalInvoicePort;
import com.zekiloni.george.commerce.application.port.out.InvoiceRepositoryPort;
import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;
import com.zekiloni.george.commerce.domain.order.model.invoice.Invoice;
import com.zekiloni.george.commerce.domain.order.model.invoice.InvoiceItem;
import com.zekiloni.george.commerce.domain.order.model.invoice.InvoiceStatus;
import com.zekiloni.george.commerce.domain.order.model.invoice.InvoiceType;
import com.zekiloni.george.commerce.domain.promotion.model.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceCreateService implements InvoiceCreateUseCase {
    public static final int INVOICE_EXPIRATION_TIME_MINUTES = 15;

    private final InvoiceRepositoryPort repository;
    private final ExternalInvoicePort externalInvoicePort;
    private final CouponService couponService;

    @Override
    public Invoice create(Order order) {
        Coupon coupon = resolveCoupon(order);
        Invoice invoice = buildInvoice(order, InvoiceType.NEW_PURCHASE, null, coupon);
        return finalize(order, invoice);
    }

    @Override
    public Invoice createRenewal(ServiceAccess access) {
        OrderItem item = access.getOrderItem();
        if (item == null || item.getOffering() == null) {
            throw new IllegalStateException(
                    "Cannot create renewal invoice for ServiceAccess " + access.getId() +
                            " — no order item / offering reference");
        }
        Order order = Order.builder()
                .id(null)  // renewal invoice isn't tied to a new Order
                .item(java.util.List.of(item))
                .tenantId(access.getTenantId())
                .build();
        Coupon coupon = null; // renewal coupons could be looked up here in future
        Invoice invoice = buildInvoice(order, InvoiceType.RENEWAL, access.getId(), coupon);
        return finalize(order, invoice);
    }

    private Invoice finalize(Order order, Invoice invoice) {
        Money total = invoice.getTotal();
        ExternalInvoicePort.ExternalInvoice externalInvoice = externalInvoicePort
                .createInvoice(order.getId(), "new",
                        total.getAmount().toPlainString(), total.getCurrency().getCurrencyCode());

        invoice.setInvoiceNumber(externalInvoice.invoiceId());
        invoice.setPaymentReference(externalInvoice.paymentUrl());
        return repository.save(invoice);
    }

    private Coupon resolveCoupon(Order order) {
        if (order.getCouponCode() == null || order.getCouponCode().isBlank()) return null;
        return couponService.findApplicable(order.getCouponCode()).orElse(null);
    }

    private Invoice buildInvoice(Order order, InvoiceType invoiceType, String serviceAccessId, Coupon coupon) {
        return Invoice.builder()
                .order(order)
                .invoiceType(invoiceType)
                .serviceAccessId(serviceAccessId)
                .issuedAt(OffsetDateTime.now())
                .dueAt(OffsetDateTime.now().plusMinutes(INVOICE_EXPIRATION_TIME_MINUTES))
                .status(InvoiceStatus.PENDING)
                .items(buildInvoiceItems(order, invoiceType, coupon))
                .build();
    }

    private List<InvoiceItem> buildInvoiceItems(Order order, InvoiceType invoiceType, Coupon coupon) {
        boolean couponApplies = coupon != null && couponService.appliesOnInvoice(coupon, invoiceType);
        return order.getItem().stream()
                .map(item -> buildInvoiceItem(item, invoiceType, couponApplies ? coupon : null))
                .toList();
    }

    private InvoiceItem buildInvoiceItem(OrderItem orderItem, InvoiceType invoiceType, Coupon coupon) {
        Offering offering = orderItem.getOffering();
        Money totalForItem = offering.priceFor(orderItem, invoiceType);
        if (coupon != null && couponService.appliesToOffering(coupon, offering.getId())) {
            totalForItem = couponService.apply(coupon, totalForItem);
        }
        int units = orderItem.getUnits() == null ? 1 : orderItem.getUnits();

        return InvoiceItem.builder()
                .offering(offering)
                .units(units)
                .totalAmount(totalForItem)
                .build();
    }
}
