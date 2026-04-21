package com.zekiloni.george.commerce.domain.order.model.invoice.event;

public enum InvoiceEventType {
    InvoiceCreated,
    InvoiceReceivedPayment,
    InvoiceProcessing,
    InvoiceExpired,
    InvoiceSettled,
    InvoiceInvalid,
    InvoicePaymentSettled,
    PaymentRequestCreated,
    PaymentRequestUpdated,
    PaymentRequestArchived,
    PaymentRequestStatusChanged,
    PayoutCreated,
    PayoutApproved,
    PayoutUpdated,
    SubscriberCreated,
    SubscriberCredited,
    SubscriberCharged,
    SubscriberActivated,
    SubscriberPhaseChanged,
    SubscriberDisabled,
    PaymentReminder,
    PlanStarted,
    SubscriberNeedUpgrade
}
