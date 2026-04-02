# GSM Box Configuration System - Quick Reference

## Entity Diagram

```
GSMBoxConfig (Main Container)
├── id, boxName (unique), boxLabel, boxType
├── ipAddress, port, imei (unique), imsi, phoneNumber
├── manufacturer, model, firmwareVersion
├── status, signalStrength, batteryLevel
├── isActive, isLocked, allowSMS, allowCalls, allowData
├── maxConcurrentCalls, maxSMSPerMinute
├── lastSeenAt, createdAt, updatedAt, createdBy, updatedBy
│
├─── 1:N ──> GSMProtocolConfig
│            ├── protocol (GSMProtocol enum)
│            ├── isEnabled, priority, portNumber
│            ├── settings (JSON), maxBandwidth
│            └── createdAt, updatedAt
│
├─── 1:N ──> GSMAuthCredential
│            ├── authType (GSMAuthType enum)
│            ├── credentialName, credentialValue (encrypted)
│            ├── credentialSecret (encrypted), additionalData (JSON)
│            ├── isPrimary, isActive, expiresAt, lastUsedAt
│            └── createdAt, updatedAt, createdBy
│
└─── 1:N ──> GSMBoxLog
             ├── eventType, eventMessage, errorDetails
             ├── signalStrength, batteryLevel
             ├── relatedPhone, relatedData (JSON)
             └── createdAt
```

## Enumerations

### GSMBoxType (7 types)
```
QUAD_BAND, DUAL_BAND, SINGLE_BAND
LTE_MODEM, 4G_ROUTER, 5G_MODEM
HYBRID
```

### GSMProtocol (12 types)
```
2G:     GSM, GPRS, EDGE
3G:     UMTS, HSPA
4G:     LTE, LTE_ADVANCED
5G:     NR
VoIP:   VOIP_SIP, VOIP_IAX, VOIP_H323
Legacy: SS7
```

### GSMBoxStatus (8 states)
```
ONLINE, OFFLINE, IDLE, BUSY, ERROR
MAINTENANCE, DISCONNECTED, INITIALIZING
```

### GSMAuthType (8 types)
```
BASIC, API_KEY, OAUTH2, BEARER_TOKEN
CERTIFICATE, HMAC, JWT, CUSTOM
```

## File Structure Created

```
Entities (8 files):
├── GSMBoxConfig.java
├── GSMProtocolConfig.java
├── GSMAuthCredential.java
├── GSMBoxLog.java
├── GSMBoxType.java (enum)
├── GSMProtocol.java (enum)
├── GSMBoxStatus.java (enum)
└── GSMAuthType.java (enum)

Repositories (4 files):
├── GSMBoxConfigRepository.java
├── GSMProtocolConfigRepository.java
├── GSMAuthCredentialRepository.java
└── GSMBoxLogRepository.java

DTOs (4 files):
├── GSMBoxConfigDTO.java
├── GSMProtocolConfigDTO.java
├── GSMAuthCredentialDTO.java
└── GSMBoxLogDTO.java

Database:
└── V2__init_gsm_box_config_schema.sql

Documentation:
├── GSM_BOX_CONFIG_DOCUMENTATION.md
└── GSM_BOX_CONFIG_QUICK_REFERENCE.md (this file)

Total: 20 Files Created
```

## Most Common Operations

### Create GSM Box
```java
GSMBoxConfig box = new GSMBoxConfig();
box.setBoxName("gsm_001");
box.setBoxLabel("Primary GSM Gateway");
box.setBoxType(GSMBoxType.QUAD_BAND);
box.setIpAddress("192.168.1.100");
box.setImei("358075045148345");
box.setPhoneNumber("+1234567890");
box.setMaxSMSPerMinute(20);
gsmBoxConfigRepository.save(box);
```

### Add LTE Protocol
```java
GSMProtocolConfig protocol = new GSMProtocolConfig();
protocol.setGsmBoxConfig(box);
protocol.setProtocol(GSMProtocol.LTE);
protocol.setIsEnabled(true);
protocol.setPriority(1);
gsmProtocolConfigRepository.save(protocol);
```

### Add API Key Auth
```java
GSMAuthCredential auth = new GSMAuthCredential();
auth.setGsmBoxConfig(box);
auth.setAuthType(GSMAuthType.API_KEY);
auth.setCredentialName("Live API Key");
auth.setCredentialValue(encryptedKey);
auth.setIsPrimary(true);
gsmAuthCredentialRepository.save(auth);
```

### Log Event
```java
GSMBoxLog log = new GSMBoxLog();
log.setGsmBoxConfig(box);
log.setEventType("SMS_SENT");
log.setEventMessage("SMS sent successfully");
log.setRelatedPhone("+1234567890");
gsmBoxLogRepository.save(log);
```

### Query Operations

#### Get Online Boxes
```java
List<GSMBoxConfig> online = gsmBoxConfigRepository
    .findByStatusAndIsActiveTrue(GSMBoxStatus.ONLINE);
```

#### Get By Type
```java
List<GSMBoxConfig> quadBand = gsmBoxConfigRepository
    .findByBoxType(GSMBoxType.QUAD_BAND);
```

#### Get By IMEI
```java
Optional<GSMBoxConfig> box = gsmBoxConfigRepository.findByImei(imei);
```

#### Get Enabled Protocols
```java
List<GSMProtocolConfig> protocols = gsmProtocolConfigRepository
    .findByGsmBoxConfigIdAndIsEnabledTrue(boxId);
```

#### Get Primary Auth
```java
Optional<GSMAuthCredential> auth = gsmAuthCredentialRepository
    .findByGsmBoxConfigIdAndIsPrimaryTrue(boxId);
```

#### Get Box Logs (Paginated)
```java
Page<GSMBoxLog> logs = gsmBoxLogRepository
    .findByGsmBoxConfigIdOrderByCreatedAtDesc(
        boxId, PageRequest.of(0, 50)
    );
```

## Database Tables

| Table | Purpose |
|-------|---------|
| gsm_box_configs | Main GSM box configuration |
| gsm_protocol_configs | Protocol settings per box |
| gsm_auth_credentials | Authentication credentials (encrypted) |
| gsm_box_logs | Activity and event logs |

## Key Features

| Feature | Implementation |
|---------|-----------------|
| Multiple Box Types | 7 types (2G to 5G) |
| Protocol Support | 12 protocols (GSM to 5G+VoIP) |
| Authentication | 8 methods (Basic to Custom) |
| Credential Management | Multi-credential support with expiry |
| Rate Limiting | SMS and call limits configurable |
| Access Control | Lock, enable/disable flags |
| Audit Trail | Created/updated by tracking |
| Activity Logging | Event logging with context |
| Encryption Ready | For credential storage |

## Common Queries

### Statistics
```java
// Total active boxes
long active = gsmBoxConfigRepository.countByIsActiveTrue();

// Online count
long online = gsmBoxConfigRepository.countByStatus(GSMBoxStatus.ONLINE);

// Error events count
long errors = gsmBoxLogRepository.countByGsmBoxConfigIdAndEventType(
    boxId, "ERROR"
);
```

### Maintenance
```java
// Get boxes expiring soon
LocalDateTime soon = LocalDateTime.now().plusDays(7);
List<GSMAuthCredential> expiring = gsmAuthCredentialRepository
    .findByGsmBoxConfigIdAndExpiresAtBefore(boxId, soon);

// Get unused credentials
LocalDateTime unused = LocalDateTime.now().minusDays(30);
List<GSMAuthCredential> old = gsmAuthCredentialRepository
    .findByGsmBoxConfigIdAndLastUsedAtBefore(boxId, unused);
```

### Troubleshooting
```java
// Get error logs
List<GSMBoxLog> errors = gsmBoxLogRepository
    .findByGsmBoxConfigIdAndEventTypeContains(boxId, "ERROR");

// Get latest log
Optional<GSMBoxLog> latest = gsmBoxLogRepository
    .findFirstByGsmBoxConfigIdOrderByCreatedAtDesc(boxId);

// Get signal loss events
List<GSMBoxLog> signalLoss = gsmBoxLogRepository
    .findByGsmBoxConfigIdAndEventType(boxId, "SIGNAL_LOST");
```

## Admin Operations

### Lock Box (Emergency)
```java
box.setIsLocked(true);
gsmBoxConfigRepository.save(box);
```

### Disable SMS
```java
box.setAllowSMS(false);
gsmBoxConfigRepository.save(box);
```

### Change Status
```java
box.setStatus(GSMBoxStatus.MAINTENANCE);
gsmBoxConfigRepository.save(box);
```

### Disable Credential
```java
auth.setIsActive(false);
gsmAuthCredentialRepository.save(auth);
```

### Update Protocol Priority
```java
protocol.setPriority(2);
gsmProtocolConfigRepository.save(protocol);
```

## Security Checklist

✅ Encrypt credential values  
✅ Mask credentials in API responses  
✅ Audit all credential changes  
✅ Track lastUsedAt for credentials  
✅ Support credential expiration  
✅ Implement rate limiting  
✅ Lock compromised boxes  
✅ Log all admin operations  
✅ Use TLS for connections  
✅ Validate IP addresses  

## Typical Admin Workflow

1. **Register Box**
   - Create GSMBoxConfig with device details
   - Set allowed operations (SMS, calls, data)

2. **Configure Protocols**
   - Add LTE as primary protocol
   - Add GSM as fallback
   - Set priority and bandwidth limits

3. **Setup Authentication**
   - Add primary API key
   - Add backup OAuth token
   - Set credential expiry

4. **Enable Operations**
   - Set isActive = true
   - Monitor logs
   - Adjust limits based on usage

5. **Ongoing Management**
   - Rotate credentials monthly
   - Monitor signal/battery
   - Update firmware
   - Review activity logs

## Performance Tips

1. Use indexes on frequently queried fields
2. Paginate log queries
3. Archive old logs periodically
4. Cache box status in Redis
5. Use connection pooling
6. Monitor query performance
7. Lazy load logs and credentials
8. Index on boxName, status, type, imei

## Recommended Indexes

Created in migration V2:
- `idx_gsm_box_name` - Name lookup
- `idx_gsm_box_status` - Status filtering
- `idx_gsm_box_type` - Type filtering
- `idx_gsm_box_imei` - IMEI lookup
- `idx_gsm_box_phone` - Phone lookup
- `idx_gsm_box_ip` - IP lookup
- `idx_gsm_auth_primary` - Primary credential
- `idx_gsm_log_event` - Event type search
- `idx_gsm_log_created` - Date range queries

## Error Handling

```java
try {
    // Validate IMEI not duplicate
    if (gsmBoxConfigRepository.existsByImei(imei)) {
        throw new DuplicateIMEIException();
    }
    
    // Check box name
    if (gsmBoxConfigRepository.existsByBoxName(name)) {
        throw new DuplicateBoxNameException();
    }
    
    gsmBoxConfigRepository.save(box);
} catch (DataIntegrityViolationException e) {
    // Handle database constraint violations
    log.error("Box configuration error", e);
}
```

## Related Documentation

- Full docs: **GSM_BOX_CONFIG_DOCUMENTATION.md**
- System index: **INDEX.md**
- Form config docs: **FORM_CONFIG_DOCUMENTATION.md**

---

**Total Entities Created**: 8  
**Total Repositories**: 4  
**Total DTOs**: 4  
**Database Tables**: 4  
**Indexes Created**: 11  

**Status**: ✅ Ready for Service Layer Implementation


