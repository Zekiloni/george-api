package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.in.InvoiceUpdateUseCase;
import com.zekiloni.george.commerce.application.port.in.OrderQueryUseCase;
import com.zekiloni.george.commerce.application.port.in.OrderUpdateUseCase;
import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.domain.order.model.OrderStatus;
import com.zekiloni.george.commerce.domain.order.model.invoice.Invoice;
import com.zekiloni.george.commerce.domain.order.model.invoice.InvoiceStatus;
import com.zekiloni.george.commerce.domain.order.model.invoice.InvoiceType;
import com.zekiloni.george.commerce.domain.order.service.OrderProvisioningService;
import com.zekiloni.george.commerce.domain.promotion.model.Coupon;
import com.zekiloni.george.commerce.domain.promotion.model.CouponDuration;
import com.zekiloni.george.commerce.domain.promotion.model.CouponStatus;
import com.zekiloni.george.commerce.domain.promotion.model.CouponType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceSettlementServiceTest {

    @Mock
    private InvoiceUpdateUseCase invoiceUpdateUseCase;
    @Mock
    private OrderQueryUseCase orderQueryUseCase;
    @Mock
    private OrderUpdateUseCase orderUpdateUseCase;
    @Mock
    private OrderProvisioningService orderProvisioningService;
    @Mock
    private ServiceAccessRenewalService renewalService;
    @Mock
    private CouponService couponService;

    private InvoiceSettlementService service;

    @BeforeEach
    void setUp() {
        service = new InvoiceSettlementService(
                invoiceUpdateUseCase,
                orderQueryUseCase,
                orderUpdateUseCase,
                orderProvisioningService,
                renewalService,
                couponService);
    }

    @Test
    void alreadyPaidShortCircuits() {
        Invoice invoice = baseInvoice(InvoiceType.NEW_PURCHASE);
        invoice.setStatus(InvoiceStatus.PAID);

        service.settle(invoice);

        verifyNoInteractions(invoiceUpdateUseCase, orderProvisioningService,
                orderUpdateUseCase, renewalService, couponService);
    }

    @Test
    void nullInvoiceIsSafe() {
        service.settle(null);
        verifyNoInteractions(invoiceUpdateUseCase);
    }

    @Test
    void newPurchaseMarksPaidProvisionsAndCompletesOrder() {
        Order order = Order.builder().id("order-1").build();
        Invoice invoice = baseInvoice(InvoiceType.NEW_PURCHASE);
        invoice.setOrder(order);
        when(orderQueryUseCase.getById("order-1")).thenReturn(Optional.of(order));

        service.settle(invoice);

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.PAID);
        assertThat(invoice.getPaidAt()).isNotNull();
        verify(invoiceUpdateUseCase).update(invoice);
        verify(orderProvisioningService).handle(order);
        verify(orderUpdateUseCase).update(order);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        verifyNoInteractions(renewalService);
    }

    @Test
    void renewalMarksPaidAndExtendsServiceAccess() {
        Invoice invoice = baseInvoice(InvoiceType.RENEWAL);
        invoice.setServiceAccessId("svc-1");

        service.settle(invoice);

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.PAID);
        verify(invoiceUpdateUseCase).update(invoice);
        verify(renewalService).renew("svc-1");
        verifyNoInteractions(orderProvisioningService, orderUpdateUseCase);
    }

    @Test
    void renewalWithoutServiceAccessIdSkipsRenewButStillMarksPaid() {
        Invoice invoice = baseInvoice(InvoiceType.RENEWAL);
        invoice.setServiceAccessId(null);

        service.settle(invoice);

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.PAID);
        verify(invoiceUpdateUseCase).update(invoice);
        verify(renewalService, never()).renew(any());
    }

    @Test
    void newPurchaseWithoutOrderSkipsProvisioning() {
        Invoice invoice = baseInvoice(InvoiceType.NEW_PURCHASE);
        invoice.setOrder(null);

        service.settle(invoice);

        verify(invoiceUpdateUseCase).update(invoice);
        verifyNoInteractions(orderProvisioningService, orderUpdateUseCase);
    }

    @Test
    void newPurchaseWithUnknownOrderSkipsProvisioning() {
        Invoice invoice = baseInvoice(InvoiceType.NEW_PURCHASE);
        invoice.setOrder(Order.builder().id("missing").build());
        when(orderQueryUseCase.getById("missing")).thenReturn(Optional.empty());

        service.settle(invoice);

        verify(invoiceUpdateUseCase).update(invoice);
        verify(orderProvisioningService, never()).handle(any());
    }

    @Test
    void couponRedemptionRecordedWhenPresent() {
        Order order = Order.builder().id("order-2").couponCode("SAVE20").build();
        Invoice invoice = baseInvoice(InvoiceType.NEW_PURCHASE);
        invoice.setOrder(order);
        when(orderQueryUseCase.getById("order-2")).thenReturn(Optional.of(order));

        Coupon coupon = Coupon.builder()
                .id("c1").code("SAVE20")
                .type(CouponType.PERCENT)
                .duration(CouponDuration.ONCE)
                .percent(new BigDecimal("0.20"))
                .status(CouponStatus.ACTIVE)
                .build();
        when(couponService.findApplicable("SAVE20")).thenReturn(Optional.of(coupon));

        service.settle(invoice);

        verify(couponService).recordRedemption(coupon);
    }

    @Test
    void couponRedemptionSkippedWhenOrderHasNoCode() {
        Order order = Order.builder().id("order-3").build(); // no couponCode
        Invoice invoice = baseInvoice(InvoiceType.NEW_PURCHASE);
        invoice.setOrder(order);
        when(orderQueryUseCase.getById("order-3")).thenReturn(Optional.of(order));

        service.settle(invoice);

        verify(couponService, never()).recordRedemption(any());
    }

    @Test
    void couponRedemptionSkippedWhenCouponNotApplicable() {
        Order order = Order.builder().id("order-4").couponCode("EXPIRED").build();
        Invoice invoice = baseInvoice(InvoiceType.NEW_PURCHASE);
        invoice.setOrder(order);
        when(orderQueryUseCase.getById("order-4")).thenReturn(Optional.of(order));
        when(couponService.findApplicable("EXPIRED")).thenReturn(Optional.empty());

        service.settle(invoice);

        verify(couponService, never()).recordRedemption(any());
    }

    @Test
    void renewalWithCouponCodeRecordsRedemption() {
        Order order = Order.builder().id("order-5").couponCode("LOYAL").build();
        Invoice invoice = baseInvoice(InvoiceType.RENEWAL);
        invoice.setOrder(order);
        invoice.setServiceAccessId("svc-2");

        Coupon coupon = Coupon.builder()
                .id("c2").code("LOYAL")
                .type(CouponType.PERCENT)
                .duration(CouponDuration.FOREVER)
                .percent(new BigDecimal("0.10"))
                .status(CouponStatus.ACTIVE)
                .build();
        when(couponService.findApplicable("LOYAL")).thenReturn(Optional.of(coupon));

        service.settle(invoice);

        verify(renewalService).renew("svc-2");
        verify(couponService).recordRedemption(coupon);
    }

    private static Invoice baseInvoice(InvoiceType type) {
        Invoice invoice = Invoice.builder().build();
        invoice.setId("inv-1");
        invoice.setInvoiceType(type);
        invoice.setStatus(InvoiceStatus.PENDING);
        return invoice;
    }
}
