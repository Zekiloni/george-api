package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.commerce.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.catalog.model.TierMode;
import com.zekiloni.george.commerce.domain.inventory.model.PageServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceStatus;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;
import com.zekiloni.george.commerce.domain.order.service.strategy.ProvisioningStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceAccessRenewalServiceTest {

    @Mock
    private InventoryRepositoryPort repository;
    @Mock
    private ProvisioningStrategy pageStrategy;

    private ServiceAccessRenewalService service;

    @BeforeEach
    void setUp() {
        when(pageStrategy.getType()).thenReturn(ServiceSpecification.PAGE);
        service = new ServiceAccessRenewalService(repository, List.of(pageStrategy));
    }

    @Test
    void extendsValidToFromExistingValidToWhenInFuture() {
        OffsetDateTime futureValidTo = OffsetDateTime.now().plusDays(2);
        ServiceAccess access = active("a1", futureValidTo, monthlyOffering(1));
        when(repository.findById("a1")).thenReturn(Optional.of(access));

        service.renew("a1");

        ArgumentCaptor<ServiceAccess> captor = ArgumentCaptor.forClass(ServiceAccess.class);
        verify(repository).save(captor.capture());
        ServiceAccess saved = captor.getValue();
        // 1-month extension from existing validTo (which is 2 days from now)
        assertThat(saved.getValidTo()).isEqualTo(futureValidTo.plusMonths(1));
        assertThat(saved.getStatus()).isEqualTo(ServiceStatus.ACTIVE);
        assertThat(saved.getRenewalCount()).isEqualTo(1);
    }

    @Test
    void extendsFromNowWhenValidToInPast() {
        OffsetDateTime pastValidTo = OffsetDateTime.now().minusDays(5);
        ServiceAccess access = active("a2", pastValidTo, monthlyOffering(1));
        when(repository.findById("a2")).thenReturn(Optional.of(access));

        OffsetDateTime before = OffsetDateTime.now();
        service.renew("a2");
        OffsetDateTime after = OffsetDateTime.now();

        ArgumentCaptor<ServiceAccess> captor = ArgumentCaptor.forClass(ServiceAccess.class);
        verify(repository).save(captor.capture());
        OffsetDateTime newValidTo = captor.getValue().getValidTo();
        // Renewed from "now": should fall between (before+1mo) and (after+1mo)
        assertThat(newValidTo).isAfterOrEqualTo(before.plusMonths(1).minusSeconds(1));
        assertThat(newValidTo).isBeforeOrEqualTo(after.plusMonths(1).plusSeconds(1));
    }

    @Test
    void renewSuspendedRestoresActiveAndCallsResume() {
        OffsetDateTime past = OffsetDateTime.now().minusDays(1);
        ServiceAccess access = suspended("a3", past, monthlyOffering(1));
        when(repository.findById("a3")).thenReturn(Optional.of(access));

        service.renew("a3");

        verify(pageStrategy, times(1)).resume(access);
        ArgumentCaptor<ServiceAccess> captor = ArgumentCaptor.forClass(ServiceAccess.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(ServiceStatus.ACTIVE);
        assertThat(captor.getValue().getSuspendedAt()).isNull();
    }

    @Test
    void renewActiveDoesNotCallResume() {
        ServiceAccess access = active("a4", OffsetDateTime.now().plusDays(2), monthlyOffering(1));
        when(repository.findById("a4")).thenReturn(Optional.of(access));

        service.renew("a4");

        verify(pageStrategy, never()).resume(access);
    }

    @Test
    void renewTerminatedIsNoOp() {
        ServiceAccess access = active("a5", OffsetDateTime.now(), monthlyOffering(1));
        access.setStatus(ServiceStatus.TERMINATED);
        when(repository.findById("a5")).thenReturn(Optional.of(access));

        service.renew("a5");

        verify(repository, never()).save(access);
        verify(pageStrategy, never()).resume(access);
    }

    @Test
    void incrementsRenewalCount() {
        ServiceAccess access = active("a6", OffsetDateTime.now().plusDays(1), monthlyOffering(1));
        access.setRenewalCount(2);
        when(repository.findById("a6")).thenReturn(Optional.of(access));

        service.renew("a6");

        ArgumentCaptor<ServiceAccess> captor = ArgumentCaptor.forClass(ServiceAccess.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getRenewalCount()).isEqualTo(3);
    }

    @Test
    void multiUnitItemExtendsByUnitsTimesIntervalCount() {
        // 6 units × 1 month → +6 months
        OffsetDateTime base = OffsetDateTime.now().plusDays(2);
        ServiceAccess access = active("a7", base, monthlyOffering(6));
        when(repository.findById("a7")).thenReturn(Optional.of(access));

        service.renew("a7");

        ArgumentCaptor<ServiceAccess> captor = ArgumentCaptor.forClass(ServiceAccess.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getValidTo()).isEqualTo(base.plusMonths(6));
    }

    @Test
    void quotaBasedOfferingDoesNotExtendValidTo() {
        // No timeUnit → validTo is unchanged
        OffsetDateTime original = OffsetDateTime.now().plusDays(2);
        Offering quota = Offering.builder()
                .id("o-quota")
                .name("Leads")
                .unitAmount(Money.of("USD", new BigDecimal("5.00")))
                .timeUnit(null)
                .intervalCount(1)
                .minUnits(1)
                .tierMode(TierMode.NONE)
                .build();
        OrderItem item = OrderItem.builder().offering(quota).units(50).build();
        ServiceAccess access = PageServiceAccess.builder()
                .id("a8")
                .serviceSpecification(ServiceSpecification.PAGE)
                .validTo(original)
                .status(ServiceStatus.ACTIVE)
                .orderItem(item)
                .build();
        when(repository.findById("a8")).thenReturn(Optional.of(access));

        service.renew("a8");

        ArgumentCaptor<ServiceAccess> captor = ArgumentCaptor.forClass(ServiceAccess.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getValidTo()).isEqualTo(original);
    }

    @Test
    void unknownIdThrows() {
        when(repository.findById("nope")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.renew("nope"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // --- helpers ---

    private static ServiceAccess active(String id, OffsetDateTime validTo, Offering offering) {
        OrderItem item = OrderItem.builder().offering(offering).units(offering.getMinUnits()).build();
        return PageServiceAccess.builder()
                .id(id)
                .serviceSpecification(ServiceSpecification.PAGE)
                .validTo(validTo)
                .status(ServiceStatus.ACTIVE)
                .orderItem(item)
                .build();
    }

    private static ServiceAccess suspended(String id, OffsetDateTime validTo, Offering offering) {
        ServiceAccess access = active(id, validTo, offering);
        access.setStatus(ServiceStatus.SUSPENDED);
        access.setSuspendedAt(OffsetDateTime.now().minusHours(2));
        return access;
    }

    private static Offering monthlyOffering(int units) {
        return Offering.builder()
                .id("off-monthly")
                .name("Monthly")
                .unitAmount(Money.of("USD", new BigDecimal("20.00")))
                .timeUnit(ChronoUnit.MONTHS)
                .intervalCount(1)
                .minUnits(units)
                .tierMode(TierMode.NONE)
                .build();
    }
}
