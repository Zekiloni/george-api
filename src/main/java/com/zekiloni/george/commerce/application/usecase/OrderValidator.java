package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.common.domain.model.Characteristic;
import com.zekiloni.george.commerce.application.port.in.OfferingQueryUseCase;
import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import com.zekiloni.george.commerce.domain.catalog.model.OfferingStatus;
import com.zekiloni.george.commerce.domain.catalog.model.specification.characteristic.CharacteristicSpecification;
import com.zekiloni.george.commerce.domain.catalog.model.specification.characteristic.CharacteristicValueSpecification;
import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;
import com.zekiloni.george.commerce.domain.promotion.model.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Validates an order at create time:
 *  - non-empty items
 *  - offering exists, is ACTIVE
 *  - units within Offering's [minUnits, maxUnits]
 *  - characteristics: required cardinality, regex / value-spec constraints
 *
 * Side effect: resolves and sets the canonical Offering on each OrderItem so
 * downstream code (invoice, provisioning) doesn't have to re-load it.
 */
@Component
@RequiredArgsConstructor
public class OrderValidator {

    private final OfferingQueryUseCase offeringQueryUseCase;
    private final CouponService couponService;

    public void validate(Order order) {
        if (order.getItem() == null || order.getItem().isEmpty()) {
            throw new OrderValidationException("Order must contain at least one item");
        }
        order.getItem().forEach(this::validateItem);
        validateCoupon(order);
    }

    private void validateCoupon(Order order) {
        if (order.getCouponCode() == null || order.getCouponCode().isBlank()) return;
        Coupon coupon = couponService.findApplicable(order.getCouponCode())
                .orElseThrow(() -> new OrderValidationException(
                        "Coupon code '" + order.getCouponCode() + "' is invalid or expired"));
        boolean anyApplicable = order.getItem().stream()
                .anyMatch(it -> couponService.appliesToOffering(coupon, it.getOffering().getId()));
        if (!anyApplicable) {
            throw new OrderValidationException(
                    "Coupon '" + coupon.getCode() + "' does not apply to any offering in this order");
        }
    }

    private void validateItem(OrderItem item) {
        if (item.getOffering() == null || item.getOffering().getId() == null) {
            throw new OrderValidationException("Order item is missing an offering reference");
        }

        Offering offering = offeringQueryUseCase.getById(item.getOffering().getId())
                .orElseThrow(() -> new OrderValidationException(
                        "Offering not found: " + item.getOffering().getId()));

        item.setOffering(offering);

        validateOfferingActive(offering);
        validateUnits(offering, item);
        validateCharacteristics(offering, item);
    }

    private void validateOfferingActive(Offering offering) {
        if (offering.getStatus() != OfferingStatus.ACTIVE) {
            throw new OrderValidationException(
                    "Offering '" + nameOf(offering) + "' is not available for purchase");
        }
    }

    private void validateUnits(Offering offering, OrderItem item) {
        if (item.getUnits() == null || item.getUnits() <= 0) {
            throw new OrderValidationException(
                    "Units must be greater than 0 for '" + nameOf(offering) + "'");
        }
        if (offering.getMinUnits() != null && item.getUnits() < offering.getMinUnits()) {
            throw new OrderValidationException(
                    "Minimum " + offering.getMinUnits() + " units for '" + nameOf(offering) + "'");
        }
        if (offering.getMaxUnits() != null && item.getUnits() > offering.getMaxUnits()) {
            throw new OrderValidationException(
                    "Maximum " + offering.getMaxUnits() + " units for '" + nameOf(offering) + "'");
        }
    }

    private void validateCharacteristics(Offering offering, OrderItem item) {
        List<CharacteristicSpecification> specs = Optional.ofNullable(offering.getCharacteristicSpecification())
                .orElse(List.of());
        List<Characteristic> provided = Optional.ofNullable(item.getCharacteristic())
                .orElse(List.of());

        for (CharacteristicSpecification spec : specs) {
            int min = spec.getMinCardinality() == null ? 0 : spec.getMinCardinality();
            if (min > 0) {
                long count = provided.stream()
                        .filter(c -> spec.getName().equals(c.getName()) && c.getValue() != null)
                        .count();
                if (count < min) {
                    throw new OrderValidationException(
                            "Characteristic '" + spec.getName() + "' is required");
                }
            }
        }

        for (Characteristic c : provided) {
            CharacteristicSpecification spec = specs.stream()
                    .filter(s -> s.getName().equals(c.getName()))
                    .findFirst()
                    .orElseThrow(() -> new OrderValidationException(
                            "Unknown characteristic '" + c.getName() + "' for '" + nameOf(offering) + "'"));
            validateCharacteristicValue(spec, c);
        }
    }

    private void validateCharacteristicValue(CharacteristicSpecification spec, Characteristic c) {
        if (c.getValue() == null) return;

        if (spec.getRegex() != null && !spec.getRegex().isBlank()) {
            String stringValue = String.valueOf(c.getValue());
            try {
                if (!Pattern.matches(spec.getRegex(), stringValue)) {
                    throw new OrderValidationException(
                            "Characteristic '" + c.getName() + "' does not match required pattern");
                }
            } catch (PatternSyntaxException ignore) {
                // ignore malformed spec patterns rather than fail order
            }
        }

        List<CharacteristicValueSpecification> allowed = spec.getCharacteristicValueSpecification();
        if (allowed != null && !allowed.isEmpty()) {
            boolean ok = allowed.stream().anyMatch(v -> matches(v, c.getValue()));
            if (!ok) {
                throw new OrderValidationException(
                        "Value '" + c.getValue() + "' is not allowed for characteristic '" + c.getName() + "'");
            }
        }
    }

    private boolean matches(CharacteristicValueSpecification valueSpec, Object provided) {
        if (Objects.equals(valueSpec.getValue(), provided)) return true;

        BigDecimal value = asDecimal(provided);
        if (value == null) return false;
        BigDecimal from = asDecimal(valueSpec.getValueFrom());
        BigDecimal to = asDecimal(valueSpec.getValueTo());
        if (from != null && value.compareTo(from) < 0) return false;
        if (to != null && value.compareTo(to) > 0) return false;
        return from != null || to != null;
    }

    private BigDecimal asDecimal(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return new BigDecimal(n.toString());
        try {
            return new BigDecimal(o.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String nameOf(Offering offering) {
        return offering.getName() != null ? offering.getName() : offering.getId();
    }
}
