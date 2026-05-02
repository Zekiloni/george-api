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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

/**
 * Single point of truth for "this invoice is fully paid → make it real":
 *  - Marks invoice PAID + paidAt
 *  - For NEW_PURCHASE: provisions the order, marks order COMPLETED
 *  - For RENEWAL: extends ServiceAccess.validTo, restores from SUSPENDED if needed
 *  - Records coupon redemption (if any)
 *
 * Idempotent: re-deliveries (BTCPay can re-send) short-circuit on already-PAID status.
 * Both {@code InvoiceSettledEventHandler} and {@code InvoicePaymentSettledEventHandler}
 * funnel through {@link #settle(Invoice)}; whichever event arrives first does the work.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceSettlementService {

    private final InvoiceUpdateUseCase invoiceUpdateUseCase;
    private final OrderQueryUseCase orderQueryUseCase;
    private final OrderUpdateUseCase orderUpdateUseCase;
    private final OrderProvisioningService orderProvisioningService;
    private final ServiceAccessRenewalService renewalService;
    private final CouponService couponService;

    @Transactional
    public void settle(Invoice invoice) {
        if (invoice == null) return;
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            log.info("Invoice {} already PAID — ignoring redelivered settle", invoice.getId());
            return;
        }

        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidAt(OffsetDateTime.now());
        invoiceUpdateUseCase.update(invoice);
        log.info("Marked invoice {} as PAID (type={})", invoice.getId(), invoice.getInvoiceType());

        if (invoice.getInvoiceType() == InvoiceType.RENEWAL) {
            if (invoice.getServiceAccessId() != null) {
                renewalService.renew(invoice.getServiceAccessId());
            } else {
                log.warn("RENEWAL invoice {} has no serviceAccessId — cannot renew", invoice.getId());
            }
        } else {
            settleNewPurchase(invoice);
        }

        recordCouponRedemption(invoice);
    }

    private void settleNewPurchase(Invoice invoice) {
        Order order = invoice.getOrder();
        if (order == null || order.getId() == null) {
            log.warn("NEW_PURCHASE invoice {} has no associated order — skipping provisioning", invoice.getId());
            return;
        }
        // Re-load to ensure managed state for provisioning side effects.
        Order managed = orderQueryUseCase.getById(order.getId()).orElse(null);
        if (managed == null) {
            log.warn("Invoice {} references unknown order {}", invoice.getId(), order.getId());
            return;
        }
        orderProvisioningService.handle(managed);
        managed.setStatus(OrderStatus.COMPLETED);
        orderUpdateUseCase.update(managed);
    }

    private void recordCouponRedemption(Invoice invoice) {
        Order order = invoice.getOrder();
        if (order == null || order.getCouponCode() == null) return;
        Coupon coupon = couponService.findApplicable(order.getCouponCode()).orElse(null);
        if (coupon != null) couponService.recordRedemption(coupon);
    }
}
