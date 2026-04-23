# BTCPay Integration

## Overview

BTCPay Server is an open-source cryptocurrency payment processor integrated into the George platform to enable Bitcoin and cryptocurrency-based billing for service subscriptions and one-time purchases. This integration handles payment acceptance, webhook notifications, and transaction reconciliation within the Commerce domain.

BTCPay provides a privacy-respecting, non-custodial payment solution where the merchant (George platform) retains full control over received funds without intermediaries.

## Architecture

### Integration Points

The BTCPay integration operates at the intersection of three system layers:

1. **Payment Gateway Abstraction**: Commerce domain abstracts payment processing through a PaymentProcessor interface, with BTCPayProcessor as the concrete implementation
2. **Order Processing**: When a client initiates checkout, the system creates an Order (PENDING status) and delegates payment handling to the BTCPayProcessor
3. **Webhook Listener**: BTCPay sends transaction status updates via webhooks, triggering ServiceAccess provisioning upon payment confirmation

### Payment Flow

```
Client Checkout Flow:
  1. Client selects offerings and initiates checkout
  2. Commerce calculates Order total (in USD or specified currency)
  3. Client selects Bitcoin as payment method
  4. BTCPayProcessor creates payment request with:
     - Order ID (external identifier)
     - Amount (converted to BTC at current rate)
     - Callback webhook URL
     - Timeout (e.g., 15 minutes)
  5. BTCPayProcessor receives payment URL from BTCPay
  6. Client is redirected to BTCPay invoice page
  7. Client scans QR code or copies address, sends BTC
  8. BTCPay confirms payment and triggers webhook
  9. Platform receives webhook, verifies signature
  10. Commerce marks Order as COMPLETED
  11. Inventory provisioning triggered
```

## Configuration

### BTCPay Server Setup

BTCPay Server instance must be deployed and accessible. Configuration requirements:

```yaml
btcpay:
  server-url: "https://btcpay.example.com"
  store-id: "store-id-from-btcpay-dashboard"
  api-key: "api-key-from-btcpay-dashboard"
  webhook-secret: "webhook-secret-for-signature-verification"
  network: "mainnet"  # or "testnet" for development
  confirmations-required: 1  # Minimum confirmations to consider payment final
```

### Credential Management

BTCPay credentials (API key, webhook secret) are stored in the platform's secure credential vault and must never be committed to source code. At runtime:

1. **API Key**: Used for authenticated requests to BTCPay API (create invoices, check status)
2. **Webhook Secret**: Used to verify webhook signature and prevent spoofing

### Currency and Exchange Rates

BTCPay invoices are always denominated in Bitcoin (BTC) but the platform accepts payment in USD or other fiat currencies. The conversion is handled as follows:

```
Order Amount (USD 99.99) 
  → Exchange rate lookup (1 BTC = $42,000)
  → BTC Amount = 99.99 / 42,000 = 0.00238 BTC
  → BTCPay invoice created with BTC amount
```

Exchange rates are fetched from:
- **Primary**: BTCPay's embedded rate provider (CoinGecko by default)
- **Fallback**: Cached rate from last successful lookup
- **Stale Data Handling**: If cached rate is > 1 hour old, a warning is logged and a fresh rate is fetched

### Timeouts and Invoice Expiration

BTCPay invoices have built-in expiration (configurable, typically 15 minutes). After expiration:
- Invoice can no longer be paid
- Order remains PENDING
- Client receives payment expiration notification
- Client can initiate new payment (generates new BTCPay invoice)

The platform also enforces a server-side timeout (e.g., 30 minutes) beyond which an unpaid order is automatically cancelled.

## Payment Processing

### Invoice Creation

When a client selects Bitcoin payment:

```java
// Commerce receives order
Order order = createOrder(items, tenantId);

// Client chooses Bitcoin payment
PaymentRequest paymentRequest = btcPayProcessor.createPaymentRequest(
    orderId: order.id,
    amount: order.totalAmount,
    currency: "USD",
    description: order.items.map(i -> i.offering.name).join(", "),
    notificationUrl: "https://platform.example.com/webhooks/btcpay",
    redirectUrl: "https://platform.example.com/orders/{orderId}/confirmation"
);

// Client is sent to BTCPay checkout
return redirectTo(paymentRequest.invoiceUrl);
```

### Webhook Handling

BTCPay sends webhook notifications when payment status changes:

```
POST /webhooks/btcpay

{
  "deliveryId": "...",
  "webhookId": "...",
  "timestamp": 1672531200,
  "storeId": "...",
  "type": "InvoicePaymentReceived",
  "data": {
    "invoiceId": "...",
    "orderId": "...",  // Our external Order ID
    "status": "Complete",
    "amountReceived": "0.00238",  // BTC received
    "amountPaid": "0.00238",      // BTC paid
    "rate": 42000,
    "cryptoCode": "BTC",
    "transactionId": "...",
    "confirmations": 1
  }
}
```

Platform webhook handler:

```java
@PostMapping("/webhooks/btcpay")
public ResponseEntity<Void> handleBTCPayWebhook(
    @RequestBody String payload,
    @RequestHeader("BTCPay-Sig") String signature
) {
    // 1. Verify webhook signature using webhook secret
    if (!verifySignature(payload, signature)) {
        return ResponseEntity.status(401).build();
    }
    
    // 2. Parse webhook data
    WebhookEvent event = parseWebhook(payload);
    
    // 3. Handle specific event types
    if (event.type == "InvoicePaymentReceived") {
        String orderId = event.data.orderId;
        Order order = orderRepository.findById(orderId);
        
        // 4. Verify payment matches order amount
        if (verifyPaymentAmount(event.data, order.totalAmount)) {
            // 5. Update order status
            order.status = OrderStatus.COMPLETED;
            orderRepository.save(order);
            
            // 6. Trigger provisioning
            inventoryService.provisionServiceAccess(order);
        }
    }
    
    // 7. Return 200 to acknowledge receipt
    return ResponseEntity.ok().build();
}
```

### Payment Status Tracking

Orders transition through states driven by BTCPay events:

```
Order: PENDING
  ↓ (InvoicePaymentReceived event)
Order: COMPLETED → triggers inventory provisioning
  ↓
ServiceAccess: ACTIVE (credentials/entitlements provisioned)
```

If payment fails or expires:

```
Order: PENDING
  ↓ (InvoiceExpired event OR 30-min server timeout)
Order: FAILED → client must retry with new payment
```

## Reconciliation and Auditing

### Transaction Recording

Every BTCPay interaction is recorded as a transaction event:

```
Transaction
├── id: UUID
├── orderId: string
├── btcPayInvoiceId: string
├── transactionId: string (on-chain)
├── amountUSD: Money
├── amountBTC: BigDecimal
├── exchangeRate: BigDecimal
├── status: enum (PENDING, CONFIRMED, FAILED, EXPIRED)
├── confirmations: int
├── webhookReceived: timestamp
├── webhookProcessed: timestamp
├── tenantId: string
└── metadata: JSON (raw webhook data)
```

Transaction records serve multiple purposes:
- **Audit Trail**: Complete history of payment attempts
- **Reconciliation**: Verify received BTC matches expected amounts
- **Reporting**: Financial statements and cryptocurrency exposure

### Confirmation Requirements

Bitcoin payments are considered final after reaching a configurable confirmation count:
- **Mainnet**: Default 1-6 confirmations (configurable, depends on payment amount)
- **Testnet**: Default 1 confirmation
- **High-Risk Orders**: May require additional confirmations (e.g., >1000 USD requires 6 confirmations)

Until confirmations are reached, ServiceAccess is provisioned conditionally (ACTIVE but marked PROVISIONAL).

### Orphaned Payment Detection

The system periodically reconciles:
- Payments received at BTCPay wallet that don't correspond to an Order (orphaned payments)
- Orders marked COMPLETED but not yet confirmed on-chain (provisional access)

Orphaned payments are investigated and manually assigned to orders if possible, or refunded if not claimable.

## Error Handling and Edge Cases

### Underpayment

If a client sends less BTC than the invoice amount:
- BTCPay marks invoice as UNDERPAID
- Platform receives InvoicePaymentReceived event with amountReceived < amountPaid
- Order remains PENDING
- Client is notified and asked to send remaining amount
- Invoice timeout still applies

### Overpayment

If a client sends more BTC than the invoice amount:
- BTCPay marks invoice as COMPLETE once minimum is reached
- Extra amount is tracked as overpayment
- Platform processes order for invoiced amount
- Overpayment is either returned (if integrated with wallet) or credited to client account for future purchases

### Network Congestion and Fee Dynamics

During high network congestion, transaction confirmation times may exceed expectations:
- Invoice timeout still applies (typically 15 minutes)
- Client is notified that confirmation may take longer
- Platform accepts provisional payment and activates services, but monitors for confirmation
- If transaction never confirms (transaction fee too low), manual refund is triggered

### BTCPay Webhook Retry Logic

BTCPay implements exponential backoff for failed webhook deliveries:
- Attempt 1: Immediately
- Attempt 2: After 1 minute
- Attempt 3: After 10 minutes
- Attempt 4: After 1 hour
- Continues for 24 hours

Platform must be idempotent: receiving the same webhook twice must not double-process or double-provision. Idempotency is achieved by:
1. Checking if transaction already exists before creating new entry
2. Using order ID as idempotency key
3. Logging duplicate webhook attempts

## Security Considerations

### Signature Verification

Every webhook is cryptographically signed using HMAC-SHA256:
```
Signature = HMAC-SHA256(webhook_secret, request_body)
```

Platform must verify signature before processing webhook:
```java
String expectedSignature = generateHmacSignature(payload, webhookSecret);
if (!expectedSignature.equals(receivedSignature)) {
    throw new InvalidWebhookSignatureException();
}
```

### API Key Security

BTCPay API key is used for sensitive operations:
- Create payment requests
- Query invoice status
- Access webhook status

API key must:
- Be rotated regularly (monthly or on compromise detection)
- Have minimal scopes (only necessary permissions, not full access)
- Never be logged or exposed in error messages
- Be stored encrypted in secure vault

### Payment Address Reuse Prevention

BTCPay generates unique payment addresses for each invoice. The platform must not reuse addresses across orders:
- If invoice is expired and order needs retry, a NEW BTCPay invoice is created
- Old invoice address is marked expired
- This prevents confusion and enhances privacy

## Testing and Development

### Testnet Configuration

For development and testing:

```yaml
btcpay:
  server-url: "https://testnet.demo.btcpayserver.org"
  network: "testnet"
  store-id: "test-store-id"
  api-key: "test-api-key"
```

Testnet allows:
- Free test Bitcoin from faucets (no real money)
- Testing full payment flow
- Testing webhook handling
- Testing error scenarios (double-spending, low fees, etc.)

### Mock Implementation

For unit tests, a mock BTCPayProcessor:

```java
@MockBean
private BTCPayProcessor btcPayProcessor;

@Test
public void testOrderCompletionOnBTCPayWebhook() {
    // Arrange
    when(btcPayProcessor.verifySignature(...)).thenReturn(true);
    PaymentRequest mockRequest = new PaymentRequest(...);
    when(btcPayProcessor.createPaymentRequest(...)).thenReturn(mockRequest);
    
    // Act
    webhookController.handleBTCPayWebhook(payload, signature);
    
    // Assert
    Order order = orderRepository.findById(orderId);
    assertEquals(OrderStatus.COMPLETED, order.status);
    assertTrue(serviceAccessRepository.findByOrderId(orderId).isPresent());
}
```

### Integration Testing

Full integration tests use testnet:
1. Create order via API
2. Get BTCPay invoice URL
3. Send test BTC to address (using testnet faucet)
4. Wait for webhook callback
5. Verify order marked COMPLETED
6. Verify ServiceAccess provisioned

## Monitoring and Alerting

### Key Metrics

- **Payment Success Rate**: % of orders that complete payment successfully
- **Average Confirmation Time**: Time from payment received to N confirmations
- **Webhook Delivery Latency**: Time from BTCPay event to platform webhook processing
- **Orphaned Payment Rate**: % of payments not matching an order

### Alerts

Alert conditions:
- Webhook delivery failure rate > 1% (indicates misconfiguration or network issue)
- Any webhook signature verification failure (indicates attack or misconfiguration)
- Exchange rate unavailable for > 30 minutes (may cause pricing issues)
- Pending orders > 1 hour without COMPLETED status (indicate payment issues)

### Logging

All BTCPay interactions are logged at INFO level:
```
[BTCPay] Created invoice: invoiceId=..., orderId=..., amountBTC=0.00238
[BTCPay] Webhook received: invoiceId=..., status=PaymentReceived, confirmations=1
[BTCPay] Order transitioned: orderId=..., PENDING→COMPLETED
```

Webhook signature verification failures logged at WARN level for investigation.

## Handling BTCPay Server Unavailability

If BTCPay server is temporarily unavailable:
1. Payment initiation fails gracefully
2. Client receives error: "Bitcoin payment temporarily unavailable. Please try another payment method."
3. Order remains creatable but cannot be paid with BTC
4. Alert is sent to platform administrators
5. Once BTCPay is restored, clients can retry

Alternative fallback: Offer other payment methods (credit card via Stripe, bank transfer) while BTC is unavailable.

---

This BTCPay integration documentation provides complete technical coverage of payment acceptance, webhook handling, reconciliation, security, and operational considerations. The non-custodial, privacy-respecting architecture ensures clients retain sovereignty over received cryptocurrency while the platform handles all transaction lifecycle management.

