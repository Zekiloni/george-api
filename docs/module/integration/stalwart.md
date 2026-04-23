# Stalwart Gateway Integration

## Overview

Stalwart is an all-in-one email and SMS gateway platform that serves as the platform's messaging infrastructure for both email and SMS delivery. It provides a unified API for sending emails via SMTP and SMS via GSM/Telco networks, along with delivery tracking, bounce handling, and detailed logging. The Stalwart integration acts as the abstraction layer between the George platform and messaging providers, enabling clients to send both emails and SMS through a single, consistent interface.

Unlike postal (email-only), Stalwart supports SMS delivery through GSM box integration, allowing clients to manage both communication channels through one system.

## Architecture

### Integration Points

Stalwart integrates at multiple layers:

1. **SMTP Service**: Provides SMTP credentials and server details for email delivery
2. **SMS Service**: Provides GSM box/gateway credentials for SMS delivery
3. **Delivery Tracking**: Webhooks for delivery events (sent, bounced, opened, clicked)
4. **Rate Limiting**: API quota management and throttling
5. **Audit Logging**: Complete message delivery history

### Message Flow

```
Email Delivery Flow:
  1. Client has SmtpServiceAccess with SMTP credentials from Stalwart
  2. Client connects to Stalwart SMTP server (provided by Stalwart)
  3. Client authenticates with Stalwart credentials
  4. Client sends email message
  5. Stalwart validates message format and enforces characteristics (daily limit, recipients per message)
  6. Stalwart queues message for delivery
  7. Stalwart delivers message to recipient email server
  8. Recipient server accepts message (or bounces)
  9. Stalwart sends webhook to platform with delivery event
  10. Platform records delivery event in audit log
  
SMS Delivery Flow:
  1. Client has GsmServiceAccess with GSM gateway credentials from Stalwart
  2. Client calls platform SMS API with phone number and message
  3. Platform calls Stalwart SMS API with credentials and message payload
  4. Stalwart validates message (length, characters, recipients)
  5. Stalwart routes message to appropriate GSM network/provider
  6. GSM network delivers SMS to recipient phone
  7. Stalwart receives delivery receipt from GSM network
  8. Stalwart sends webhook with SMS delivery status
  9. Platform records SMS delivery event
```

## Configuration

### Stalwart Server Setup

Stalwart instance must be deployed and accessible:

```yaml
stalwart:
  # SMTP Configuration (for email delivery)
  smtp:
    server: "smtp.stalwart.example.com"
    port: 587
    use-tls: true
    api-url: "https://api.stalwart.example.com"
    api-key: "api-key-from-stalwart-dashboard"
    
  # SMS Configuration (for SMS delivery via GSM)
  sms:
    api-url: "https://api.stalwart.example.com"
    api-key: "api-key-from-stalwart-dashboard"
    
  # Webhook Configuration
  webhook:
    secret: "webhook-secret-for-signature-verification"
    endpoint: "https://platform.example.com/webhooks/stalwart"
    
  # Delivery Tracking
  tracking:
    enabled: true
    track-opens: true
    track-clicks: true
    
  # Rate Limiting (enforced by Stalwart, mirrored in characteristics)
  rate-limit:
    daily-emails: 1000
    daily-sms: 500
    concurrent-connections: 10
```

### Credential Management

Stalwart credentials (API key, webhook secret) are stored securely:

```
Stalwart Credentials (Vault)
├── api-key: "api-key-..." (used for API calls)
├── webhook-secret: "webhook-secret-..." (used to verify webhook signatures)
└── smtp-credentials: (generated per client, see SmtpServiceAccess)
```

### Provider Integrations

Stalwart itself integrates with multiple email and SMS providers:

**Email Providers**:
- Built-in SMTP (Stalwart's own infrastructure)
- Amazon SES
- SendGrid
- Mailgun
- Custom SMTP

**SMS Providers**:
- Twilio
- Vonage (Nexmo)
- African Telecom networks
- Asian GSM gateways
- Local telecom partnerships

Stalwart abstracts these providers, presenting a unified API to George platform.

## SMTP Service (Email Delivery)

### SMTP Credentials Provisioning

When a client purchases SMTP service:

```
Commerce Order → Inventory Provisioning → SmtpServiceAccess Created

SmtpServiceAccess {
  smtpServer: "smtp.stalwart.example.com"
  port: 587
  username: "client-uuid"
  password: "generated-secure-password"
  characteristics: {
    daily_send_limit: 1000,
    max_recipients_per_message: 500,
    max_attachment_size: "10MB",
    from_domain: "client.example.com" (if custom domain purchased)
  }
}
```

Credentials are generated during provisioning and stored encrypted in the database.

### SMTP Connection and Message Sending

Client integrates SMTP credentials into their email tool (Outlook, MailChimp, etc.):

```
SMTP Configuration Example:
  Host: smtp.stalwart.example.com
  Port: 587 (STARTTLS) or 465 (TLS)
  Username: {client-uuid}
  Password: {generated-password}
  From Address: {custom-domain}@client.example.com (if domain purchased)
```

Platform also provides SMTP client library for direct integration:

```java
SmtpServiceAccess smtpAccess = inventoryService.getSmtpAccess(tenantId);

MailMessage mail = new MailMessage()
    .to("recipient@example.com")
    .subject("Test Email")
    .body("This is a test email")
    .from(smtpAccess.getFromAddress());

// Validate against characteristics
if (currentDailySendCount < smtpAccess.getCharacteristic("daily_send_limit")) {
    mailService.send(mail, smtpAccess);
    incrementDailySendCount(tenantId);
} else {
    throw new QuotaExceededException("Daily email limit reached");
}
```

### SMTP Metrics and Quota Enforcement

Platform tracks daily email send counts per tenant:

```
EmailMetrics {
  tenantId: "tenant-001"
  date: "2024-01-15"
  emailsSent: 342,
  bounces: 12,
  complaints: 2,
  opens: 89,
  clicks: 34,
  dailyLimit: 1000
}
```

Before sending email, system checks:

```java
public boolean canSendEmail(String tenantId) {
    SmtpServiceAccess access = getSmtpAccess(tenantId);
    EmailMetrics metrics = getMetricsForToday(tenantId);
    
    // Check daily limit
    if (metrics.emailsSent >= access.getCharacteristic("daily_send_limit")) {
        return false;  // Limit reached
    }
    
    // Check validity period
    if (!access.isValid()) {
        return false;  // Subscription expired
    }
    
    return true;
}
```

## SMS Service (GSM/Telco Delivery)

### SMS Credentials and Gateway Setup

GSM boxes can be physical or virtual GSM gateways. When a client purchases GSM service:

```
Commerce Order → Inventory Provisioning → GsmServiceAccess Created

GsmServiceAccess extends ServiceAccess {
  gatewayType: "virtual" | "physical"  // Virtual = Stallwart API, Physical = on-premises
  
  # For Virtual Gateway (Stalwart API)
  apiUrl: "https://api.stalwart.example.com/sms"
  apiKey: "client-gsm-api-key"
  
  # For Physical Gateway
  gatewayHost: "192.168.1.100"
  gatewayPort: 9999
  gatewayUsername: "gateway-admin"
  gatewayPassword: "gateway-password"
  
  characteristics: {
    daily_sms_limit: 500,
    max_recipients_per_batch: 100,
    supported_countries: ["US", "CA", "UK", ...],
    message_encoding: "utf8" | "gsm7"  // GSM7 for ASCII, UTF8 for unicode
  }
}
```

### SMS Sending via API

Platform provides SMS sending via REST API:

```
POST /api/sms/send
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "phone_numbers": ["+1234567890", "+0987654321"],
  "message": "Hello, this is a test SMS",
  "campaign_id": "campaign-001",
  "send_at": "2024-01-15T14:30:00Z" (optional, for scheduled sending)
}
```

Backend handler:

```java
@PostMapping("/api/sms/send")
@PreAuthorize("hasRole('OPERATOR')")
public ResponseEntity<SmsSendResponse> sendSms(
    @RequestBody SmsSendRequest request,
    @RequestAttribute("tenant_id") String tenantId
) {
    // 1. Get client's GSM credentials
    GsmServiceAccess gsmAccess = inventoryService.getGsmAccess(tenantId);
    
    // 2. Check if service is active
    if (!gsmAccess.isActive()) {
        throw new ServiceAccessException("SMS service not active");
    }
    
    // 3. Validate message and recipients
    validateSmsRequest(request, gsmAccess);
    
    // 4. Check daily SMS quota
    SmsMetrics metrics = metricsService.getMetricsForToday(tenantId);
    if (metrics.smsSent + request.phoneNumbers.size() > 
        gsmAccess.getCharacteristic("daily_sms_limit")) {
        throw new QuotaExceededException("Daily SMS limit would be exceeded");
    }
    
    // 5. Send SMS via Stalwart API or gateway
    SmsSendResult result = stalwartSmsService.send(request, gsmAccess);
    
    // 6. Record SMS metadata for tracking
    recordSmsEvent(tenantId, request, result);
    
    // 7. Return result with batch ID for tracking
    return ResponseEntity.ok(new SmsSendResponse(
        batchId: result.getBatchId(),
        phoneCount: request.phoneNumbers.size(),
        estimatedCost: calculateCost(request, gsmAccess)
    ));
}
```

### SMS Delivery Tracking

Stalwart sends webhooks for SMS delivery status:

```
POST /webhooks/stalwart
X-Stalwart-Signature: {signature}
Content-Type: application/json

{
  "event_type": "sms_delivered",
  "timestamp": 1672531200,
  "batch_id": "sms-batch-001",
  "phone_number": "+1234567890",
  "message_id": "msg-001",
  "status": "delivered",
  "delivery_time": 1672531185,
  "network_id": "telco-network-id"
}
```

Webhook handler:

```java
@PostMapping("/webhooks/stalwart")
public ResponseEntity<Void> handleStalwartWebhook(
    @RequestBody String payload,
    @RequestHeader("X-Stalwart-Signature") String signature
) {
    // 1. Verify webhook signature
    if (!verifyStalwartSignature(payload, signature)) {
        logger.warn("Invalid Stalwart webhook signature");
        return ResponseEntity.status(401).build();
    }
    
    // 2. Parse webhook event
    StalwartEvent event = parseStalwartEvent(payload);
    
    // 3. Handle SMS delivery event
    if (event.eventType == "sms_delivered") {
        SmsRecord smsRecord = smsRepository.findByMessageId(event.messageId);
        smsRecord.deliveryStatus = "DELIVERED";
        smsRecord.deliveryTime = event.deliveryTime;
        smsRepository.save(smsRecord);
        
        // Update metrics
        metricsService.incrementDeliveredCount(smsRecord.tenantId);
    }
    
    // 4. Handle SMS failure
    if (event.eventType == "sms_failed") {
        smsRecord.deliveryStatus = "FAILED";
        smsRecord.failureReason = event.errorMessage;
        smsRepository.save(smsRecord);
        
        // Alert client if failure rate exceeds threshold
        if (failureRateExceeded(smsRecord.tenantId)) {
            notificationService.alertHighFailureRate(smsRecord.tenantId);
        }
    }
    
    return ResponseEntity.ok().build();
}
```

## Email Delivery Tracking

### Bounce and Complaint Handling

Stalwart tracks email bounces and complaints:

```
Email Bounce Events:
1. Hard bounce: Recipient doesn't exist, invalid address
   → Platform marks address as invalid, stops sending to it
   
2. Soft bounce: Mailbox full, server temporarily unavailable
   → Platform retries with exponential backoff
   
3. Complaint: Recipient marked email as spam
   → Platform removes email from list, alerts client
```

Webhook for bounce event:

```
POST /webhooks/stalwart
{
  "event_type": "email_bounced",
  "timestamp": 1672531200,
  "message_id": "msg-001",
  "recipient_email": "user@example.com",
  "bounce_type": "hard" | "soft" | "complaint",
  "bounce_reason": "User unknown",
  "bounce_subtype": "Mailbox doesn't exist"
}
```

### Open and Click Tracking

If tracking is enabled (characteristic: track_opens, track_clicks):

```
Email Open Event:
{
  "event_type": "email_opened",
  "timestamp": 1672531200,
  "message_id": "msg-001",
  "recipient_email": "user@example.com",
  "user_agent": "Mozilla/5.0...",
  "ip_address": "203.0.113.45"
}

Email Click Event:
{
  "event_type": "email_clicked",
  "timestamp": 1672531200,
  "message_id": "msg-001",
  "recipient_email": "user@example.com",
  "link_url": "https://campaign.example.com/offer",
  "user_agent": "Mozilla/5.0...",
  "ip_address": "203.0.113.45"
}
```

Platform aggregates these events for analytics:

```
EmailCampaignMetrics {
  campaignId: "campaign-001"
  recipientCount: 1000,
  deliveredCount: 987,
  bounceCount: 13,
  complaintCount: 2,
  openCount: 342,
  openRate: 34.6%,
  clickCount: 87,
  clickRate: 8.7%
}
```

## Integration with Page Builder

### SMS to Page Flow

When client distributes page tracking links via SMS:

```
Workflow:
  1. Client creates page in Page Builder
  2. Client publishes page, gets tracking link
  3. Client uses Leads table to segment SMS recipients
  4. Client calls SMS API with recipients and message:
     "Click here: https://platform.example.com/link/abc123"
  5. Platform sends SMS via Stalwart
  6. Recipient receives SMS with link
  7. Recipient clicks link, lands on page
  8. Platform initiates tracking session
  9. Tracking events recorded
  10. Client monitors session in console
```

### Email to Page Flow

Similarly, client can distribute page links via email:

```
Workflow:
  1. Client creates page in Page Builder
  2. Client exports leads from Leads table
  3. Client integrates SMTP credentials into email marketing tool (MailChimp, SendGrid, etc.)
  4. Client sends campaign with page link embedded in email
  5. Stalwart delivers emails via Stalwart SMTP
  6. Recipients receive email, click page link
  7. Platform tracks visitor session
```

Or client uses platform email API:

```java
EmailMessage message = new EmailMessage()
    .subject("Special Offer")
    .body("Click here to claim your offer: https://platform.example.com/link/abc123")
    .from("noreply@client.example.com");

// Send to lead list
List<String> emails = leadsService.getEmails(filteredLeadIds);
for (String email : emails) {
    message.to(email);
    emailService.send(message);
}
```

## Rate Limiting and Quota Management

### Daily Quotas

Characteristics enforce daily limits:

```
SmtpServiceAccess characteristics:
  - daily_send_limit: 1000 (max emails per day)
  - max_recipients_per_message: 500 (max recipients in single message)
  - max_attachment_size: 10MB
  
GsmServiceAccess characteristics:
  - daily_sms_limit: 500 (max SMS per day)
  - max_recipients_per_batch: 100 (max recipients in single batch)
  - supported_countries: [...] (restrict to certain countries)
```

Reset behavior:
- Daily quotas reset at midnight UTC
- Clients can see current usage in dashboard
- Clients receive warning at 80% quota, blocking at 100%

### Burst Limits

Stalwart enforces per-minute rate limits to prevent abuse:

```
Burst Limits:
  - SMTP: 10 messages per minute per connection
  - SMS API: 50 requests per minute
  - Both: Sliding window rate limiting
```

If limit exceeded, Stalwart returns 429 Too Many Requests. Platform should:
1. Implement exponential backoff for retries
2. Queue messages for delivery in off-peak hours
3. Alert client that rate limit was hit

```java
public void sendSmsWithRetry(String tenantId, SmsSendRequest request) {
    int maxRetries = 3;
    int retryDelay = 1000;  // 1 second
    
    for (int attempt = 0; attempt < maxRetries; attempt++) {
        try {
            stalwartSmsService.send(request);
            return;  // Success
        } catch (RateLimitException e) {
            if (attempt < maxRetries - 1) {
                Thread.sleep(retryDelay);
                retryDelay *= 2;  // Exponential backoff
            } else {
                throw e;  // Give up after max retries
            }
        }
    }
}
```

## Security and Privacy

### Webhook Signature Verification

All Stalwart webhooks are HMAC-SHA256 signed:

```
Signature = HMAC-SHA256(webhook_secret, request_body)
```

Verify before processing:

```java
private boolean verifyStalwartSignature(String payload, String receivedSignature) {
    String expectedSignature = HmacUtils.hmacSha256Hex(
        STALWART_WEBHOOK_SECRET,
        payload
    );
    return expectedSignature.equals(receivedSignature);
}
```

### Sensitive Data Handling

SMS and email content may contain sensitive information:
- Never log message body
- Log only metadata: recipient, message_id, status, timestamp
- Encrypt stored message content at rest
- Implement retention policies (delete after X days)

```java
// Log metadata only, not content
logger.info("Email sent: to={}, messageId={}, status={}", 
    maskEmail(recipient), 
    messageId, 
    status);

// Encrypt stored message body
@Column(columnDefinition = "VARBINARY")
@Convert(converter = EncryptionConverter.class)
private String messageContent;
```

### Opt-Out and Unsubscribe

For email campaigns, include unsubscribe link per regulations (CAN-SPAM, GDPR):

```
Unsubscribe Link:
  https://platform.example.com/unsubscribe?token={unique-unsubscribe-token}
```

When user clicks unsubscribe:
1. Mark email as opted-out in Leads table
2. Add to suppression list (Stalwart maintains)
3. Prevent future sends to that email
4. Send to Stalwart to update their suppression list

### GDPR and Privacy

SMS and email tracking must be compliant:
- Explicit consent required before sending marketing SMS/email
- Clear unsubscribe mechanism
- Data retention policies (typically 30-90 days for event logs)
- Right to be forgotten: Delete all records for a phone/email on request

## Monitoring and Alerting

### Key Metrics

Monitor Stalwart health:
- **Delivery Rate**: % of emails/SMS successfully delivered
- **Bounce Rate**: % of hard bounces
- **Complaint Rate**: % of spam complaints (goal: < 0.1%)
- **API Latency**: Time to send request to Stalwart
- **Queue Depth**: Number of pending messages in Stalwart

### Alerts

Alert conditions:
- Bounce rate > 5% (may indicate list quality issue)
- Complaint rate > 0.5% (spam complaints, may get IP blacklisted)
- API errors > 1% (Stalwart connectivity issue)
- Daily quota usage > 80% (warn client)
- Queue depth > 10,000 (message backlog, possible system overload)

### Dashboard

Client-facing analytics dashboard:

```
Email Metrics (Daily)
├── Sent: 234
├── Delivered: 230 (98.3%)
├── Bounced: 3 (1.3%)
├── Complaints: 1 (0.4%)
├── Opens: 87 (37.8% of delivered)
└── Clicks: 22 (9.6% of delivered)

SMS Metrics (Daily)
├── Sent: 156
├── Delivered: 154 (98.7%)
├── Failed: 2 (1.3%)
└── Remaining Quota: 344/500

Campaign Attribution
├── Emails from campaign_jan: 123 sent, 45 opens
├── SMS from campaign_jan: 78 sent, 42 delivered
└── Page conversions from campaign_jan: 12
```

## Troubleshooting

### Email Not Sending

**Symptom**: Email request accepted but message not received by recipient

**Common Causes**:
1. Invalid SMTP credentials
2. Sender domain not verified in Stalwart
3. Message rejected by recipient's mail server
4. Rate limit exceeded (429 response)

**Resolution**:
- Verify SMTP credentials in SmtpServiceAccess
- Check sender domain verification in Stalwart dashboard
- Review bounce/complaint webhooks
- Check rate limiting, implement backoff

### SMS Not Sending

**Symptom**: SMS API returns success but recipient doesn't receive message

**Common Causes**:
1. Invalid phone number format
2. Country not supported by GSM gateway
3. Recipient blocked or opted-out
4. Daily SMS limit reached

**Resolution**:
- Validate phone number format (E.164 format required)
- Check supported countries in GsmServiceAccess characteristics
- Check suppression list and opt-out status
- Monitor daily quota usage

### Webhook Not Being Received

**Symptom**: Delivery events not received, metrics not updating

**Common Causes**:
1. Webhook endpoint unreachable from Stalwart
2. Firewall blocking Stalwart IP range
3. Invalid webhook URL configured in Stalwart
4. SSL certificate validation failure

**Resolution**:
- Verify webhook URL is publicly accessible
- Check firewall rules, whitelist Stalwart IP range
- Test webhook endpoint: `curl -X POST https://webhook-url`
- Verify SSL certificate is valid (not self-signed for production)

---

This Stalwart Gateway integration documentation provides comprehensive coverage of email and SMS delivery, quota management, delivery tracking, security, and operational considerations. The unified gateway enables clients to manage both communication channels through a single, consistent platform interface.

